/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.enums.v3.V3Course
import de.flavormate.enums.v3.V3Diet
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Recipe
import de.flavormate.models.v3.V3Recipe
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeService {
  fun migrateRecipes() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Recipe.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipes ($i..${i + BATCH_SIZE}/$count)")
      val recipes =
        transaction(SourceDbInstance.getInstance()) {
          V2Recipe.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3Recipe.batchInsert(recipes) { recipe ->
          val id =
            DynamicMappingTable.getOrCreateId(recipe[V2Recipe.id], DynamicMappingTable.recipe)
          val ownedBy =
            DynamicMappingTable.getId(recipe[V2Recipe.author], DynamicMappingTable.author)
          this[V3Recipe.id] = id
          this[V3Recipe.cookTime] = recipe[V2Recipe.cookTime]
          this[V3Recipe.course] = V3Course.valueOf(recipe[V2Recipe.course].name)
          this[V3Recipe.description] = recipe[V2Recipe.description]
          this[V3Recipe.diet] = V3Diet.valueOf(recipe[V2Recipe.diet].name)
          this[V3Recipe.label] = recipe[V2Recipe.label]
          this[V3Recipe.prepTime] = recipe[V2Recipe.prepTime]
          this[V3Recipe.restTime] = recipe[V2Recipe.restTime]
          this[V3Recipe.serving] =
            DynamicMappingTable.getId(recipe[V2Recipe.serving], DynamicMappingTable.recipeServing)
          this[V3Recipe.url] = recipe[V2Recipe.url]
          this[V3Recipe.createdOn] = recipe[V2Recipe.createdOn]
          this[V3Recipe.lastModifiedOn] = recipe[V2Recipe.lastModifiedOn]
          this[V3Recipe.version] = recipe[V2Recipe.version]
          this[V3Recipe.ownedBy] = ownedBy
        }
      }
    }
  }
}
