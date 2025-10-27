/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.enums.v3.V3Diet
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.models.v2.V2Account
import de.flavormate.models.v3.V3Account
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object AccountService {

  fun migrateAccounts() {
    val errorAccounts =
      transaction(SourceDbInstance.getInstance()) {
        V2Account.selectAll().where { V2Account.mail.isNull() }.map { it[V2Account.username] }
      }
    if (errorAccounts.isNotEmpty()) {
      throw Error("Accounts with usernames do not have an email address: $errorAccounts")
    }

    val count = transaction(SourceDbInstance.getInstance()) { V2Account.selectAll().count() }
    for (i in 0..count step BATCH_SIZE) {
      println("Migrating accounts ($i..${i + BATCH_SIZE}/$count)")
      val accounts =
        transaction(SourceDbInstance.getInstance()) {
          V2Account.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3Account.batchInsert(accounts) { account ->
          val id =
            DynamicMappingTable.getOrCreateId(account[V2Account.id], DynamicMappingTable.account)
          this[V3Account.id] = id
          this[V3Account.username] = account[V2Account.username]
          this[V3Account.displayName] = account[V2Account.displayName]
          this[V3Account.password] = account[V2Account.password]
          this[V3Account.enabled] = account[V2Account.valid]
          this[V3Account.verified] = true
          this[V3Account.diet] = V3Diet.valueOf(account[V2Account.diet].name)
          this[V3Account.email] = account[V2Account.mail]
          this[V3Account.firstLogin] = account[V2Account.firstLogin]
          this[V3Account.createdOn] = account[V2Account.createdOn]
          this[V3Account.lastModifiedOn] = account[V2Account.lastModifiedOn]
          //                    this[V3Account.avatar] = account[V2Account.avatar]
          this[V3Account.version] = account[V2Account.version]
          this[V3Account.ownedBy] = id
        }
      }
    }
  }
}
