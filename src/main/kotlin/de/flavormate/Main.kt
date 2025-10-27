/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate

import de.flavormate.databases.FlywayInstance
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.services.*
import de.flavormate.utils.EnvProperties
import kotlin.system.exitProcess

const val BATCH_SIZE = 50L

fun main(args: Array<String>) {

  val sourceReady = SourceDbInstance.isDatabaseReady()

  if (!sourceReady) {
    println(
      "Source database is not ready. Please start the latest FlavorMate v2 Server to prepare the database before migrating to v3."
    )
    exitProcess(1)
  }

  if (EnvProperties.cleanDatabase) {
    println("Cleaning database")
    FlywayInstance.getInstance().clean()
  } else {
    val alreadyMigrated = TargetDbInstance.alreadyMigrated()

    if (alreadyMigrated) {
      println("Database already migrated")
      exitProcess(0)
    }
  }

  FlywayInstance.getInstance().migrate()

  // Clean old V3 files
  FileService.cleanV3Folders()

  println("Migrating accounts")
  AccountService.migrateAccounts()

  println("Migrating authors")
  AuthorService.migrateAuthors()

  println("Migrating roles")
  RoleService.migrateRoles()

  println("Migrate recipes")
  RecipeServingService.migrateRecipeServings()

  RecipeIngredientGroupItemNutritionService.migrate()

  RecipeService.migrateRecipes()

  RecipeIngredientGroupService.migrate()
  RecipeIngredientGroupItemService.migrate()

  RecipeInstructionGroupService.migrate()
  RecipeInstructionGroupItemService.migrate()

  println("Migrate category recipe relations")
  CategoryRecipeService.migrate()

  println("Migrate books")
  BookService.migrate()
  BookRecipeService.migrate()
  BookSubscribeService.migrate()

  println("Migrate tags")
  TagService.migrateTags()
  TagRecipeService.migrate()

  println("Migrate stories")
  StoryService.migrate()

  println("Migrating account avatars")
  AccountAvatarService.migrate()

  println("Migrate recipe files")
  RecipeFileService.migrate()

  println("Update covers")
  RecipeFileService.updateCovers()
  BookService.updateCovers()
  CategoryService.updateCovers()
  TagService.updateCovers()
}
