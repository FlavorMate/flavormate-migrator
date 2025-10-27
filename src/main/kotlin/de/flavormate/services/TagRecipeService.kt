/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2TagRecipe
import de.flavormate.models.v3.V3TagRecipe
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object TagRecipeService {
  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2TagRecipe.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating tag recipe relations ($i..${i + BATCH_SIZE}/$count)")
      val relations =
        transaction(SourceDbInstance.getInstance()) {
          V2TagRecipe.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3TagRecipe.batchInsert(relations) { relation ->
          val recipeId =
            DynamicMappingTable.getId(relation[V2TagRecipe.recipe], DynamicMappingTable.recipe)
          val tagId = DynamicMappingTable.getId(relation[V2TagRecipe.tag], DynamicMappingTable.tag)
          this[V3TagRecipe.recipe] = recipeId
          this[V3TagRecipe.tag] = tagId
        }
      }
    }
  }
}
