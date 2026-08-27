package com.pypisan.sanchitra.presentation.screens.profile


import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.pypisan.sanchitra.data.util.StringConstants

@Composable
fun SearchHistorySection(
    modifier: Modifier = Modifier,
    searchHistory: List<String> = emptyList(),
    onSearchItemClick: (String) -> Unit = {},
    onClearHistory: () -> Unit = {}
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringConstants.Composable.Placeholders.SearchHistorySectionTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (searchHistory.isNotEmpty()) {
                    Button(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.error,
                            focusedContentColor = MaterialTheme.colorScheme.onError
                        ),
                        scale = ButtonDefaults.scale(focusedScale = 1.05f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = StringConstants.Composable.Placeholders.SearchHistoryClearAll,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        if (searchHistory.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = StringConstants.Composable.Placeholders.NoSearchHistory,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // --- SEARCH HISTORY ITEMS ---
            itemsIndexed(
                items = searchHistory,
                key = { _, item -> item }
            ) { index, item ->
                ListItem(
                    selected = false,
                    onClick = { onSearchItemClick(item) },
                    headlineContent = {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Normal
                            )
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (index == 0) Modifier.focusRequester(firstItemFocusRequester)
                            else Modifier
                        )
                        .focusProperties {
                            // Block escaping up to the top navigation tabs from the top item
                            if (index == 0) {
                                up = FocusRequester.Cancel
                            }
                        },
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
