/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Category : V2BaseEntity("categories") {
  val label = text("label")
  val group = reference("group_id", V2CategoryGroup.id)
}
