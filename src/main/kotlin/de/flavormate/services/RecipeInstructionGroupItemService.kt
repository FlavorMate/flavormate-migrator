/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Instruction
import de.flavormate.models.v3.V3RecipeInstructionGroupItem
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeInstructionGroupItemService {

  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Instruction.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipe instruction group items ($i..${i + BATCH_SIZE}/$count)")
      val instructions =
        transaction(SourceDbInstance.getInstance()) {
          V2Instruction.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3RecipeInstructionGroupItem.batchInsert(instructions) { instruction ->
          val id =
            DynamicMappingTable.getOrCreateId(
              instruction[V2Instruction.id],
              DynamicMappingTable.recipeInstructionGroupItem,
            )
          val groupId =
            DynamicMappingTable.getId(
              instruction[V2Instruction.group],
              DynamicMappingTable.recipeInstructionGroup,
            )

          val index =
            DynamicMappingTable.getOrCreateIndex(
              groupId,
              DynamicMappingTable.recipeInstructionGroupItemIndex,
            )

          this[V3RecipeInstructionGroupItem.id] = id
          this[V3RecipeInstructionGroupItem.label] = instruction[V2Instruction.label].trim()
          this[V3RecipeInstructionGroupItem.index] = index
          this[V3RecipeInstructionGroupItem.group] = groupId
        }
      }
    }
  }
}
