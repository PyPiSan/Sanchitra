package com.pypisan.sanchitra.presentation.screens.mediamanager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import kotlinx.coroutines.yield

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ManageMedia(
    modifier: Modifier = Modifier,
    gridColumns: Int = 2,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    topBarFocusRequester: FocusRequester? = null,
) {

    val viewModel: ManageMediaViewModel = hiltViewModel()
    val childPadding = rememberChildPadding()
    val lazyGridState = rememberLazyGridState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var activeDialog by remember { mutableStateOf<MediaActionType?>(null) }

    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()
    val importError by viewModel.importError.collectAsStateWithLifecycle()

    val cards = remember {
        listOf(
            MediaActionCardData(
                type = MediaActionType.IMPORT_URL,
                title = "Import Media/Channels from URL",
                subtitle = "Load M3U, M3U8, or JSON playlists from the web",
                buttonText = "Import",
                icon = Icons.Default.Add,
                accentColor = Color(0xFFFFE082)
            ),
            MediaActionCardData(
                type = MediaActionType.IMPORT_LOCAL,
                title = "Import from Local Disk",
                subtitle = "Pick playlists or video files from TV or USB storage",
                buttonText = "Browse",
                icon = Icons.Default.List,
                accentColor = Color(0xFF81C784)
            ),
            MediaActionCardData(
                type = MediaActionType.TEST_STREAM,
                title = "Test Streaming Link",
                subtitle = "Play and verify HLS, DASH, or direct media URLs",
                buttonText = "Test",
                icon = Icons.Default.PlayArrow,
                accentColor = Color(0xFFFF5722)
            ),
            MediaActionCardData(
                type = MediaActionType.TEST_DRM,
                title = "Test DRM Link",
                subtitle = "Verify Widevine, ClearKey, or PlayReady license streams",
                buttonText = "Test DRM",
                icon = Icons.Default.Lock,
                accentColor = Color(0xFFEF5350)
            )
        )
    }

    var lastFocusedIndex by rememberSaveable { mutableIntStateOf(-1) }
    val focusRequesters = remember(cards) {
        List(cards.size) { FocusRequester() }
    }

    // Scroll header back into view when navigating up to Row 1
    LaunchedEffect(lastFocusedIndex) {
        if (lastFocusedIndex in 0 until gridColumns) {
            lazyGridState.animateScrollToItem(0)
        }
    }

    // Restore D-pad focus when returning to this screen
    LaunchedEffect(lifecycleOwner, cards) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (lastFocusedIndex >= 0 && lastFocusedIndex in cards.indices) {
                val targetGridIndex = if (lastFocusedIndex < gridColumns) 0 else lastFocusedIndex + 1
                lazyGridState.scrollToItem(targetGridIndex)
                yield()
                try {
                    focusRequesters.getOrNull(lastFocusedIndex)?.requestFocus()
                } catch (_: Exception) {
                    // Safe fallback
                }
            }
        }
    }

    // Show TopBar when header/Row 1 is in view, hide when navigating down to Row 2
    val isTopBarVisible by remember(gridColumns) {
        derivedStateOf {
            val isAtTop = lazyGridState.firstVisibleItemIndex == 0 &&
                    lazyGridState.firstVisibleItemScrollOffset < 20
            val isOnFirstRow = lastFocusedIndex < gridColumns
            isAtTop && isOnFirstRow
        }
    }

    LaunchedEffect(isTopBarVisible) {
        onScroll(isTopBarVisible)
    }

    val targetIndex = if (lastFocusedIndex >= 0) lastFocusedIndex else 0

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Fixed(gridColumns),
        modifier = modifier
            .fillMaxSize()
            .focusRestorer(focusRequesters.getOrNull(targetIndex) ?: FocusRequester.Default),
        contentPadding = PaddingValues(
            start = childPadding.start,
            top = childPadding.top,
            end = childPadding.end,
            bottom = childPadding.bottom + 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Top Title + User Hints (spans full width and scrolls with the grid)
        item(
            key = "manage_media_header",
            span = { GridItemSpan(maxLineSpan) }
        ) {
            ManageMediaHeader()
        }

        // 2x2 Action Cards
        itemsIndexed(
            items = cards,
            key = { _, item -> item.type.name }
        ) { index, card ->
            MediaActionCard(
                data = card,
                onClick = { activeDialog = card.type },
                modifier = Modifier
                    .onFocusChanged {
                        if (it.isFocused) {
                            lastFocusedIndex = index
                        }
                    }
                    .focusRequester(focusRequesters.getOrElse(index) { FocusRequester() })
                    .focusProperties {
                        if (index < gridColumns && topBarFocusRequester != null) {
                            up = topBarFocusRequester
                        }
                        if (index % gridColumns == 0) {
                            left = FocusRequester.Cancel
                        }
                    }
            )
        }
    }

    // Dialogs
    when (activeDialog) {
        MediaActionType.IMPORT_URL -> ImportUrlDialog(
            isLoading = isImporting,
            serverError = importError,
            onDismiss = {
                viewModel.clearError()
                activeDialog = null
            },
            onConfirm = { name, url ->
                viewModel.importFromUrl(name = name, url = url) {
                    activeDialog = null // Closes automatically once saved in Room DB
                }
            }
        )

        MediaActionType.IMPORT_LOCAL -> ImportLocalDiskDialog(
            onDismiss = {
                viewModel.clearError()
                activeDialog = null
            },
            onConfirm = { uri ->
                viewModel.importFromLocal(uri) {
                    activeDialog = null
                }
            }
        )

        MediaActionType.TEST_STREAM -> TestStreamDialog(
            onDismiss = { activeDialog = null },
            onConfirm = { streamUrl ->
//                onTestStream(streamUrl)
                activeDialog = null
            }
        )

        MediaActionType.TEST_DRM -> TestDrmDialog(
            onDismiss = { activeDialog = null },
            onConfirm = { streamUrl, licenseUrl, scheme ->
//                onTestDrm(streamUrl, licenseUrl, scheme)
                activeDialog = null
            }
        )

        null -> Unit
    }
}

@Composable
private fun ManageMediaHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Text(
            text = "Manage Media",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Import channel playlists from web or USB/local storage, or test stream & DRM links before saving.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Subtle TV Hint Bar
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1A1A22),
            border = BorderStroke(1.dp, Color(0xFFFFE082).copy(alpha = 0.28f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Hint",
                    tint = Color(0xFFFFE082),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tip: Supports M3U, M3U8, JSON, HLS, DASH & Widevine/ClearKey. Use your remote D-pad to choose an option below.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }
    }
}