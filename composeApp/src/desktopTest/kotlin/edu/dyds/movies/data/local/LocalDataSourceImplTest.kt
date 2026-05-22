package edu.dyds.movies.data.local

import edu.dyds.movies.domain.entity.Movie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class LocalDataSourceImplTest {

	@Test
	fun `inicia vacio`() {
		val localDataSource = LocalDataSourceImpl()

		assertTrue(localDataSource.movies.isEmpty())
	}

	@Test
	fun `saveMovies actualiza cache con lista de dos movies`() = runTest {
		val localDataSource = LocalDataSourceImpl()
		val movies = listOf(buildMovie(1), buildMovie(2))

		localDataSource.saveMovies(movies)

		assertEquals(2, localDataSource.movies.size)
		assertEquals(movies, localDataSource.movies)
	}

	private fun buildMovie(id: Int): Movie {
		return Movie(
			id = id,
			title = "Movie $id",
			overview = "Overview $id",
			releaseDate = "2024-01-01",
			poster = "poster$id",
			backdrop = "backdrop$id",
			originalTitle = "Original $id",
			originalLanguage = "en",
			popularity = 7.5,
			voteAverage = 6.7
		)
	}
}
