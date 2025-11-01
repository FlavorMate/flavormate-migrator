/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.databases

import de.flavormate.utils.EnvProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object TargetDbInstance {
  private val host
    get() = EnvProperties.targetHost

  private val port
    get() = EnvProperties.targetPort

  private val database
    get() = EnvProperties.targetDatabase

  private val username
    get() = EnvProperties.targetUsername

  private val password
    get() = EnvProperties.targetPassword

  @Volatile private var instance: Database? = null

  fun getInstance() =
    instance
      ?: synchronized(this) {
        instance
          ?: Database.connect(
            url = "jdbc:postgresql://$host:$port/$database",
            driver = "org.postgresql.Driver",
            user = username,
            password = password,
          )
      }

  fun alreadyMigrated() =
    transaction(getInstance()) {
      exec(
        """
            SELECT EXISTS (
                SELECT FROM
                    pg_tables
                WHERE
                    schemaname = 'public' AND
                    tablename  = 'v3_flyway_schema_history'
            );
        """
          .trimIndent()
      ) {
        it.next()
        it.getBoolean(1)
      }!!
    }

  fun clean() {
    transaction(getInstance()) {
      exec(
        """
            SELECT CONCAT('DROP TABLE IF EXISTS ', TABLE_NAME,' CASCADE;') as query
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_NAME LIKE 'v3_%';
        """
          .trimIndent()
      ) {
        while (it.next()) {
          println(it.getString("query"))
          exec(it.getString("query"))
        }
      }
    }
  }
}
