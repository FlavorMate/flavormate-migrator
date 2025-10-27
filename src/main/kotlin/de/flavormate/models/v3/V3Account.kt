/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import de.flavormate.enums.v3.V3Diet
import java.time.OffsetDateTime
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object V3Account : V3BaseEntity("v3__account") {
  val username = text("username")
  val displayName = text("display_name")
  val password = text("password")
  val enabled = bool("enabled").default(false)
  val verified = bool("verified").default(false)
  val diet = enumerationByName<V3Diet>("diet", 255).default(V3Diet.Meat)
  val email = text("email")
  val firstLogin = bool("first_login").default(true)
  val avatar = optReference("avatar", V3AccountAvatar.id)
  val createdOn = timestampWithTimeZone("created_on").default(OffsetDateTime.now())
  val lastModifiedOn = timestampWithTimeZone("last_modified_on").default(OffsetDateTime.now())
  val ownedBy = reference("owned_by", id)
  val version = long("version").default(1)
}
