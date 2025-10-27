/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.enums.FilePath
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2File
import de.flavormate.models.v3.V3Recipe
import de.flavormate.models.v3.V3RecipeFile
import de.flavormate.utils.EnvProperties
import de.flavormate.utils.ImageUtils
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeFileService {

  private val rootPath = EnvProperties.rootPath

  fun migrate() {
    val count =
      transaction(SourceDbInstance.getInstance()) {
        V2File.selectAll().where { V2File.recipeId.isNotNull() }.count()
      }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating recipe files ($i..${i + BATCH_SIZE}/$count)")
      val files =
        transaction(SourceDbInstance.getInstance()) {
          V2File.selectAll()
            .where { V2File.recipeId.isNotNull() }
            .offset(i)
            .limit(BATCH_SIZE.toInt())
            .map { it }
        }

      val existingFiles = files.filter { getPath(it[V2File.recipeId]!!, it[V2File.id]).exists() }

      transaction(TargetDbInstance.getInstance()) {
        V3RecipeFile.batchInsert(existingFiles) { file ->
          val id =
            DynamicMappingTable.getOrCreateId(file[V2File.id], DynamicMappingTable.recipeFile)

          val recipeId =
            DynamicMappingTable.getId(file[V2File.recipeId]!!, DynamicMappingTable.recipe)

          val ownedById =
            V3Recipe.selectAll()
              .where { V3Recipe.id.eq(recipeId) }
              .map { it[V3Recipe.ownedBy] }
              .first()

          this[V3RecipeFile.id] = id
          this[V3RecipeFile.mimeType] = "image/webp"
          this[V3RecipeFile.createdOn] = file[V2File.createdOn]
          this[V3RecipeFile.lastModifiedOn] = file[V2File.lastModifiedOn]
          this[V3RecipeFile.version] = file[V2File.version]
          this[V3RecipeFile.recipe] = recipeId
          this[V3RecipeFile.ownedBy] = ownedById
        }
      }

      for (file in existingFiles) {
        val fileId = DynamicMappingTable.getId(file[V2File.id], DynamicMappingTable.recipeFile)

        val inputFile = getPath(file[V2File.recipeId]!!, file[V2File.id])

        val outputDir = FileService.getPath(FilePath.Recipe, fileId)

        ImageUtils.generateWideImage(inputFile, outputDir)
      }
    }
  }

  private fun getPath(recipeId: Int, fileId: Int): Path =
    Paths.get(rootPath)
      .resolve("recipes")
      .resolve(recipeId.toString())
      .resolve("images")
      .resolve("$fileId.jpg")

  fun updateCovers() {
    transaction(TargetDbInstance.getInstance()) {
      val query =
        """
            UPDATE public.v3__recipe r
            SET cover_file = sub.file_id
            FROM (
                SELECT DISTINCT ON (recipe_id) recipe_id, id AS file_id
                FROM public.v3__recipe__file
                ORDER BY recipe_id, created_on, id
            )
            AS sub
            WHERE r.id = sub.recipe_id
        """
          .trimIndent()
      exec(query)
    }
  }
}
