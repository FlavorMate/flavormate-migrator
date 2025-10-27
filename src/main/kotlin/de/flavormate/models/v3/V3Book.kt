/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object V3Book : V3BaseEntity("v3__book") {
  val label = text("label")
  val visible = bool("visible")
  val createdOn = timestampWithTimeZone("created_on")
  val lastModifiedOn = timestampWithTimeZone("last_modified_on")
  val version = long("version")
  val ownedBy = reference("owned_by", V3Account.id)
  val coverFile = optReference("cover_file", V3Recipe.id)
}
