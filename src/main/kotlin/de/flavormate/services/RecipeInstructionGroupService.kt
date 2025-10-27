/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.extensions.trimToNull
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2InstructionGroup
import de.flavormate.models.v3.V3RecipeInstructionGroup
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeInstructionGroupService {

  fun migrate() {
    val count =
      transaction(SourceDbInstance.getInstance()) { V2InstructionGroup.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipe instruction groups ($i..${i + BATCH_SIZE}/$count)")
      val groups =
        transaction(SourceDbInstance.getInstance()) {
          V2InstructionGroup.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3RecipeInstructionGroup.batchInsert(groups) { group ->
          val id =
            DynamicMappingTable.getOrCreateId(
              group[V2InstructionGroup.id],
              DynamicMappingTable.recipeInstructionGroup,
            )
          val recipeId =
            DynamicMappingTable.getId(group[V2InstructionGroup.recipe], DynamicMappingTable.recipe)

          val index =
            DynamicMappingTable.getOrCreateIndex(
              recipeId,
              DynamicMappingTable.recipeInstructionGroupIndex,
            )

          this[V3RecipeInstructionGroup.id] = id
          this[V3RecipeInstructionGroup.label] = group[V2InstructionGroup.label]?.trimToNull()
          this[V3RecipeInstructionGroup.index] = index
          this[V3RecipeInstructionGroup.recipe] = recipeId
        }
      }
    }
  }
}
