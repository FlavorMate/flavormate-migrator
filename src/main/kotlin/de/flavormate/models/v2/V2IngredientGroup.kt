/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2IngredientGroup : V2BaseEntity("ingredient_groups") {

  val label = text("label").nullable()
  val recipe = reference("recipe_id", V2Recipe.id)
}
