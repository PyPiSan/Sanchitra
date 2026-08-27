package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import com.pypisan.sanchitra.presentation.theme.SanchitraCardShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.SwitchDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.pypisan.sanchitra.data.util.StringConstants

@Composable
fun SubtitlesSection(
    isSubtitlesChecked: Boolean,
    onSubtitleCheckChange: (isChecked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstItemFocusRequester = remember { FocusRequester() }

    with(StringConstants.Composable.Placeholders) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp)
                .verticalScroll(rememberScrollState())
                .focusRestorer(firstItemFocusRequester)
                .focusGroup()
                .padding(top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section Title
            Text(
                text = SubtitlesSectionTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Subtitle Toggle Switch Item
            ListItem(
                selected = false,
                onClick = {
                    val newState = !isSubtitlesChecked
                    //  Update Global Variable
                    StringConstants.Profile.isSubtitleOn = newState
                    // Trigger State update
                    onSubtitleCheckChange(newState)
                },
                trailingContent = {
                    Switch(
                        checked = isSubtitlesChecked,
                        onCheckedChange = { newState ->
                            StringConstants.Profile.isSubtitleOn = newState
                            onSubtitleCheckChange(newState)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                headlineContent = {
                    Text(
                        text = SubtitlesSectionSubtitlesItem,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(firstItemFocusRequester)
                    .focusProperties {
                        // Prevents escaping UP into top navigation bar
                        up = FocusRequester.Cancel
                    },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                    focusedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                ),
                shape = ListItemDefaults.shape(shape = MaterialTheme.shapes.small),
                scale = ListItemDefaults.scale(focusedScale = 1.02f)
            )

            // Language Selection Item (Revealed ONLY when Subtitles are ON)
            AnimatedVisibility(
                visible = isSubtitlesChecked,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ListItem(
                    selected = false,
                    onClick = {},
                    trailingContent = {
                        Text(
                            text = SubtitlesSectionLanguageValue,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    headlineContent = {
                        Text(
                            text = SubtitlesSectionLanguageItem,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                        focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
                        focusedContentColor = MaterialTheme.colorScheme.inverseOnSurface
                    ),
                    shape = ListItemDefaults.shape(shape = MaterialTheme.shapes.small),
                    scale = ListItemDefaults.scale(focusedScale = 1.02f)
                )
            }
        }
    }
}
