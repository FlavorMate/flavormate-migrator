/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.mappingTables

import java.util.*

object DynamicMappingTable {

  val account = mutableMapOf<Int, String>()
  val accountAvatar = mutableMapOf<Int, String>()
  val author = mutableMapOf<Int, String>()
  val book = mutableMapOf<Int, String>()
  val recipe = mutableMapOf<Int, String>()
  val recipeFile = mutableMapOf<Int, String>()
  val recipeIngredientGroup = mutableMapOf<Int, String>()
  val recipeIngredientGroupItem = mutableMapOf<Int, String>()
  val recipeInstructionGroup = mutableMapOf<Int, String>()
  val recipeInstructionGroupItem = mutableMapOf<Int, String>()
  val recipeNutrition = mutableMapOf<Int, String>()
  val recipeServing = mutableMapOf<Int, String>()
  val story = mutableMapOf<Int, String>()
  val tag = mutableMapOf<Int, String>()

  // Indices (ID -> Index)
  val recipeIngredientGroupIndex = mutableMapOf<String, Int>()
  val recipeIngredientGroupItemIndex = mutableMapOf<String, Int>()
  val recipeInstructionGroupIndex = mutableMapOf<String, Int>()
  val recipeInstructionGroupItemIndex = mutableMapOf<String, Int>()

  fun getOrCreateId(input: Int, map: MutableMap<Int, String>): String {
    map.putIfAbsent(input, UUID.randomUUID().toString())
    return map[input]!!
  }

  fun getId(input: Int, map: MutableMap<Int, String>): String = map[input]!!

  fun getOrCreateIndex(id: String, map: MutableMap<String, Int>): Int {
    val a = map.merge(id, 0) { oldValue, _ -> oldValue.plus(1) }!!
    return a
  }
}
