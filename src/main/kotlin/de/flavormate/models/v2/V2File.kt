/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.models.v2

import de.flavormate.enums.v2.V2FileCategory
import de.flavormate.enums.v2.V2FileType

object V2File : V2BaseEntity("files") {

  val type = enumerationByName<V2FileType>("type", 255)

  val category = enumerationByName<V2FileCategory>("category", 255)

  val owner = reference("owner", V2Author.id)

  val recipeId = optReference("recipe_id", V2Recipe.id)
}
