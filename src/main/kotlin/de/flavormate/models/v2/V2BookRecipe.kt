/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import org.jetbrains.exposed.sql.Table

object V2BookRecipe : Table("book_recipe") {
  val book = reference("book_id", V2Book.id)
  val recipe = reference("recipe_id", V2Recipe.id)

  override val primaryKey = PrimaryKey(recipe, book)
}
