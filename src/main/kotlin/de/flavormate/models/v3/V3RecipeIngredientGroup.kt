/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

object V3RecipeIngredientGroup : V3BaseEntity("v3__recipe__ingredient_group") {
  val index = integer("index")
  val label = text("label").nullable()
  val recipe = reference("recipe_id", V3Recipe.id)
}
