/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Tag : V2BaseEntity("tags") {
  val label = text("label")
}
