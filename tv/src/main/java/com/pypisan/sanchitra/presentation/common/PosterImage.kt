package com.pypisan.sanchitra.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.pypisan.sanchitra.data.entities.IPTVChannel
import com.pypisan.sanchitra.data.util.StringConstants

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


@Composable
fun PosterImageChannel(
    logoUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
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
                .padding(4.dp),
            contentScale = ContentScale.Fit,
            error = {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp)
                )
            }
        )
    }
}

@Composable
fun PosterImageIPTVChannel(
    iptvChannel: IPTVChannel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E1E),
                        Color(0xFF2C2C2C)
                    )
                )
            )
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(iptvChannel.logo)
                .crossfade(true)
                .build(),
            contentDescription = iptvChannel.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}