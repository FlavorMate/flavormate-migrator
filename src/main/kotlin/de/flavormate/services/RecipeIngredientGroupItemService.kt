/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.mappingTables.LocalizedUnitsTable
import de.flavormate.models.v2.V2Ingredient
import de.flavormate.models.v2.V2Unit
import de.flavormate.models.v3.V3RecipeIngredientGroupItem
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeIngredientGroupItemService {

  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Ingredient.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipe ingredient group items ($i..${i + BATCH_SIZE}/$count)")
      val groups =
        transaction(SourceDbInstance.getInstance()) {
          val ingredients = V2Ingredient.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
          ingredients
            .filter { it[V2Ingredient.unit] != null }
            .forEach { ingredient ->
              val unit =
                V2Unit.selectAll()
                  .where { V2Unit.id.eq(ingredient[V2Ingredient.unit]!!) }
                  .map { it[V2Unit.label] }
                  .first()

              ingredient[V2Ingredient.label] = "$unit ${ingredient[V2Ingredient.label]}"
            }
          ingredients
        }

      transaction(TargetDbInstance.getInstance()) {
        V3RecipeIngredientGroupItem.batchInsert(groups) { group ->
          val id =
            DynamicMappingTable.getOrCreateId(
              group[V2Ingredient.id],
              DynamicMappingTable.recipeIngredientGroupItem,
            )
          val groupId =
            DynamicMappingTable.getId(
              group[V2Ingredient.group],
              DynamicMappingTable.recipeIngredientGroup,
            )

          val index =
            DynamicMappingTable.getOrCreateIndex(
              groupId,
              DynamicMappingTable.recipeIngredientGroupItemIndex,
            )

          this[V3RecipeIngredientGroupItem.id] = id
          this[V3RecipeIngredientGroupItem.index] = index
          this[V3RecipeIngredientGroupItem.amount] = group[V2Ingredient.amount]
          this[V3RecipeIngredientGroupItem.label] = group[V2Ingredient.label].trim()
          this[V3RecipeIngredientGroupItem.group] = groupId
          this[V3RecipeIngredientGroupItem.unit] =
            group[V2Ingredient.unitLocalized]?.let { LocalizedUnitsTable.getId(it) }
          this[V3RecipeIngredientGroupItem.nutrition] =
            group[V2Ingredient.nutrition]?.let {
              DynamicMappingTable.getId(it, DynamicMappingTable.recipeNutrition)
            }
        }
      }
    }
  }
}
