/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import de.flavormate.enums.v2.V2Diet
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

object V2Account : V2BaseEntity("accounts") {

  val displayName = text("display_name", eagerLoading = true)

  val username = text("username")

  val mail = text("mail")

  val password = text("password")

  val lastActivity = timestampWithTimeZone("last_activity")

  val valid = bool("valid")

  val diet = enumeration<V2Diet>("diet")

  val avatar = optReference("avatar", V2File.id)

  val firstLogin = bool("first_login")
}
