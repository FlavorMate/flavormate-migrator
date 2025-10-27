/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2BookRecipe
import de.flavormate.models.v3.V3BookRecipe
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object BookRecipeService {
  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2BookRecipe.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating book recipes ($i..${i + BATCH_SIZE}/$count)")
      val relations =
        transaction(SourceDbInstance.getInstance()) {
          V2BookRecipe.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3BookRecipe.batchInsert(relations) { relation ->
          val bookId =
            DynamicMappingTable.getId(relation[V2BookRecipe.book], DynamicMappingTable.book)
          val recipeId =
            DynamicMappingTable.getId(relation[V2BookRecipe.recipe], DynamicMappingTable.recipe)

          this[V3BookRecipe.book] = bookId
          this[V3BookRecipe.recipe] = recipeId
        }
      }
    }
  }
}
