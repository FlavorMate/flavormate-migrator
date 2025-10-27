/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.databases

import java.time.Duration
import org.jetbrains.exposed.sql.ColumnType
import org.postgresql.util.PGInterval

class IntervalColumnType : ColumnType<PGInterval>() {

  override fun sqlType(): String = "INTERVAL SECOND"

  override fun valueFromDB(value: Any): PGInterval =
    when (value) {
      is PGInterval -> value
      is Long -> PGInterval(Duration.ofNanos(value).toString())
      is Number -> PGInterval(Duration.ofNanos(value.toLong()).toString())
      is String -> PGInterval(Duration.parse(value).toString())
      else -> valueFromDB(value.toString())
    }

  override fun notNullValueToDB(value: PGInterval): Any = value
}
