package com.pypisan.sanchitra.presentation.screens.profile

import android.util.Log
import androidx.activity.compose.BackHandler
import com.pypisan.sanchitra.presentation.theme.SanchitraTheme
import androidx.annotation.FloatRange
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.pypisan.sanchitra.storage.SearchHistoryManager
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.StringConstants.Profile.userSelectedLanguage
import com.pypisan.sanchitra.presentation.Screens
import com.pypisan.sanchitra.presentation.common.GradientProgressIndicator
import com.pypisan.sanchitra.presentation.screens.dashboard.TopBarFocusRequesters
import com.pypisan.sanchitra.presentation.screens.dashboard.TopBarTabs
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.utils.AnalyticsTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(
    @FloatRange(from = 0.0, to = 1.0) sidebarWidthFraction: Float = 0.32f,
    profileScreenViewModel: ProfileScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val childPadding = rememberChildPadding()
    val scope = rememberCoroutineScope()

    val sidebarFirstItemRequester = remember { FocusRequester() }

    var selectedTab by rememberSaveable { mutableStateOf(ProfileScreens.Accounts) }
    var isSidebarFocused by remember { mutableStateOf(false) }

    var searchHistory by remember { mutableStateOf<List<String>>(emptyList()) }
    var currentSelectedLanguages by remember { mutableStateOf(userSelectedLanguage) }
    var isScreenLoading by remember { mutableStateOf(false) }

    // --- Dialog State for Language Update ---
    var showLanguageDialog by remember { mutableStateOf(false) }
    var languageDialogTitle by remember { mutableStateOf("") }
    var languageDialogMessage by remember { mutableStateOf("") }

    var isSubtitlesChecked by rememberSaveable {
        mutableStateOf(StringConstants.Profile.isSubtitleOn)
    }

    val profileTabIndex = remember { TopBarTabs.indexOf(Screens.Profile) }
    val profileTabFocusRequester = remember(profileTabIndex) {
        TopBarFocusRequesters.getOrNull(profileTabIndex + 1)
    }

    LaunchedEffect("ProfileScreen") {
        AnalyticsTracker.logScreenView("ProfileScreen")
    }

    val historyManager = remember { SearchHistoryManager.getInstance(context) }

    LaunchedEffect(Unit) {
        searchHistory = historyManager.getSearchHistory()
    }

    val handleLanguageToggle: (String) -> Unit = { language ->
        val updatedList = if (language in currentSelectedLanguages) {
            currentSelectedLanguages - language
        } else {
            currentSelectedLanguages + language
        }
        currentSelectedLanguages = updatedList
        userSelectedLanguage = updatedList

        updateUserLanguageApi(
            scope = scope,
            profileScreenViewModel = profileScreenViewModel,
            newLanguages = updatedList,
            onResult = { isSuccess ->
                if (isSuccess) {
                    languageDialogTitle =
                        StringConstants.Composable.Placeholders.LanguageDialogSuccessTitle
                    languageDialogMessage =
                        StringConstants.Composable.Placeholders.LanguageDialogSuccessMessage
                } else {
                    languageDialogTitle =
                        StringConstants.Composable.Placeholders.LanguageDialogFailedTitle
                    languageDialogMessage =
                        StringConstants.Composable.Placeholders.LanguageDialogFailedMessage
                }
                showLanguageDialog = true
            })
    }

    LaunchedEffect(Unit) {
        sidebarFirstItemRequester.requestFocus()
    }

    BackHandler(enabled = !isSidebarFocused) {
        sidebarFirstItemRequester.requestFocus()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = childPadding.start, vertical = childPadding.top)
        ) {
            // --- LEFT COLUMN (SIDEBAR) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth(fraction = sidebarWidthFraction)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .onFocusChanged { isSidebarFocused = it.hasFocus }
                    .focusRestorer()
                    .focusGroup(),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileScreens.entries.forEachIndexed { index, screen ->
                    val isSelected = selectedTab == screen

                    key(screen.name) {
                        ListItem(
                            selected = isSelected,
                            onClick = {
                                selectedTab = screen
                            },
                            headlineContent = {
                                Text(
                                    text = screen.tabTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            trailingContent = {
                                Icon(
                                    imageVector = screen.icon,
                                    modifier = Modifier.size(20.dp),
                                    contentDescription = screen.tabTitle
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (index == 0) Modifier.focusRequester(
                                        sidebarFirstItemRequester
                                    )
                                    else Modifier
                                )
                                .focusProperties {
                                    if (index == 0) {
                                        up = profileTabFocusRequester ?: FocusRequester.Cancel
                                    }
                                    if (screen == ProfileScreens.About) {
                                        right = FocusRequester.Cancel
                                    }
                                    if (screen == ProfileScreens.SearchHistory && searchHistory.isEmpty()) {
                                        right = FocusRequester.Cancel
                                    }
                                }
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        selectedTab = screen
                                    }
                                },
                            scale = ListItemDefaults.scale(focusedScale = 1.02f),
                            colors = ListItemDefaults.colors(
                                containerColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                                focusedContentColor = MaterialTheme.colorScheme.surface,
                                selectedContainerColor = if (isSidebarFocused) {
                                    MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.35f)
                                } else {
                                    Color.Transparent
                                },
                                selectedContentColor = if (isSidebarFocused) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                }
                            ),
                            shape = ListItemDefaults.shape(shape = MaterialTheme.shapes.extraSmall)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // --- RIGHT COLUMN (CONTENT) ---
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .focusProperties {
                    up = FocusRequester.Cancel
                }
                .focusGroup()) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ProfileContentTransition"
                ) { screen ->
                    when (screen) {
                        ProfileScreens.Accounts -> AccountsSection(
                            onLoadingChange = { isScreenLoading = it }
                        )
                        ProfileScreens.About -> AboutSection()
                        ProfileScreens.Subtitles -> SubtitlesSection(
                            isSubtitlesChecked = isSubtitlesChecked,
                            onSubtitleCheckChange = { isSubtitlesChecked = it })

                        ProfileScreens.Language -> LanguageSection(
                            languages = StringConstants.Utils.LanguageSectionItems,
                            selectedLanguages = currentSelectedLanguages,
                            onLanguageToggle = handleLanguageToggle
                        )

                        ProfileScreens.SearchHistory -> SearchHistorySection(
                            searchHistory = searchHistory, onClearHistory = {
                                historyManager.clearSearchHistory()
                                searchHistory = emptyList()
                            })

                        ProfileScreens.HelpAndSupport -> HelpAndSupportSection()
                    }
                }
            }
        }

        if (isScreenLoading) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    GradientProgressIndicator()
                }
            }
        }

        LanguageUpdateDialog(
            showDialog = showLanguageDialog,
            title = languageDialogTitle,
            message = languageDialogMessage,
            onDismissRequest = { showLanguageDialog = false })
    }
}

fun updateUserLanguageApi(
    scope: CoroutineScope,
    profileScreenViewModel: ProfileScreenViewModel,
    newLanguages: List<String>,
    onResult: (isSuccess: Boolean) -> Unit
) {
//    Log.d("ProfileScreen", "Updating backend with selected languages: $newLanguages")
    scope.launch {
        val success = withContext(Dispatchers.IO) {
            profileScreenViewModel.userPrefUpdate(
                StringConstants.Composable.Placeholders.LanguageSectionTitle.lowercase(),
                newLanguages.toList()
            )
        }
        // Result is delivered back on the main thread
        onResult(success)
    }
}

@Preview(device = Devices.TV_1080p)
@Composable
fun ProfileScreenPreview() {
    SanchitraTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
            ProfileScreen()
        }
    }
}
