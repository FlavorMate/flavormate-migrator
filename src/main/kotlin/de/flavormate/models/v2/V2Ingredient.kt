/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Ingredient : V2BaseEntity("ingredients") {
  val amount = double("amount").nullable()
  val label = text("label")
  val unit = reference("unit_id", V2Unit.id).nullable()
  val group = reference("group_id", V2IngredientGroup.id)
  val schema = integer("schema")
  val unitLocalized = integer("unit_localized").nullable()
  val nutrition = reference("nutrition_id", V2Nutrition.id).nullable()
}
