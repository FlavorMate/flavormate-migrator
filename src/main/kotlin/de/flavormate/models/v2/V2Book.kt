/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Book : V2BaseEntity("books") {
  val label = text("label")

  val owner = reference("owner_id", V2Author.id)

  val visible = bool("visible")

  //     val recipes=reference("recipes",)
}
