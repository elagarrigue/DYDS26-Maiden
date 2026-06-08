package edu.dyds.movies.domain

import java.util.Locale

internal fun normalizeTitle(title: String): String {
    return title
        .trim()
        .replace(Regex("\\s+"), " ")
        .lowercase(Locale.ROOT)
}