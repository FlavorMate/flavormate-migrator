/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.Table

object V3BookRecipe : Table("v3__book__recipe") {
  val book = reference("book_id", V3Book.id)
  val recipe = reference("recipe_id", V3Recipe.id)

  override val primaryKey = PrimaryKey(book, recipe)
}
