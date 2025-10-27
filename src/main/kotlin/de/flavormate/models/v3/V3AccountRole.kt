/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.Table

object V3AccountRole : Table("v3__account__role") {
  val account = reference("account_id", V3Account.id)
  val role = reference("role_id", V3Role.id)

  override val primaryKey = PrimaryKey(account, role)
}
