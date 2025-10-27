/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.Table

object V3TagRecipe : Table("v3__tag__recipe") {
  val recipe = reference("recipe_id", V3Recipe.id)
  val tag = reference("tag_id", V3Tag.id)

  override val primaryKey = PrimaryKey(recipe, tag)
}
