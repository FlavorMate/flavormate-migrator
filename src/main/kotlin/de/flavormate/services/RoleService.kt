/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.BATCH_SIZE
import de.flavormate.databases.SourceDbInstance
import de.flavormate.databases.TargetDbInstance
import de.flavormate.mappingTables.DynamicMappingTable
import de.flavormate.mappingTables.RoleMappingTable
import de.flavormate.models.v2.V2AccountRole
import de.flavormate.models.v3.V3AccountRole
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object RoleService {
  fun migrateRoles() {
    val count = transaction(SourceDbInstance.getInstance()) { V2AccountRole.selectAll().count() }

    for (i in 0..count step BATCH_SIZE) {
      println("Migrating roles ($i..${i + BATCH_SIZE}/$count)")
      val roles =
        transaction(SourceDbInstance.getInstance()) {
          V2AccountRole.selectAll().offset(i).limit(BATCH_SIZE.toInt()).map { it }
        }

      transaction(TargetDbInstance.getInstance()) {
        V3AccountRole.batchInsert(roles) { role ->
          val accountId =
            DynamicMappingTable.getId(role[V2AccountRole.account], DynamicMappingTable.account)
          val roleId = RoleMappingTable.getId(role[V2AccountRole.role])
          this[V3AccountRole.account] = accountId
          this[V3AccountRole.role] = roleId
        }
      }
    }
  }
}
