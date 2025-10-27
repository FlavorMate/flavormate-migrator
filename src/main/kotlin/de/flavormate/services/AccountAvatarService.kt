/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.enums.FilePath
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Account
import de.flavormate.models.v2.V2File
import de.flavormate.models.v3.V3Account
import de.flavormate.models.v3.V3AccountAvatar
import de.flavormate.utils.EnvProperties
import de.flavormate.utils.ImageUtils
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object AccountAvatarService {

  private val rootPath = EnvProperties.rootPath

  fun migrate() {
    val count =
      transaction(SourceDbInstance.getInstance()) {
        V2Account.selectAll().where { V2Account.avatar.isNotNull() }.count()
      }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating account avatars ($i..${i + BATCH_SIZE}/$count)")
      val files =
        transaction(SourceDbInstance.getInstance()) {
          val fileIds =
            V2Account.selectAll()
              .where { V2Account.avatar.isNotNull() }
              .offset(i)
              .limit(BATCH_SIZE.toInt())
              .map { it[V2Account.avatar]!! }
          V2File.selectAll().where { V2File.id.inList(fileIds) }.map { it }
        }

      val existingFiles = files.filter { getPath(it[V2File.owner], it[V2File.id]).exists() }

      transaction(TargetDbInstance.getInstance()) {
        V3AccountAvatar.batchInsert(existingFiles) { file ->
          val id =
            DynamicMappingTable.getOrCreateId(file[V2File.id], DynamicMappingTable.accountAvatar)
          val ownedBy = DynamicMappingTable.getId(file[V2File.owner], DynamicMappingTable.account)

          this[V3AccountAvatar.id] = id
          this[V3AccountAvatar.mimeType] = "image/webp"
          this[V3AccountAvatar.createdOn] = file[V2File.createdOn]
          this[V3AccountAvatar.lastModifiedOn] = file[V2File.lastModifiedOn]
          this[V3AccountAvatar.version] = file[V2File.version]
          this[V3AccountAvatar.ownedBy] = ownedBy
        }

        for (file in existingFiles) {
          val id =
            DynamicMappingTable.getOrCreateId(file[V2File.id], DynamicMappingTable.accountAvatar)
          val ownedBy = DynamicMappingTable.getId(file[V2File.owner], DynamicMappingTable.account)

          V3Account.update({ V3Account.id eq ownedBy }) { it[V3Account.avatar] = id }
        }
      }

      for (file in existingFiles) {
        val fileId = DynamicMappingTable.getId(file[V2File.id], DynamicMappingTable.accountAvatar)

        val inputFile = getPath(file[V2File.owner], file[V2File.id])

        val outputDir = FileService.getPath(FilePath.AccountAvatar, fileId)

        ImageUtils.generateSquareImage(inputFile, outputDir)
      }
    }
  }

  private fun getPath(accountId: Int, fileId: Int): Path =
    Paths.get(rootPath)
      .resolve("accounts")
      .resolve(accountId.toString())
      .resolve("images")
      .resolve("$fileId.jpg")
}
