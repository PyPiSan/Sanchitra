package com.pypisan.sanchitra.presentation.screens.profile

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.pypisan.sanchitra.data.util.StringConstants


@Composable
fun HelpAndSupportSection(
    modifier: Modifier = Modifier,
    onFAQClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onContactClick: () -> Unit = {}
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
                // Extra bottom padding so last item is not clipped on TV screens
                .padding(top = 16.dp, bottom = 120.dp)
        ) {
            Text(
                text = HelpAndSupportSectionTitle,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 1. FAQ Item (Index 0: Blocks UP movement)
            HelpAndSupportSectionItem(
                title = HelpAndSupportSectionFAQItem,
                onClick = onFAQClick,
                modifier = Modifier
                    .focusRequester(firstItemFocusRequester)
                    .focusProperties {
                        // Blocks focus from escaping UP to the Top Tab Bar
                        up = FocusRequester.Cancel
                    }
            )

            // 2. Privacy Policy Item
            HelpAndSupportSectionItem(
                title = HelpAndSupportSectionPrivacyItem,
                onClick = onPrivacyClick
            )

            // 3. Contact Us Item
            HelpAndSupportSectionItem(
                title = HelpAndSupportSectionContactItem,
                value = HelpAndSupportSectionContactValue,
                onClick = onContactClick
            )
        }
    }
}

@Composable
private fun HelpAndSupportSectionItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String? = null,
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        selected = false,
        onClick = onClick,
        trailingContent = {
            value?.let { nnValue ->
                Text(
                    text = nnValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } ?: run {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    modifier = Modifier.size(20.dp),
                    contentDescription = StringConstants
                        .Composable
                        .Placeholders
                        .HelpAndSupportSectionListItemIconDescription
                )
            }
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            contentColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            focusedContentColor = MaterialTheme.colorScheme.inverseOnSurface
        ),
        shape = ListItemDefaults.shape(shape = MaterialTheme.shapes.small),
        scale = ListItemDefaults.scale(focusedScale = 1.02f)
    )
}
