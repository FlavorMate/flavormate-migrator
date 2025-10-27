/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.databases

import de.flavormate.utils.EnvProperties
import org.flywaydb.core.Flyway

object FlywayInstance {

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

  private val url
    get() = "jdbc:postgresql://${host}:${port}/${database}"

  @Volatile private var instance: Flyway? = null

  fun getInstance(): Flyway =
    instance
      ?: synchronized(this) {
        instance
          ?: Flyway.configure()
            .dataSource(url, username, password)
            .cleanDisabled(false)
            .defaultSchema("public")
            .locations("classpath:db/migration")
            .table("v3_flyway_schema_history")
            .baselineVersion("3.0.0")
            .skipDefaultCallbacks(true)
            // .sqlMigrationPrefix("V")
            //  .sqlMigrationSeparator("__")
            .load()
      }
}
