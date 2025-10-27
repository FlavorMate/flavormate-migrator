/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import org.jetbrains.exposed.sql.Table

object V2AccountRole : Table("account_roles") {
  val account = reference("account_id", V2Account.id)
  val role = reference("role_id", V2Role.id)

  override val primaryKey = PrimaryKey(account, role)
}
