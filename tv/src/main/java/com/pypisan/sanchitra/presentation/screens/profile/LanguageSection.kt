package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.data.util.StringConstants


@Composable
fun LanguageSection(
    languages: List<String>,
    selectedLanguages: List<String>,
    onLanguageToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstItemFocusRequester = remember { FocusRequester() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .focusRestorer(firstItemFocusRequester)
            .focusGroup(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = StringConstants.Composable.Placeholders.LanguageSectionTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        itemsIndexed(
            items = languages,
            key = { _, lang -> lang }
        ) { index, language ->
            val isSelected = language in selectedLanguages

            ListItem(
                selected = isSelected,
                onClick = { onLanguageToggle(language) },
                headlineContent = {
                    Text(
                        text = language,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                trailingContent = if (isSelected) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(
                                id = R.string.language_section_listItem_icon_content_description,
                                language
                            ),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (index == 0) Modifier.focusRequester(firstItemFocusRequester)
                        else Modifier
                    )
                    .focusProperties {
                        // Prevent focus from escaping to top bar when scrolling to top of language list
                        if (index == 0) {
                            up = FocusRequester.Cancel
                        }
                    },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    selectedContainerColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.inverseSurface
                ),
                shape = ListItemDefaults.shape(shape = MaterialTheme.shapes.small),
                scale = ListItemDefaults.scale(focusedScale = 1.02f)
            )
        }
    }
}
