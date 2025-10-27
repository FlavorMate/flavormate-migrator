/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Tag
import de.flavormate.models.v3.V3Tag
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object TagService {
  fun migrateTags() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Tag.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating tags ($i..${i + BATCH_SIZE}/$count)")
      val tags =
        transaction(SourceDbInstance.getInstance()) {
          V2Tag.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3Tag.batchInsert(tags) { tag ->
          val id = DynamicMappingTable.getOrCreateId(tag[V2Tag.id], DynamicMappingTable.tag)
          this[V3Tag.id] = id
          this[V3Tag.label] = tag[V2Tag.label]
          this[V3Tag.createdOn] = tag[V2Tag.createdOn]
          this[V3Tag.lastModifiedOn] = tag[V2Tag.lastModifiedOn]
          this[V3Tag.version] = tag[V2Tag.version]
        }
      }
    }
  }

  fun updateCovers() {
    transaction(TargetDbInstance.getInstance()) {
      val query =
        """
            UPDATE public.v3__tag t
            SET cover_recipe = sub.recipe_id
            FROM (
                SELECT DISTINCT ON (rb.tag_id) rb.tag_id, r.id AS recipe_id
                FROM public.v3__tag__recipe rb
                JOIN public.v3__recipe r on rb.recipe_id = r.id
                WHERE r.cover_file IS NOT NULL
                ORDER BY rb.tag_id, r.id
            )
            AS sub
            WHERE t.id = sub.tag_id;
        """
          .trimIndent()
      exec(query)
    }
  }
}
