/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.utils

import io.github.cdimascio.dotenv.dotenv

object EnvProperties {

  val cleanDatabase: Boolean = dotenv().get("FLAVORMATE_MIGRATOR_CLEAN_DATABASE").toBooleanStrict()
  val rootPath: String = dotenv().get("FLAVORMATE_PATHS_FILES").let(::expandEnvVars)

  val sourceHost: String = dotenv().get("FLAVORMATE_MIGRATOR_DB_SOURCE_HOST")
  val sourcePort: Int = dotenv().get("FLAVORMATE_MIGRATOR_DB_SOURCE_PORT")?.toInt() ?: 5432
  val sourceDatabase: String = dotenv().get("FLAVORMATE_MIGRATOR_DB_SOURCE_DB")
  val sourceUsername: String = dotenv().get("FLAVORMATE_MIGRATOR_DB_SOURCE_USERNAME")
  val sourcePassword: String = dotenv().get("FLAVORMATE_MIGRATOR_DB_SOURCE_PASSWORD")

  val targetHost: String = dotenv().get("FLAVORMATE_DB_HOST")
  val targetPort: Int = dotenv().get("FLAVORMATE_DB_PORT")?.toInt() ?: 5432
  val targetDatabase: String = dotenv().get("FLAVORMATE_DB_DATABASE")
  val targetUsername: String = dotenv().get("FLAVORMATE_DB_USER")
  val targetPassword: String = dotenv().get("FLAVORMATE_DB_PASSWORD")

  fun expandEnvVars(input: String): String {
    var result = input

    // ${VAR} and $VAR
    val unixRegex = Regex("""\$\{?([A-Za-z0-9_]+)\}?""")
    result =
      unixRegex.replace(result) { match ->
        val varName = match.groupValues[1]
        System.getenv(varName) ?: match.value
      }

    // %VAR% (Windows)
    val winRegex = Regex("""%([A-Za-z0-9_]+)%""")
    result =
      winRegex.replace(result) { match ->
        val varName = match.groupValues[1]
        System.getenv(varName) ?: match.value
      }

    return result
  }
}
