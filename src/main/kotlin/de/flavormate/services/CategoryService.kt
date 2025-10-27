/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.services

import de.flavormate.databases.TargetDbInstance
import org.jetbrains.exposed.sql.transactions.transaction

object CategoryService {

  fun updateCovers() {
    transaction(TargetDbInstance.getInstance()) {
      val query =
        """
            UPDATE public.v3__category c
            SET cover_recipe = sub.recipe_id
            FROM (
                SELECT DISTINCT ON (rb.category_id) rb.category_id, r.id AS recipe_id
                FROM public.v3__category__recipe rb
                JOIN public.v3__recipe r on rb.recipe_id = r.id
                WHERE r.cover_file IS NOT NULL
                ORDER BY rb.category_id, r.id
            )
            AS sub
            WHERE c.id = sub.category_id;
        """
          .trimIndent()
      exec(query)
    }
  }
}
