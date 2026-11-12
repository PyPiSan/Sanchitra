package com.pypisan.sanchitra.presentation.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pypisan.sanchitra.presentation.Screens
import com.pypisan.sanchitra.presentation.screens.categories.CategoriesScreen
import com.pypisan.sanchitra.presentation.screens.movies.MoviesScreen
import com.pypisan.sanchitra.presentation.screens.profile.ProfileScreen
import com.pypisan.sanchitra.presentation.screens.home.HomeScreen
import com.pypisan.sanchitra.presentation.screens.livetv.TVScreen
import com.pypisan.sanchitra.presentation.screens.search.SearchScreen
import com.pypisan.sanchitra.utils.Padding

val ParentPadding = PaddingValues(vertical = 16.dp, horizontal = 58.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current): Padding {
    return remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction) + 8.dp,
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction) + 8.dp,
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DashboardScreen(
    openCategoryIPTVList: (categoryName: String) -> Unit,
    openMovieDetailsScreen: (movieId: String) -> Unit,
    openTVPlayer: (channelId: Int) -> Unit,
    openIPTVPlayer: (channelId: Int) -> Unit,
    isComingBackFromDifferentScreen: Boolean,
    resetIsComingBackFromDifferentScreen: () -> Unit,
    onBackPressed: () -> Unit
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val navController = rememberNavController()

    var isTopBarVisible by remember { mutableStateOf(true) }
    var isTopBarFocused by remember { mutableStateOf(false) }

    var currentDestination: String? by remember { mutableStateOf(null) }

    val currentTopBarSelectedTabIndex by remember(currentDestination) {
        derivedStateOf {
            currentDestination?.let { TopBarTabs.indexOf(Screens.valueOf(it)) } ?: 0
        }
    }

    val isAtHomeScreen = currentDestination == Screens.Home()

    // Flag to request focus on the Home tab after popping back
    var shouldFocusHomeTab by remember { mutableStateOf(false) }

    LaunchedEffect(currentDestination, shouldFocusHomeTab) {
        if (shouldFocusHomeTab && isAtHomeScreen) {
            TopBarFocusRequesters.getOrNull(1)?.requestFocus()
            shouldFocusHomeTab = false
        }
    }

    DisposableEffect(Unit) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    // Consolidated Back Press Logic
    val handleCustomBack: () -> Unit = {
        val activeTabRequesterIndex = if (currentTopBarSelectedTabIndex >= 0) {
            currentTopBarSelectedTabIndex + 1
        } else {
            1
        }

        if (!isTopBarVisible) {
            // 1. If TopBar is scrolled away, show it and focus the active tab
            isTopBarVisible = true
            TopBarFocusRequesters.getOrNull(activeTabRequesterIndex)?.requestFocus()
        } else if (!isTopBarFocused) {
            // 2. Focus is on ANY card/content (Movies, TV, Categories, Search, etc.):
            // Move focus to that screen's tab in the TopBar
            TopBarFocusRequesters.getOrNull(activeTabRequesterIndex)?.requestFocus()
        } else if (!isAtHomeScreen) {
            // 3. TopBar IS focused, and we are not on Home: pop to Home
            navController.popBackStack(Screens.Home(), inclusive = false)
            shouldFocusHomeTab = true
        } else {
            // 4. On Home tab with TopBar focused: Exit app
            onBackPressed()
        }
    }
    BackPressHandledArea(
        onBackPressed = handleCustomBack
    )  {
        // We do not want to focus the TopBar everytime we come back from another screen e.g.
        var wasTopBarFocusRequestedBefore by rememberSaveable { mutableStateOf(false) }

        var topBarHeightPx: Int by rememberSaveable { mutableIntStateOf(0) }

        // Used to show/hide DashboardTopBar
        val topBarYOffsetPx by animateIntAsState(
            targetValue = if (isTopBarVisible) 0 else -topBarHeightPx,
            animationSpec = tween(),
            label = "",
            finishedListener = {
                if (it == -topBarHeightPx && isComingBackFromDifferentScreen) {
                    focusManager.moveFocus(FocusDirection.Down)
                    resetIsComingBackFromDifferentScreen()
                }
            }
        )

        // Used to push down/pull up NavHost when DashboardTopBar is shown/hidden
        val navHostTopPaddingDp by animateDpAsState(
            targetValue = if (isTopBarVisible) with(density) { topBarHeightPx.toDp() } else 0.dp,
            animationSpec = tween(),
            label = "",
        )

        LaunchedEffect(Unit) {
            if (!wasTopBarFocusRequestedBefore) {
                TopBarFocusRequesters[currentTopBarSelectedTabIndex + 1].requestFocus()
                wasTopBarFocusRequestedBefore = true
            }
        }

        DashboardTopBar(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = topBarYOffsetPx) }
                .onSizeChanged { topBarHeightPx = it.height }
                .onFocusChanged {
                    isTopBarFocused = it.hasFocus
                }
                // CRITICAL FIX: Redirect focus when entering the TopBar from below
                .focusProperties {
                    enter = {
                        val activeTabIndex = if (currentTopBarSelectedTabIndex >= 0) {
                            currentTopBarSelectedTabIndex + 1
                        } else {
                            1
                        }
                        TopBarFocusRequesters.getOrNull(activeTabIndex) ?: FocusRequester.Default
                    }
                }
                .padding(
                    horizontal = ParentPadding.calculateStartPadding(
                        LocalLayoutDirection.current
                    ) + 8.dp
                )
                .padding(
                    top = ParentPadding.calculateTopPadding(),
                    bottom = ParentPadding.calculateBottomPadding()
                ),
            selectedTabIndex = currentTopBarSelectedTabIndex,
        ) { screen ->
            val targetRoute = screen()
            if (currentDestination != targetRoute) {
                navController.navigate(targetRoute) {
                // Pop up to the start destination (Home) to avoid stacking tabs
                popUpTo(Screens.Home()) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
                }
            }
        }


        Body(
            openCategoryIPTVList = openCategoryIPTVList,
            openMovieDetailsScreen = openMovieDetailsScreen,
            openTVPlayer = openTVPlayer,
            openIPTVPlayer = openIPTVPlayer,
            updateTopBarVisibility = { isTopBarVisible = it },
            onBackFromAnyTab = handleCustomBack, // <--- HERE
            isTopBarVisible = isTopBarVisible,
            navController = navController,
            modifier = Modifier.offset(y = navHostTopPaddingDp),
        )
    }
}

