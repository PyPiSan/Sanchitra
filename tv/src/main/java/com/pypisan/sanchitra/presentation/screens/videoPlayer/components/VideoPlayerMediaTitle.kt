package com.pypisan.sanchitra.presentation.screens.videoPlayer.components

import com.pypisan.sanchitra.presentation.theme.SanchitraTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

enum class VideoPlayerMediaTitleType { AD, LIVE, DEFAULT }

@Composable
fun VideoPlayerMediaTitle(
    title: String,
    secondaryText: String,
    tertiaryText: String,
    modifier: Modifier = Modifier,
    type: VideoPlayerMediaTitleType = VideoPlayerMediaTitleType.DEFAULT
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(10.dp))

        when (type) {

            VideoPlayerMediaTitleType.LIVE -> {

                if (secondaryText.isNotEmpty() || tertiaryText.isNotEmpty()) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (secondaryText.isNotEmpty()) {

                            Text(
                                text = "Now Showing:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = secondaryText,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (secondaryText.isNotEmpty() && tertiaryText.isNotEmpty()) {

                            Spacer(Modifier.width(12.dp))

                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(Modifier.width(12.dp))
                        }

                        if (tertiaryText.isNotEmpty()) {

                            Text(
                                text = "Next:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = tertiaryText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            else -> {

                val subTitle = buildString {

                    append(secondaryText)

                    if (secondaryText.isNotEmpty() && tertiaryText.isNotEmpty()) {
                        append(" • ")
                    }

                    append(tertiaryText)
                }

                Text(
                    text = subTitle, style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(name = "TV Series", device = "id:tv_4k")
@Composable
private fun VideoPlayerMediaTitlePreviewSeries() {
    SanchitraTheme {
        Surface(shape = RectangleShape) {
            VideoPlayerMediaTitle(
                title = "True Detective",
                secondaryText = "S1E5",
                tertiaryText = "The Secret Fate Of All Life",
                type = VideoPlayerMediaTitleType.DEFAULT
            )
        }
    }
}

@Preview(name = "Live", device = "id:tv_4k")
@Composable
private fun VideoPlayerMediaTitlePreviewLive() {
    SanchitraTheme {
        Surface(shape = RectangleShape) {
            VideoPlayerMediaTitle(
                title = "MacLaren Reveal Their 2022 Car: The MCL36",
                secondaryText = "Formula 1",
                tertiaryText = "54K watching now",
                type = VideoPlayerMediaTitleType.LIVE
            )
        }
    }
}

@Preview(name = "Ads", device = "id:tv_4k")
@Composable
private fun VideoPlayerMediaTitlePreviewAd() {
    SanchitraTheme {
        Surface(shape = RectangleShape) {
            VideoPlayerMediaTitle(
                title = "Samsung Galaxy Note20 | Ultra 5G",
                secondaryText = "Get the most powerful Note yet",
                tertiaryText = "",
                type = VideoPlayerMediaTitleType.AD
            )
        }
    }
}
