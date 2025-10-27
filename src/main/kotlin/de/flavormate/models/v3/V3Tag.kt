/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object V3Tag : V3BaseEntity("v3__tag") {
  val label = text("label")
  val createdOn = timestampWithTimeZone("created_on")
  val lastModifiedOn = timestampWithTimeZone("last_modified_on")
  val version = long("version")
  val coverRecipe = optReference("cover_recipe", V3Recipe.id)
}
