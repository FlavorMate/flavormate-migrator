/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

object V3RecipeInstructionGroupItem : V3BaseEntity("v3__recipe__instruction_group__item") {
  val index = integer("index")
  val label = text("label").nullable()
  val group = reference("group_id", V3RecipeInstructionGroup.id)
}
