/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Serving
import de.flavormate.models.v3.V3Serving
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeServingService {
  fun migrateRecipeServings() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Serving.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipe servings ($i..${i + BATCH_SIZE}/$count)")
      val servings =
        transaction(SourceDbInstance.getInstance()) {
          V2Serving.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3Serving.batchInsert(servings) { serving ->
          val id =
            DynamicMappingTable.getOrCreateId(
              serving[V2Serving.id],
              DynamicMappingTable.recipeServing,
            )
          this[V3Serving.id] = id
          this[V3Serving.amount] = serving[V2Serving.amount]
          this[V3Serving.label] = serving[V2Serving.label]
        }
      }
    }
  }
}
