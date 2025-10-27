/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

object V3RecipeIngredientGroupItem : V3BaseEntity("v3__recipe__ingredient_group__item") {
  val index = integer("index")
  val amount = double("amount").nullable()
  val label = text("label")
  val group = reference("group_id", V3RecipeIngredientGroup.id)
  val unit = text("unit").nullable()
  val nutrition = reference("nutrition_id", V3RecipeIngredientGroupItemNutrition.id).nullable()
}
