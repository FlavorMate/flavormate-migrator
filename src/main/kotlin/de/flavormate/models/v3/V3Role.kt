/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.Table

object V3Role : Table("v3__role") {
  val id = text("id")
  val value = text("value")

  override val primaryKey = PrimaryKey(id)
}
