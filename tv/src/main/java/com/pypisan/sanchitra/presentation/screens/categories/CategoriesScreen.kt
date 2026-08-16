package com.pypisan.sanchitra.presentation.screens.categories

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.data.entities.IPTVCategoryDto
import com.pypisan.sanchitra.presentation.Screens
import com.pypisan.sanchitra.presentation.common.Loading
import com.pypisan.sanchitra.presentation.common.MovieCard
import com.pypisan.sanchitra.presentation.screens.common.CommonErrorScreen
import com.pypisan.sanchitra.presentation.screens.dashboard.TopBarFocusRequesters
import com.pypisan.sanchitra.presentation.screens.dashboard.TopBarTabs
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.utils.GradientBg
import kotlinx.coroutines.yield

@Composable
fun CategoriesScreen(
    gridColumns: Int = 4,
    onCategoryClick: (categoryId: String) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    categoriesScreenViewModel: CategoriesScreenViewModel = hiltViewModel()
) {

    val uiState by categoriesScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is CategoriesScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        is CategoriesScreenUiState.Ready -> {
            Catalog(
                gridColumns = gridColumns,
                iptvCategories = s.categoryList,
                onCategoryClick = onCategoryClick,
                onScroll = onScroll,
                modifier = Modifier.fillMaxSize()
            )
        }

        is CategoriesScreenUiState.Error -> {
            CommonErrorScreen(
                message = s.message
            )
        }
    }
}

@Composable
private fun Catalog(
    iptvCategories: List<IPTVCategoryDto>,
    modifier: Modifier = Modifier,
    gridColumns: Int = 4,
    onCategoryClick: (categoryId: String) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
) {
    val childPadding = rememberChildPadding()
    val lazyGridState = rememberLazyGridState()
    val lifecycleOwner = LocalLifecycleOwner.current

    val categoriesTabIndex = remember { TopBarTabs.indexOf(Screens.Categories) }
    val categoriesTabFocusRequester = remember(categoriesTabIndex) {
        TopBarFocusRequesters.getOrNull(categoriesTabIndex + 1)
    }

    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(-1) }
    val focusRequesters = remember(iptvCategories) {
        List(iptvCategories.size) { FocusRequester() }
    }

    LaunchedEffect(lifecycleOwner, iptvCategories) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (lastFocusedIndex >= 0 && lastFocusedIndex in iptvCategories.indices) {
                lazyGridState.scrollToItem(lastFocusedIndex)
                yield()
                try {
                    focusRequesters.getOrNull(lastFocusedIndex)?.requestFocus()
                } catch (e: Exception) {
                    // Safe fallback
                }
            }
        }
    }

    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset < 100
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }


    val targetIndex1 = if (lastFocusedIndex >= 0) lastFocusedIndex else 0
    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(gridColumns),
        modifier = modifier
                    .fillMaxSize()
            .focusRestorer(focusRequesters.getOrNull(targetIndex1) ?: FocusRequester.Default),
        contentPadding = PaddingValues(
            start = childPadding.start,
            top = childPadding.top,
            end = childPadding.end,
            bottom = childPadding.bottom + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(
            items = iptvCategories,
            key = { _, item -> item.name }
        ) { index, movieCategory ->
            var isFocused by remember { mutableStateOf(false) }

            // TV Animated Scale & Alpha for polished feel
            val scale by animateFloatAsState(
                targetValue = if (isFocused) 1.05f else 1.0f,
                animationSpec = tween(durationMillis = 200),
                label = "cardScale"
            )
            val bgAlpha by animateFloatAsState(
                targetValue = if (isFocused) 0.75f else 0.25f,
                animationSpec = tween(durationMillis = 200),
                label = "bgAlpha"
            )

            MovieCard(
                onClick = { onCategoryClick(movieCategory.name) },
                modifier = Modifier
                    .aspectRatio(16 / 9f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) {
                            lastFocusedIndex = index
                        }
                    }
                    .focusRequester(focusRequesters.getOrElse(index) { FocusRequester() })
                    .focusProperties {
                        if (index < gridColumns && categoriesTabFocusRequester != null) {
                            up = categoriesTabFocusRequester
                        }
                        if (index % gridColumns == 0) {
                            left = FocusRequester.Cancel
                        }
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(bgAlpha)
                    ) {
                        GradientBg()
                    }

                    Text(
                        text = movieCategory.name,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .then(
                                if (isFocused) Modifier.basicMarquee(iterations = Int.MAX_VALUE)
                                else Modifier
                            ),
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}
