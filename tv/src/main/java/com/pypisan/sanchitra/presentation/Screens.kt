package com.pypisan.sanchitra.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.pypisan.sanchitra.presentation.screens.categories.CategoryIPTVListScreen
import com.pypisan.sanchitra.presentation.screens.movies.MovieDetailsScreen
import com.pypisan.sanchitra.presentation.screens.videoPlayer.TVPlayerScreen
import com.pypisan.sanchitra.presentation.screens.videoPlayer.VideoPlayerScreen

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null
) {
    Profile,
    Home(isTabItem = true),
    TV(isTabItem = true),
    Categories(isTabItem = true),
    Movies(isTabItem = true),

    Favourites(isTabItem = false),
    Search(isTabItem = true, tabIcon = Icons.Default.Search),
    CategoryIPTVList(listOf(CategoryIPTVListScreen.CategoryNameKey)),
    MovieDetails(
        listOf(MovieDetailsScreen.MovieIdBundleKey)
    ),
    Dashboard,
    TVPlayer(listOf(TVPlayerScreen.TVIdBundleKey)),
    VideoPlayer(
        listOf(VideoPlayerScreen.IPTVStreamIdBundleKey)
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
