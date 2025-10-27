/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.mappingTables

object CategoryMappingTable {
  private const val FILE_PATH = "/categories.csv"

  @Volatile private var instance: Map<Int, String>? = null

  private fun getInstance() = instance ?: synchronized(this) { instance ?: init() }

  private fun init(): Map<Int, String> {
    val lines =
      object {}.javaClass.getResourceAsStream(FILE_PATH)?.bufferedReader()?.readLines() ?: listOf()

    return lines.drop(1).associate { line ->
      val parts = line.split(",").map { it.trim().removeSurrounding("'") }

      parts[0].toInt() to parts[2]
    }
  }

  fun getId(key: Int): String = getInstance()[key]!!
}
