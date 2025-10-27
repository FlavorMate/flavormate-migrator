/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.enums.FilePath
import de.flavormate.utils.EnvProperties
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories

object FileService {
  private val rootPath = EnvProperties.rootPath

  fun cleanV3Folders() {
    for (folder in FilePath.entries) {
      val path = Paths.get(rootPath).resolve(folder.path).toFile()

      if (path.exists()) path.deleteRecursively()

      path.mkdirs()
    }
  }

  fun getPath(category: FilePath, id: String): Path {
    val path =
      Paths.get(rootPath)
        .resolve(category.path)
        .resolve(id.take(2))
        .resolve(id.substring(2, 4))
        .resolve(id)

    path.createDirectories()

    return path
  }
}
