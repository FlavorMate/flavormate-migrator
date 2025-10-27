/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Nutrition : V2BaseEntity("nutrition") {
  val openFoodFactsId = double("open_food_facts_id").nullable()
  val carbohydrates = double("carbohydrates").nullable()
  val energyKcal = double("energy_kcal").nullable()
  val fat = double("fat").nullable()
  val saturatedFat = double("saturated_fat").nullable()
  val sugars = double("sugars").nullable()
  val fiber = double("fiber").nullable()
  val proteins = double("proteins").nullable()
  val salt = double("salt").nullable()
  val sodium = double("sodium").nullable()
}
