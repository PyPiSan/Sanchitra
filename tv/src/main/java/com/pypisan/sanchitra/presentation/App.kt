package com.pypisan.sanchitra.presentation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pypisan.sanchitra.presentation.common.PlayerState
import com.pypisan.sanchitra.presentation.screens.categories.CategoryIPTVListScreen
import com.pypisan.sanchitra.presentation.screens.dashboard.DashboardScreen
import com.pypisan.sanchitra.presentation.screens.movies.MovieDetailsScreen
import com.pypisan.sanchitra.presentation.screens.videoPlayer.IPTVPlayerScreen
import com.pypisan.sanchitra.presentation.screens.videoPlayer.TVPlayerScreen
import com.pypisan.sanchitra.presentation.screens.videoPlayer.VideoPlayerScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    onBackPressed: () -> Unit
) {
    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

    // 1. STATE FOR LAYER 2 (Details Screen)
    var isDetailsOpen by remember { mutableStateOf(false) }

    // 1. Track the active player overlay
    var activePlayer by rememberSaveable { mutableStateOf<PlayerState>(PlayerState.Idle) }

    // 2. Use a Box to layer the Player ON TOP of the NavHost
    Box(modifier = Modifier.fillMaxSize()) {

        NavHost(
            navController = navController,
            startDestination = Screens.Dashboard(),
            builder = {
                composable(
                    route = Screens.CategoryIPTVList(),
                    arguments = listOf(
                        navArgument(CategoryIPTVListScreen.CategoryNameKey) {
                            type = NavType.StringType
                        }
                    )
                ) {
                    CategoryIPTVListScreen(
                        isPlayerActive = activePlayer != PlayerState.Idle,
                        onBackPressed = {
                            if (navController.navigateUp()) {
                                isComingBackFromDifferentScreen = true
                            }
                        },
                        onChannelSelected = { iptvChannelId ->
                            // OPEN OVERLAY INSTEAD OF NAVIGATING!
                            activePlayer = PlayerState.IPTV(iptvChannelId)
                        }
                    )
                }

                composable(route = Screens.Dashboard()) {
                    DashboardScreen(
                        openCategoryIPTVList = { categoryName ->
                            navController.navigate(Screens.CategoryIPTVList.withArgs(categoryName))
                        },
                        openMovieDetailsScreen = { _ ->
                            isDetailsOpen = true
                        },
                        openVideoPlayer = { metaID ->
                            activePlayer = PlayerState.Video(metaID.toString())
                        },
                        openTVPlayer = { channelId ->
                            activePlayer = PlayerState.TV(channelId.toString())
                        },
                        onBackPressed = onBackPressed,
                        isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                        resetIsComingBackFromDifferentScreen = {
                            isComingBackFromDifferentScreen = false
                        }
                    )
                }
            }
        )
        // LAYER 2 (TOP): VIDEO DETAIL SCREEN
        if (isDetailsOpen) {
            // Intercept Back button: Close Details, return to Catalog
            BackHandler { isDetailsOpen = false }

            MovieDetailsScreen(
                isPlayerActive = activePlayer != PlayerState.Idle,
                openVideoPlayer = { metaID ->
                    activePlayer = PlayerState.Video(metaID)
                },
                onBackPressed = {
                    isDetailsOpen = false
                }
            )
        }

        // LAYER 3 (TOP): PLAYER OVERLAYS
        when (val state = activePlayer) {
            is PlayerState.Idle -> {
            }

            is PlayerState.TV -> {

                key(state.channelId) {
                    BackHandler { activePlayer = PlayerState.Idle }
                    TVPlayerScreen(
                        channelId = state.channelId,
                        onBackPressed = { activePlayer = PlayerState.Idle }
                    )
                }
            }

            is PlayerState.Video -> {
                key(state.metaId) {
                    BackHandler { activePlayer = PlayerState.Idle }
                    VideoPlayerScreen(
                        metaId = state.metaId,
                        onBackPressed = { activePlayer = PlayerState.Idle }
                    )
                }
            }

            is PlayerState.IPTV -> {
                key(state.channelId) {
                    BackHandler { activePlayer = PlayerState.Idle }
                    IPTVPlayerScreen(
                        channelId = state.channelId,
                        onBackPressed = { activePlayer = PlayerState.Idle }
                    )
                }
            }
        }
    }
}