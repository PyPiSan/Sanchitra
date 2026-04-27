package com.pypisan.sanchitra.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pypisan.sanchitra.data.entities.Movie
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.data.models.IPTVChannel
import com.pypisan.sanchitra.data.util.StringConstants

@Composable
fun PosterImage(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(movie.posterUri)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(movie.name),
        contentScale = ContentScale.Crop
    )
}


@Composable
fun PosterImageChannel(
    channel: Channel,
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(channel.logoUrl)
                .crossfade(true)
                .build(),
            contentDescription = channel.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
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
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(iptvChannel.logo.url)
                .crossfade(true)
                .build(),
            contentDescription = iptvChannel.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}