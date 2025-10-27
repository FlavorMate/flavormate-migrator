/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2BookSubscriber
import de.flavormate.models.v3.V3BookSubscriber
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object BookSubscribeService {
  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2BookSubscriber.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating book subscribers ($i..${i + BATCH_SIZE}/$count)")

      val subscribers =
        transaction(SourceDbInstance.getInstance()) {
          V2BookSubscriber.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3BookSubscriber.batchInsert(subscribers) { subscriber ->
          val bookId =
            DynamicMappingTable.getId(subscriber[V2BookSubscriber.book], DynamicMappingTable.book)
          val subscriberId =
            DynamicMappingTable.getId(
              subscriber[V2BookSubscriber.author],
              DynamicMappingTable.author,
            )

          this[V3BookSubscriber.book] = bookId
          this[V3BookSubscriber.subscriber] = subscriberId
        }
      }
    }
  }
}
