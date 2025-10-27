/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Story
import de.flavormate.models.v3.V3Story
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object StoryService {

  fun migrate() {
    val count = transaction(SourceDbInstance.getInstance()) { V2Story.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating stories ($i..${i + BATCH_SIZE}/$count)")

      val stories =
        transaction(SourceDbInstance.getInstance()) {
          V2Story.selectAll().limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3Story.batchInsert(stories) { story ->
          val id = DynamicMappingTable.getOrCreateId(story[V2Story.id], DynamicMappingTable.story)
          val recipeId =
            DynamicMappingTable.getId(story[V2Story.recipe], DynamicMappingTable.recipe)
          val accountId =
            DynamicMappingTable.getId(story[V2Story.author], DynamicMappingTable.author)

          this[V3Story.id] = id
          this[V3Story.content] = story[V2Story.content]
          this[V3Story.label] = story[V2Story.label]
          this[V3Story.createdOn] = story[V2Story.createdOn]
          this[V3Story.lastModifiedOn] = story[V2Story.lastModifiedOn]
          this[V3Story.version] = story[V2Story.version]
          this[V3Story.recipe] = recipeId
          this[V3Story.ownedBy] = accountId
        }
      }
    }
  }
}
