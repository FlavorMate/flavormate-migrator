/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import org.jetbrains.exposed.sql.Table

object V2CategoryRecipe : Table("category_recipe") {
  val recipe = reference("recipe_id", V2Recipe.id)
  val category = reference("category_id", V2Category.id)

  override val primaryKey = PrimaryKey(category, recipe)
}
