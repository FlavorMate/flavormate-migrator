/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import org.jetbrains.exposed.sql.Table

object V2BookSubscriber : Table("book_subscriber") {
  val author = reference("author_id", V2Author.id)
  val book = reference("book_id", V2Book.id)

  override val primaryKey = PrimaryKey(author, book)
}
