/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import de.flavormate.databases.IntervalColumnType
import de.flavormate.enums.v3.V3Course
import de.flavormate.enums.v3.V3Diet
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object V3Recipe : V3BaseEntity("v3__recipe") {
  val cookTime = registerColumn("cook_time", IntervalColumnType())
  val course = enumerationByName<V3Course>("course", 255)
  val description = text("description").nullable()
  val diet = enumerationByName<V3Diet>("diet", 255)
  val label = text("label")
  val prepTime = registerColumn("prep_time", IntervalColumnType())
  val restTime = registerColumn("rest_time", IntervalColumnType())
  val serving = reference("serving_id", V3Serving.id)
  val url = text("url").nullable()
  val createdOn = timestampWithTimeZone("created_on")
  val lastModifiedOn = timestampWithTimeZone("last_modified_on")
  val version = long("version")
  val ownedBy = reference("owned_by", V3Account.id)
  val coverFile = optReference("cover_file", V3RecipeFile.id)
}
