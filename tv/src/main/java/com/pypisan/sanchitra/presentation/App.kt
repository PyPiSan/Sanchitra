package com.pypisan.sanchitra.presentation

import android.os.Build
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

    var isDetailsOpen by remember { mutableStateOf(false) }
    var activePlayer by rememberSaveable { mutableStateOf<PlayerState>(PlayerState.Idle) }

    Box(modifier = Modifier.fillMaxSize()) {

        // LAYER 1 (BOTTOM): NAV HOST
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

        // LAYER 2 (MIDDLE): VIDEO DETAIL SCREEN
        if (isDetailsOpen) {
            Dialog(
                onDismissRequest = { isDetailsOpen = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    usePlatformDefaultWidth = false
                )
            ) {
                MovieDetailsScreen(
                    isPlayerActive = activePlayer != PlayerState.Idle,
                    openVideoPlayer = { metaID -> activePlayer = PlayerState.Video(metaID) },
                    onBackPressed = { isDetailsOpen = false }
                )
            }
        }

        // LAYER 3 (TOP): PLAYER OVERLAYS (NATIVE DIALOGS)
        when (val state = activePlayer) {
            is PlayerState.Idle -> {}

            is PlayerState.TV -> {
                key(state.channelId) {
                    Dialog(
                        onDismissRequest = { activePlayer = PlayerState.Idle },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            usePlatformDefaultWidth = false // 🚨 Forces Full-Screen TV Overlay
                        )
                    ) {
                        TVPlayerScreen(
                            channelId = state.channelId,
                            onBackPressed = { activePlayer = PlayerState.Idle }
                        )
                    }
                }
            }

            is PlayerState.Video -> {
                key(state.metaId) {
                    Dialog(
                        onDismissRequest = { activePlayer = PlayerState.Idle },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        VideoPlayerScreen(
                            metaId = state.metaId,
                            onBackPressed = { activePlayer = PlayerState.Idle }
                        )
                    }
                }
            }

            is PlayerState.IPTV -> {
                key(state.channelId) {
                    Dialog(
                        onDismissRequest = { activePlayer = PlayerState.Idle },
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            usePlatformDefaultWidth = false
                        )
                    ) {
                        IPTVPlayerScreen(
                            channelId = state.channelId,
                            onBackPressed = { activePlayer = PlayerState.Idle }
                        )
                    }
                }
            }
        }
    }
}