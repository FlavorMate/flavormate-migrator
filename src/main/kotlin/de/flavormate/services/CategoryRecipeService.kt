/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.CategoryMappingTable
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2CategoryRecipe
import de.flavormate.models.v3.V3CategoryRecipe
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object CategoryRecipeService {
  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2CategoryRecipe.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating category recipe relations ($i..${i + BATCH_SIZE}/$count)")
      val relations =
        transaction(SourceDbInstance.getInstance()) {
          V2CategoryRecipe.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3CategoryRecipe.batchInsert(relations) { relation ->
          val categoryId = CategoryMappingTable.getId(relation[V2CategoryRecipe.category])
          val recipeId =
            DynamicMappingTable.getId(relation[V2CategoryRecipe.recipe], DynamicMappingTable.recipe)
          this[V3CategoryRecipe.category] = categoryId
          this[V3CategoryRecipe.recipe] = recipeId
        }
      }
    }
  }
}
