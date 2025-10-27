/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Instruction : V2BaseEntity("instructions") {
  val label = text("label")
  val group = reference("group_id", V2InstructionGroup.id)
}
