
package com.example.sanchitra.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.sanchitra.presentation.screens.categories.CategoryMovieListScreen
import com.example.sanchitra.presentation.screens.movies.MovieDetailsScreen
import com.example.sanchitra.presentation.screens.videoPlayer.VideoPlayerScreen

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null
) {
    Profile,
    Home(isTabItem = true),
    Categories(isTabItem = true),
    Movies(isTabItem = true),
    TV(isTabItem = true),
    Favourites(isTabItem = true),
    Search(isTabItem = true, tabIcon = Icons.Default.Search),
    CategoryMovieList(listOf(CategoryMovieListScreen.CategoryIdBundleKey)),
    MovieDetails(listOf(MovieDetailsScreen.MovieIdBundleKey)),
    Dashboard,
    VideoPlayer(listOf(VideoPlayerScreen.MovieIdBundleKey)
    );

    operator fun invoke(): String {
        val argList = StringBuilder()
        args?.let { nnArgs ->
            nnArgs.forEach { arg -> argList.append("/{$arg}") }
        }
        return name + argList
    }

    fun withArgs(vararg args: Any): String {
        val destination = StringBuilder()
        args.forEach { arg -> destination.append("/$arg") }
        return name + destination
    }
}
