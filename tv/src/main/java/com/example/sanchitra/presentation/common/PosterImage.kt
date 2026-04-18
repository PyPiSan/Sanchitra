package com.example.sanchitra.presentation.common
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.sanchitra.data.entities.Movie
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.data.util.StringConstants

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
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .crossfade(true)
            .data(channel.logoUrl)
            .build(),
        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(channel.name),
        contentScale = ContentScale.Crop
    )
}