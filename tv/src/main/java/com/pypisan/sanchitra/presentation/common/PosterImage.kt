package com.pypisan.sanchitra.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.pypisan.sanchitra.data.util.StringConstants
import kotlin.math.abs

@Composable
fun PosterImage(
    title: String,
    image: String?,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(image)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(title),
        contentScale = ContentScale.Crop
    )
}

private val FallbackChannelPalettes = listOf(
    listOf(Color(0xFF2B1714), Color(0xFF5A1F18), Color(0xFF121015)), // Crimson Gold
    listOf(Color(0xFF131D33), Color(0xFF1E3C66), Color(0xFF0E1017)), // Midnight Royal
    listOf(Color(0xFF102824), Color(0xFF195143), Color(0xFF0D1314)), // Deep Emerald
    listOf(Color(0xFF261430), Color(0xFF4D1B49), Color(0xFF120E16)), // Dark Plum
    listOf(Color(0xFF262013), Color(0xFF523C17), Color(0xFF121216)), // Warm Bronze
    listOf(Color(0xFF11232E), Color(0xFF1B4561), Color(0xFF0D1217))  // Deep Ocean
)


@Composable
fun ChannelNameArtwork(
    name: String,
    category: String?,
    modifier: Modifier = Modifier
) {
    val palette = remember(name) {
        val safeHash = abs(name.hashCode().let { if (it == Int.MIN_VALUE) 0 else it })
        FallbackChannelPalettes[safeHash % FallbackChannelPalettes.size]
    }
    val initials = remember(name) { extractInitials(name) }

    val titleFontSize = when {
        name.length <= 8 -> 22.sp
        name.length <= 18 -> 17.sp
        else -> 14.sp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = palette,
                    start = Offset(0f, 0f),
                    end = Offset(500f, 350f)
                )
            )
    ) {
        // Subtle radial sheen in center-top
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Oversized faded monogram watermark
        Text(
            text = initials,
            color = Color.White.copy(alpha = 0.07f),
            fontSize = 68.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-2).sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 6.dp, y = 12.dp)
        )

        // Optional subtle category tag in top-left
        if (!category.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = category.uppercase(),
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Centered channel title + small signature accent line
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = Color.White,
                    fontSize = titleFontSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = (titleFontSize.value + 4).sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.65f),
                        offset = Offset(0f, 2f),
                        blurRadius = 6f
                    )
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(2.5.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFE082),
                                Color(0xFFFF5722)
                            )
                        )
                    )
            )
        }
    }
}

private fun extractInitials(name: String): String {
    val words = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        words.size >= 2 -> "${words[0].first()}${words[1].first()}".uppercase()
        words.size == 1 -> words[0].take(2).uppercase()
        else -> "TV"
    }
}

@Composable
fun PosterImageChannel(
    logoUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    category: String? = null
) {
    if (logoUrl.isNullOrBlank()) {
        // No logo URL -> render designed channel name card directly
        ChannelNameArtwork(
            name = name,
            category = category,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.White.copy(alpha = 0.02f)
                    )
                )
            ),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentScale = ContentScale.Fit,
                loading = {
                    ChannelNameArtwork(
                        name = name,
                        category = category,
                        modifier = Modifier.fillMaxSize()
                    )
                },
                error = {
                    ChannelNameArtwork(
                        name = name,
                        category = category,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        }
    }
}