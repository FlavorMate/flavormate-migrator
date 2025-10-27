/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.extensions.trimToNull
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2IngredientGroup
import de.flavormate.models.v3.V3RecipeIngredientGroup
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeIngredientGroupService {

  fun migrate() {
    val count =
      transaction(SourceDbInstance.getInstance()) { V2IngredientGroup.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipe ingredient groups ($i..${i + BATCH_SIZE}/$count)")
      val groups =
        transaction(SourceDbInstance.getInstance()) {
          V2IngredientGroup.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3RecipeIngredientGroup.batchInsert(groups) { group ->
          val id =
            DynamicMappingTable.getOrCreateId(
              group[V2IngredientGroup.id],
              DynamicMappingTable.recipeIngredientGroup,
            )
          val recipeId =
            DynamicMappingTable.getId(group[V2IngredientGroup.recipe], DynamicMappingTable.recipe)

          val index =
            DynamicMappingTable.getOrCreateIndex(
              recipeId,
              DynamicMappingTable.recipeIngredientGroupIndex,
            )

          this[V3RecipeIngredientGroup.id] = id
          this[V3RecipeIngredientGroup.label] = group[V2IngredientGroup.label]?.trimToNull()
          this[V3RecipeIngredientGroup.index] = index
          this[V3RecipeIngredientGroup.recipe] = recipeId
        }
      }
    }
  }
}
