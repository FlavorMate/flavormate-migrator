/* Licensed under AGPLv3 2024 - 2025 */
package de.flavormate.extensions

fun String?.trimToNull(): String? {
  return this?.trim()?.takeIf { it.isNotEmpty() }
}
