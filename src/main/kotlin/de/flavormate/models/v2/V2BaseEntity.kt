/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone

abstract class V2BaseEntity(name: String) : Table(name = name) {
  val id = integer("id").autoIncrement().uniqueIndex()

  val version = long("version")

  val createdOn = timestampWithTimeZone("created_on")

  val lastModifiedOn = timestampWithTimeZone("last_modified_on")

  override val primaryKey = PrimaryKey(id)
}
