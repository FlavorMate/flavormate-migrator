/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.utils

import io.github.cdimascio.dotenv.dotenv

object EnvProperties {

  val dotenv = dotenv {
    ignoreIfMalformed = true
    ignoreIfMissing = true
  }

  val cleanDatabase: Boolean
    get() = dotenv["FLAVORMATE_MIGRATOR_CLEAN_DATABASE"]?.toBooleanStrict() ?: false

  val rootPath: String
    get() = dotenv["FLAVORMATE_PATHS_FILES"]!!.let(::expandEnvVars)

  val sourceHost: String
    get() = dotenv["FLAVORMATE_MIGRATOR_DB_SOURCE_HOST"]!!

  val sourcePort: Int
    get() = dotenv["FLAVORMATE_MIGRATOR_DB_SOURCE_PORT", "5432"]!!.toInt()

  val sourceDatabase: String
    get() = dotenv["FLAVORMATE_MIGRATOR_DB_SOURCE_DB"]!!

  val sourceUsername: String
    get() = dotenv["FLAVORMATE_MIGRATOR_DB_SOURCE_USERNAME"]!!

  val sourcePassword: String
    get() = dotenv["FLAVORMATE_MIGRATOR_DB_SOURCE_PASSWORD"]!!

  val targetHost: String
    get() = dotenv["FLAVORMATE_DB_HOST"]!!

  val targetPort: Int
    get() = dotenv["FLAVORMATE_DB_PORT", "5432"]!!.toInt()

  val targetDatabase: String
    get() = dotenv["FLAVORMATE_DB_DATABASE"]!!

  val targetUsername: String
    get() = dotenv["FLAVORMATE_DB_USER"]!!

  val targetPassword: String
    get() = dotenv["FLAVORMATE_DB_PASSWORD"]!!

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
