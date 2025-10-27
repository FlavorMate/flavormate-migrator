/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Serving : V2BaseEntity("servings") {
  val amount = double("amount")
  val label = text("label")
}
