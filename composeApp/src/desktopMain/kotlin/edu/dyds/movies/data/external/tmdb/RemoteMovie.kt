package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.domain.entity.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteResult(
    val page: Int,
    val results: List<RemoteMovie>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

@Serializable
data class RemoteMovie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("original_language") val originalLanguage: String,
    val popularity: Double? = null,
    @SerialName("vote_average") val voteAverage: Double? = null
) {
    fun toDomainMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview,
            releaseDate = releaseDate ?: "",
            poster = posterPath?.let { "https://image.tmdb.org/t/p/w185$it" } ?: "",
            backdrop = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
            originalTitle = originalTitle,
            originalLanguage = originalLanguage,
            popularity = popularity ?: 0.0,
            voteAverage = voteAverage ?: 0.0
        )
    }
}
