/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.databases

import de.flavormate.utils.EnvProperties
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object SourceDbInstance {
  private val host
    get() = EnvProperties.sourceHost

  private val port
    get() = EnvProperties.sourcePort

  private val database
    get() = EnvProperties.sourceDatabase

  private val username
    get() = EnvProperties.sourceUsername

  private val password
    get() = EnvProperties.sourcePassword

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

  fun isDatabaseReady(): Boolean =
    transaction(getInstance()) {
      exec(
        """
            
           SELECT EXISTS (
               SELECT 1
               FROM pg_tables pt
               WHERE pt.schemaname = 'public'
                 AND pt.tablename = 'flyway_schema_history'
                 AND EXISTS (
                   SELECT 1
                   FROM public.flyway_schema_history
                   WHERE version = '2.1.1'
               )
           );
        """
          .trimIndent()
      ) {
        it.next()
        it.getBoolean(1)
      }!!
    }
}
