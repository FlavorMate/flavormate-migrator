/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2CategoryGroup : V2BaseEntity("category_groups") {
  val label = text("label")
}
