/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import org.jetbrains.exposed.sql.Table

object V2TagRecipe : Table("tag_recipe") {
  val recipe = reference("recipe_id", V2Recipe.id)
  val tag = reference("tag_id", V2Tag.id)

  override val primaryKey = PrimaryKey(recipe, tag)
}
