@file:Suppress("FunctionName")

package edu.dyds.movies.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import edu.dyds.movies.di.MoviesDependencyInjector.getDetailViewModel
import edu.dyds.movies.di.MoviesDependencyInjector.getHomeViewModel
import edu.dyds.movies.presentation.detail.DetailScreen
import edu.dyds.movies.presentation.home.HomeScreen

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private const val HOME = "home"

private const val DETAIL = "detail"

private const val MOVIE_TITLE = "movieTitle"

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = HOME) {
        homeDestination(navController)
        detailDestination(navController)
    }
}

private fun NavGraphBuilder.homeDestination(navController: NavHostController) {
    composable(HOME) {
        HomeScreen(
            viewModel = getHomeViewModel(),
            onGoodMovieClick = {
                val encodedTitle = URLEncoder.encode(it.title, StandardCharsets.UTF_8.toString())
                navController.navigate("$DETAIL/$encodedTitle")
            }
        )
    }
}

private fun NavGraphBuilder.detailDestination(navController: NavHostController) {
    composable(
        route = "$DETAIL/{$MOVIE_TITLE}",
        arguments = listOf(navArgument(MOVIE_TITLE) { type = NavType.StringType })
    ) { backstackEntry ->
        val encodedTitle = backstackEntry.arguments?.getString(MOVIE_TITLE) ?: ""
        val decodedTitle = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())

        DetailScreen(getDetailViewModel(), decodedTitle, onBack = { navController.popBackStack() })
    }
}
