/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.Table

object V3CategoryRecipe : Table("v3__category__recipe") {
  val category = text("category_id")
  val recipe = reference("recipe_id", V3Recipe.id)

  override val primaryKey = PrimaryKey(category, recipe)
}
