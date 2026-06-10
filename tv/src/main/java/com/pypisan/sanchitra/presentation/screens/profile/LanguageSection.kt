package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.foundation.layout.PaddingValues
import com.pypisan.sanchitra.presentation.theme.JetStreamCardShape
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.StringConstants.Profile.userSelectedLanguage
import com.pypisan.sanchitra.data.util.StringConstants.Utils.LanguageSectionItems

@Composable
fun LanguageSection(
    onLanguageToggle: (String) -> Unit
) {
    with(StringConstants.Composable.Placeholders) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 72.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = LanguageSectionTitle,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            items(LanguageSectionItems.size) { index ->
                val language = LanguageSectionItems[index]
                val isSelected = language in userSelectedLanguage

                ListItem(
                    modifier = Modifier.padding(top = 16.dp),
                    selected = isSelected,
                    onClick = {
                        onLanguageToggle(language)
                    },
                    trailingContent = if (isSelected) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = stringResource(
                                    R.string.language_section_listItem_icon_content_description,
                                    language
                                )
                            )
                        }
                    } else null,
                    headlineContent = {
                        Text(
                            text = language,
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme
                            .surfaceColorAtElevation(4.dp)
                    ),
                    shape = ListItemDefaults.shape(
                        shape = JetStreamCardShape
                    )
                )
            }
        }
    }
}
