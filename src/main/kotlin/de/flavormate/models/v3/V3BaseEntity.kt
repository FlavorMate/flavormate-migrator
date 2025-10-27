/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import java.util.*
import org.jetbrains.exposed.sql.Table

abstract class V3BaseEntity(name: String) : Table(name = name) {
  val id = text("id").uniqueIndex().default(UUID.randomUUID().toString())

  override val primaryKey = PrimaryKey(id)
}
