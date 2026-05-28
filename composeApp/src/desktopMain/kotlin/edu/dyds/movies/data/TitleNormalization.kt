package edu.dyds.movies.data

import java.util.Locale

internal fun normalizeTitle(title: String): String {
    return title
        .trim()
        .replace(Regex("\\s+"), " ")
        .lowercase(Locale.ROOT)
}
