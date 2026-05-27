package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.domain.entity.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteMovie(
    @SerialName("Title") val title: String? = null,
    @SerialName("Plot") val plot: String? = null,
    @SerialName("Released") val released: String? = null,
    @SerialName("Year") val year: String? = null,
    @SerialName("Poster") val poster: String? = null,
    @SerialName("Language") val language: String? = null,
    @SerialName("Metascore") val metaScore: String? = null,
    val imdbRating: String? = null,
    @SerialName("Response") val response: String,
    @SerialName("Error") val error: String? = null
) {
    fun toDomainMovie(): Movie {
        val parsedPopularity = imdbRating?.toDoubleOrNull() ?: 0.0
        val parsedVoteAverage = if (metaScore != null && metaScore != "N/A" && metaScore.isNotBlank()) {
            metaScore.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }

        val finalReleaseDate = when {
            released != null && released != "N/A" && released.isNotBlank() -> released
            year != null && year != "N/A" && year.isNotBlank() -> year
            else -> ""
        }

        return Movie(
            id = (title ?: "").hashCode(),
            title = title ?: "",
            overview = plot ?: "",
            releaseDate = finalReleaseDate,
            poster = if (poster != "N/A" && poster != null) poster else "",
            backdrop = if (poster != "N/A" && poster != null) poster else null,
            originalTitle = title ?: "",
            originalLanguage = if (language != "N/A" && language != null) language else "",
            popularity = parsedPopularity,
            voteAverage = parsedVoteAverage
        )
    }
}
