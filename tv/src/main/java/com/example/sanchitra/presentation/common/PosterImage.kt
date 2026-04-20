package com.example.sanchitra.presentation.common
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
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

//@Composable
//fun PosterImageChannel(
//    channel: Channel,
//    modifier: Modifier = Modifier,
//) {
//    AsyncImage(
//        modifier = modifier,
//        model = ImageRequest.Builder(LocalContext.current)
//            .crossfade(true)
//            .data(channel.logoUrl)
//            .build(),
//        contentDescription = StringConstants.Composable.ContentDescription.moviePoster(channel.name),
////        contentScale = ContentScale.Crop
//    )
//}


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
            contentScale = ContentScale.Fit   // 👈 keep logos intact
        )
    }
}

//fun extractDominantColor(bitmap: Bitmap): Color {
//    val palette = Palette.from(bitmap).generate()
//    val dominant = palette.getDominantColor(0xFF2C2C2C.toInt())
//    return Color(dominant)
//}
//
//@Composable
//fun PosterImageChannel(
//    channel: Channel,
//    modifier: Modifier = Modifier,
//) {
//    var bgColor by remember { mutableStateOf(Color(0xFF2C2C2C)) }
//
//    Box(
//        modifier = modifier.background(bgColor)
//    ) {
//        AsyncImage(
//            model = ImageRequest.Builder(LocalContext.current)
//                .data(channel.logoUrl)
//                .crossfade(true)
//                .listener(
//                    onSuccess = { _, result ->
//                        val bitmap = (result.drawable as BitmapDrawable).bitmap
//                        bgColor = extractDominantColor(bitmap)
//                    }
//                )
//                .build(),
//            contentDescription = channel.name,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Fit
//        )
//    }
//}