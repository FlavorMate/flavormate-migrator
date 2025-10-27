/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import de.flavormate.databases.IntervalColumnType
import de.flavormate.enums.v2.V2Course
import de.flavormate.enums.v2.V2Diet

object V2Recipe : V2BaseEntity("recipes") {
  val cookTime = registerColumn("cook_time", IntervalColumnType())
  val prepTime = registerColumn("prep_time", IntervalColumnType())
  val restTime = registerColumn("rest_time", IntervalColumnType())

  val rating = double("rating")

  // Text fields with enums for validation
  val course = enumerationByName<V2Course>("course", 255)
  val description = text("description").nullable() // 'varchar(255)', optional
  val diet = enumerationByName<V2Diet>("diet", 255) // Not null
  val label = text("label") // 'varchar(255)', not null

  val url = text("url").nullable() // 'varchar(255)', optional

  val author = reference("author_id", V2Author.id)
  val serving = reference("serving_id", V2Serving.id)
}
