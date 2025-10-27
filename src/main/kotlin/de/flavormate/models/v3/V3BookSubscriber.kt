/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v3

import org.jetbrains.exposed.sql.Table

object V3BookSubscriber : Table("v3__book__subscriber") {
  val book = reference("book_id", V3Book.id)
  val subscriber = reference("account_id", V3Account.id)

  override val primaryKey = PrimaryKey(book, subscriber)
}
