/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Author
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object AuthorService {
  fun migrateAuthors() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Author.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating authors ($i..${i + BATCH_SIZE}/$count)")
      val authors =
        transaction(SourceDbInstance.getInstance()) {
          V2Author.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      authors.forEach {
        DynamicMappingTable.author[it[V2Author.id]] =
          DynamicMappingTable.account[it[V2Author.account]]!!
      }
    }
  }
}