//@Composable
//private fun BackPressHandledArea(
//    onBackPressed: () -> Unit,
//    modifier: Modifier = Modifier,
//    content: @Composable BoxScope.() -> Unit,
//) =
//    Box(
//        modifier = Modifier
//            .onPreviewKeyEvent {
//                if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
//                    onBackPressed()
//                    true
//                } else {
//                    false
//                }
//            }
//            .then(modifier),
//        content = content
//    )


@Composable
private fun BackPressHandledArea(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) =
    Box(
        modifier = Modifier
            .onPreviewKeyEvent { event ->
                if (event.key == Key.Back) {
                    if (event.type == KeyEventType.KeyDown) {
                        onBackPressed()
                    }
                    true
                } else {
                    false
                }
            }
            .then(modifier),
        content = content
    )


// Helper: Wraps any screen in our custom back handler automatically
private fun NavGraphBuilder.tabComposable(
    route: String,
    onBack: () -> Unit,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route) { entry ->
        BackHandler(enabled = true, onBack = onBack)
        content(entry)
    }
}

//@Composable
//private fun Body(
//    openCategoryIPTVList: (categoryName: String) -> Unit,
//    openMovieDetailsScreen: (movieId: String) -> Unit,
//    openTVPlayer: (channelId: Int) -> Unit,
//    openIPTVPlayer: (channelId: Int) -> Unit,
//    updateTopBarVisibility: (Boolean) -> Unit,
//    modifier: Modifier = Modifier,
//    navController: NavHostController = rememberNavController(),
//    isTopBarVisible: Boolean = true,
//) =
//    NavHost(
//        modifier = modifier,
//        navController = navController,
//        startDestination = Screens.Home(),
//    ) {
//        composable(Screens.Profile()) {
//            ProfileScreen()
//        }
//        composable(Screens.Home()) {
//            HomeScreen(
//                onMovieClick = { movie -> openMovieDetailsScreen(movie.id.toString())
//                },
//                goToTVPlayer = { channel -> openTVPlayer(channel) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
//        composable(Screens.Categories()) {
//            CategoriesScreen(
//                onCategoryClick = openCategoryIPTVList,
//                onScroll = updateTopBarVisibility
//            )
//        }
//        composable(Screens.Movies()) {
//            MoviesScreen(
//                onMovieClick = { movie -> openMovieDetailsScreen(movie.id.toString()) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
//        composable(Screens.TV()) {
//            TVScreen(
//                goToTVPlayer = { channel -> openTVPlayer(channel) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
////        composable(Screens.Shows()) {
////            ShowsScreen(
////                onMovieClick = openMovieDetailsScreen,
////                onScroll = updateTopBarVisibility,
////                isTopBarVisible = isTopBarVisible
////            )
////        }
//        composable(Screens.Search()) {
//            SearchScreen(
//                onMovieClick = { movie -> openMovieDetailsScreen(movie.id.toString())
//                },
//                goToIPTVPlayer = { channel -> openIPTVPlayer(channel) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
//    }


@Composable
private fun Body(
    openCategoryIPTVList: (categoryName: String) -> Unit,
    openMovieDetailsScreen: (movieId: String) -> Unit,
    openTVPlayer: (channelId: Int) -> Unit,
    openIPTVPlayer: (channelId: Int) -> Unit,
    updateTopBarVisibility: (Boolean) -> Unit,
    onBackFromAnyTab: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isTopBarVisible: Boolean = true,
) =
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Home(),
    ) {
        tabComposable(Screens.Profile(), onBack = onBackFromAnyTab) {
            ProfileScreen()
        }
        tabComposable(Screens.Home(), onBack = onBackFromAnyTab) {
            HomeScreen(
                onMovieClick = { movie -> openMovieDetailsScreen(movie.id.toString()) },
                goToTVPlayer = { channel -> openTVPlayer(channel) },
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
        tabComposable(Screens.Categories(), onBack = onBackFromAnyTab) {
            CategoriesScreen(
                onCategoryClick = openCategoryIPTVList,
                onScroll = updateTopBarVisibility
            )
        }
        tabComposable(Screens.Movies(), onBack = onBackFromAnyTab) {
            MoviesScreen(
                onMovieClick = { movie -> openMovieDetailsScreen(movie.id.toString()) },
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
        tabComposable(Screens.TV(), onBack = onBackFromAnyTab) {
            TVScreen(
                goToTVPlayer = { channel -> openTVPlayer(channel) },
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
        tabComposable(Screens.Search(), onBack = onBackFromAnyTab) {
            SearchScreen(
                onMovieClick = { movie -> openMovieDetailsScreen(movie.id.toString()) },
                goToIPTVPlayer = { channel -> openIPTVPlayer(channel) },
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
    }