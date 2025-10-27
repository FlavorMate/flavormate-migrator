/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object V3RecipeFile : V3BaseEntity("v3__recipe__file") {
  val mimeType = text("mime_type").default("image/webp")
  val createdOn = timestampWithTimeZone("created_on")
  val lastModifiedOn = timestampWithTimeZone("last_modified_on")
  val version = long("version")
  val ownedBy = reference("owned_by", V3Account.id)
  val recipe = reference("recipe_id", V3Recipe.id)
}
