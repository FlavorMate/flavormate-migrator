/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Story : V2BaseEntity("stories") {
  val content = text("content")
  val label = text("label")
  val recipe = reference("recipe_id", V2Recipe.id)
  val author = reference("author_id", V2Author.id)
}
