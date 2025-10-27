/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Book
import de.flavormate.models.v3.V3Book
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object BookService {
  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Book.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating books ($i..${i + BATCH_SIZE}/$count)")
      val books =
        transaction(SourceDbInstance.getInstance()) {
          V2Book.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3Book.batchInsert(books) { book ->
          val id = DynamicMappingTable.getOrCreateId(book[V2Book.id], DynamicMappingTable.book)
          val accountId = DynamicMappingTable.getId(book[V2Book.owner], DynamicMappingTable.author)
          this[V3Book.id] = id
          this[V3Book.label] = book[V2Book.label]
          this[V3Book.visible] = book[V2Book.visible]
          this[V3Book.createdOn] = book[V2Book.createdOn]
          this[V3Book.lastModifiedOn] = book[V2Book.lastModifiedOn]
          this[V3Book.version] = book[V2Book.version]
          this[V3Book.ownedBy] = accountId
        }
      }
    }
  }

  fun updateCovers() {
    transaction(TargetDbInstance.getInstance()) {
      val query =
        """
            UPDATE public.v3__book b
            SET cover_recipe = sub.recipe_id
            FROM (
                SELECT DISTINCT ON (rb.book_id) rb.book_id, r.id AS recipe_id
                FROM public.v3__book__recipe rb
                JOIN public.v3__recipe r on rb.recipe_id = r.id
                WHERE r.cover_file IS NOT NULL
                ORDER BY rb.book_id, r.id
            )
            AS sub
            WHERE b.id = sub.book_id;
        """
          .trimIndent()
      exec(query)
    }
  }
}
