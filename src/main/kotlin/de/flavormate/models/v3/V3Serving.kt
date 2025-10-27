/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

object V3Serving : V3BaseEntity("v3__recipe__serving") {
  val amount = double("amount")
  val label = text("label")
}
