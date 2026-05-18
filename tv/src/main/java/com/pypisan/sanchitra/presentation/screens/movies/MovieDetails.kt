package com.pypisan.sanchitra.presentation.screens.movies

import JetStreamButtonShape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.presentation.screens.auth.loopPlayer
import com.pypisan.sanchitra.utils.BlueGray300
import com.pypisan.sanchitra.utils.DeepPurple300
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetails(
    video: Videos,
    openVideoPlayer: (metaId: String) -> Unit
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(432.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        MovieImageWithGradients(
            video = video,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxWidth(0.55f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                MovieLargeTitle(movieTitle = video.title)

                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    MovieDescription(description = video.meta.description)
                    DotSeparatedRow(
                        modifier = Modifier.padding(top = 20.dp),
                        texts = listOf(
                            video.meta.releaseDate!!,
                            video.categories.joinToString(", "),
                            "${video.duration} min"
                        )
                    )
//                    DirectorScreenplayMusicRow(
//                        director = movieDetails.director,
//                        screenplay = movieDetails.screenplay,
//                        music = movieDetails.music
//                    )
                }
                WatchMovieButton(
                    metaId = video.id.toString(),
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch {
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    },
                    openVideoPlayer = openVideoPlayer
                )
            }
        }
    }
}

@Composable
private fun WatchMovieButton(
    modifier: Modifier = Modifier,
    metaId: String,
    openVideoPlayer: (metaId: String) -> Unit
) {
    Button(
        onClick = {
            openVideoPlayer(metaId)
        },
        modifier = modifier.padding(top = 24.dp),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = JetStreamButtonShape)
    ) {
        Icon(
            imageVector = Icons.Outlined.PlayArrow,
            contentDescription = null
        )

        Spacer(Modifier.size(8.dp))

        Text(
            text = stringResource(R.string.watch_trailer),
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun DirectorScreenplayMusicRow(
    director: String,
    screenplay: String,
    music: String
) {
    Row(modifier = Modifier.padding(top = 32.dp)) {
        TitleValueText(
            modifier = Modifier
                .padding(end = 32.dp)
                .weight(1f),
            title = stringResource(R.string.director),
            value = director
        )

        TitleValueText(
            modifier = Modifier
                .padding(end = 32.dp)
                .weight(1f),
            title = stringResource(R.string.screenplay),
            value = screenplay
        )

        TitleValueText(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.music),
            value = music
        )
    }
}

@Composable
private fun MovieDescription(description: String) {
    Text(
        text = description,
        style = MaterialTheme.typography.titleSmall.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        modifier = Modifier.padding(top = 8.dp),
        maxLines = 2
    )
}

@Composable
private fun MovieLargeTitle(movieTitle: String) {
    Text(
        text = movieTitle,
        style = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        maxLines = 2
    )
}

@androidx.annotation.OptIn(UnstableApi::class, ExperimentalTvMaterial3Api::class)
@Composable
private fun MovieImageWithGradients(
    video: Videos,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface,
) {

    Box(modifier = modifier) {

        // ===== ALWAYS SHOW IMAGE =====

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(video.meta.banner)
                .crossfade(true)
                .build(),
            contentDescription = StringConstants
                .Composable
                .ContentDescription
                .moviePoster(video.title),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // ===== TRAILER PLAYER =====

        if (!video.meta.trailer.isNullOrEmpty()) {

            val exoPlayer = loopPlayer(video.meta.trailer, 50f)

            var isVideoReady by remember {
                mutableStateOf(false)
            }

            DisposableEffect(exoPlayer) {

                val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(state: Int) {
                        isVideoReady =
                            state == Player.STATE_READY &&
                                    exoPlayer.isPlaying
                    }
                }
                exoPlayer.addListener(listener)

                onDispose {
                    exoPlayer.removeListener(listener)
                    exoPlayer.release()
                }
            }

            if (isVideoReady) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DeepPurple300.copy(alpha = 0.8f),
                                    BlueGray300.copy(alpha = 0.6f),
                                    Color.Black
                                )
                            )
                        )
                )

                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            setKeepContentOnPlayerReset(true)
                            setShutterBackgroundColor(
                                BlueGray300.toArgb()
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(1.08f)
                )
            }
        }

        // ===== GRADIENT OVERLAY =====

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                gradientColor
                            ),
                            startY = 600f
                        )
                    )
                    drawRect(
                        Brush.horizontalGradient(
                            colors = listOf(
                                gradientColor,
                                Color.Transparent
                            ),
                            endX = 1000f,
                            startX = 300f
                        )
                    )
                    drawRect(
                        Brush.linearGradient(
                            colors = listOf(
                                gradientColor,
                                Color.Transparent
                            ),
                            start = Offset(500f, 500f),
                            end = Offset(1000f, 0f)
                        )
                    )
                }
        )
    }
}
