/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

object V2Author : V2BaseEntity("authors") {

  val account = reference("account_id", V2Account.id)

  //     val subscribedBooks= reference("subscribed_books", )
  //
  //    val recipes = reference("recipes", )
  //
  //    val stories = reference("stories", )
}
