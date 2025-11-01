/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Nutrition
import de.flavormate.models.v3.V3OpenFoodFactsProduct
import de.flavormate.models.v3.V3RecipeIngredientGroupItemNutrition
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeIngredientGroupItemNutritionService {

    fun migrate() {
        val count = transaction(SourceDbInstance.getInstance()) { V2Nutrition.selectAll().count() }

        for (i in 0..count step BATCH_SIZE) {
            println("Migrating recipe ingredient group item nutrition ($i..${i + BATCH_SIZE}/$count)")

            val nutrition =
                transaction(SourceDbInstance.getInstance()) {
                    V2Nutrition.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
                }

            transaction(TargetDbInstance.getInstance()) {
                val offProducts =
                    nutrition
                        .filter { it[V2Nutrition.openFoodFactsId] != null }
                        .map { it[V2Nutrition.openFoodFactsId]!! }
                V3OpenFoodFactsProduct.batchInsert(offProducts) { product ->
                    this[V3OpenFoodFactsProduct.id] = product.toString()
                }
            }

            transaction(TargetDbInstance.getInstance()) {
                V3RecipeIngredientGroupItemNutrition.batchInsert(nutrition) { nutrition ->
                    val id =
                        DynamicMappingTable.getOrCreateId(
                            nutrition[V2Nutrition.id],
                            DynamicMappingTable.recipeNutrition,
                        )
                    this[V3RecipeIngredientGroupItemNutrition.id] = id
                    this[V3RecipeIngredientGroupItemNutrition.openFoodFactsId] =
                        nutrition[V2Nutrition.openFoodFactsId]?.toString()
                    this[V3RecipeIngredientGroupItemNutrition.carbohydrates] =
                        nutrition[V2Nutrition.carbohydrates]
                    this[V3RecipeIngredientGroupItemNutrition.energyKcal] = nutrition[V2Nutrition.energyKcal]
                    this[V3RecipeIngredientGroupItemNutrition.fat] = nutrition[V2Nutrition.fat]
                    this[V3RecipeIngredientGroupItemNutrition.saturatedFat] =
                        nutrition[V2Nutrition.saturatedFat]
                    this[V3RecipeIngredientGroupItemNutrition.sugars] = nutrition[V2Nutrition.sugars]
                    this[V3RecipeIngredientGroupItemNutrition.fiber] = nutrition[V2Nutrition.fiber]
                    this[V3RecipeIngredientGroupItemNutrition.proteins] = nutrition[V2Nutrition.proteins]
                    this[V3RecipeIngredientGroupItemNutrition.salt] = nutrition[V2Nutrition.salt]
                    this[V3RecipeIngredientGroupItemNutrition.sodium] = nutrition[V2Nutrition.sodium]
                }
            }
        }
    }
}
