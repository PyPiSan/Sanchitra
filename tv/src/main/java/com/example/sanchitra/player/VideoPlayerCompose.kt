package com.example.sanchitra.player

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.tv.material3.*
import com.example.sanchitra.api.TVRequest
import com.example.sanchitra.api.WatchRequest
import com.example.sanchitra.data.models.EpisodeVideoModel
import com.example.sanchitra.data.models.TVVideoModel
import com.example.sanchitra.utils.Constant
import com.example.sanchitra.utils.RequestModule
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.ui.res.painterResource
import com.example.sanchitra.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import androidx.appcompat.view.ContextThemeWrapper

@OptIn(ExperimentalTvMaterial3Api::class)
class VideoPlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val intent = intent
        val type = intent.getStringExtra("type") ?: ""

        var title = ""
        var episode = ""
        var channelId = ""
        var language = ""

        if (type == "tv") {
            title = intent.getStringExtra("channel") ?: ""
            channelId = intent.getStringExtra("id") ?: ""
            language = intent.getStringExtra("language") ?: ""
        } else {
            title = intent.getStringExtra("title") ?: ""
            episode = intent.getStringExtra("episode") ?: ""
        }

        setContent {
            MaterialTheme {
                VideoPlayerScreen(
                    type = type,
                    title = title,
                    episode = episode,
                    channelId = channelId,
                    language = language,
                    activity = this
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    type: String,
    title: String,
    episode: String,
    channelId: String,
    language: String,
    activity: ComponentActivity
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var videoLinks by remember { mutableStateOf(arrayOf<String>()) }
    var showControls by remember { mutableStateOf(true) }

    val exoPlayer = remember {
        ExoPlayer.Builder(activity).build().apply {
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    fun fetchVideoLink() {
        isLoading = true
        hasError = false
        if (type == "tv") {
            Log.d("channelId", channelId)
            val retrofit = Retrofit.Builder()
                .baseUrl(Constant.local)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val requestModule = retrofit.create(RequestModule::class.java)
            val call = requestModule.getTvVideoV2("e7y6acFyHGqwtkBLKHx6eA", TVRequest(channelId, language))

            call.enqueue(object : Callback<TVVideoModel> {
                override fun onResponse(call: Call<TVVideoModel>, response: Response<TVVideoModel>) {
                    val resource = response.body()
                    Log.d("tv", resource.toString())
                    if (response.isSuccessful && resource?.success == true) {
                        Constant.cookies = resource.cookies
                        videoLinks = arrayOf(
                            resource.value.low ?: "",
                            resource.value.medium ?: "",
                            resource.value.high ?: "",
                            resource.value.ultraHigh ?: ""
                        )
                        isLoading = false
                    } else {
                        isLoading = false
                        hasError = true
                    }
                }

                override fun onFailure(call: Call<TVVideoModel>, t: Throwable) {
                    isLoading = false
                    hasError = true
                }
            })
        } else {
            val url = if (type == "anime") Constant.animeUrl else Constant.dramaUrl
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val requestModule = retrofit.create(RequestModule::class.java)
            val call = requestModule.getEpisodeVideo(Constant.key, WatchRequest(title, episode, null))

            call.enqueue(object : Callback<EpisodeVideoModel> {
                override fun onResponse(call: Call<EpisodeVideoModel>, response: Response<EpisodeVideoModel>) {
                    val resource = response.body()
                    if (response.isSuccessful && resource?.success == true) {
                        videoLinks = arrayOf(
                            resource.value.quality1 ?: "",
                            resource.value.quality2 ?: "",
                            resource.value.quality3 ?: "",
                            resource.value.quality4 ?: ""
                        )
                        isLoading = false
                    } else {
                        isLoading = false
                        hasError = true
                    }
                }

                override fun onFailure(call: Call<EpisodeVideoModel>, t: Throwable) {
                    Log.d("error", t.message.toString())
                    isLoading = false
                    hasError = true
                    videoLinks = arrayOf(
                         "http://116.90.120.151:8000/play/a0gp/index.m3u8",
                         "http://116.90.120.151:8000/play/a0gp/index.m3u8",
                        "http://116.90.120.151:8000/play/a0gp/index.m3u8",
                        "http://116.90.120.151:8000/play/a0gp/index.m3u8"
                    )
                }
            })
        }
    }

    LaunchedEffect(Unit) {
        fetchVideoLink()
    }

    LaunchedEffect(videoLinks) {
        if (videoLinks.isNotEmpty() && videoLinks.any { it.isNotBlank() }) {
            val urlToPlay = videoLinks.lastOrNull { it.isNotBlank() } ?: ""
            if (urlToPlay.isNotBlank()) {
                val dataSourceFactory = DefaultHttpDataSource.Factory().apply {
                    setAllowCrossProtocolRedirects(true)
                    setConnectTimeoutMs(10000)
                    if (type != "tv") {
                        val map = hashMapOf(
                            "cookie" to (Constant.cookies ?: ""),
                            "x-api-key" to Constant.key
                        )
                        setDefaultRequestProperties(map)
                    } else {
                        setUserAgent("curl/7.85.0")
                    }
                }

                val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(urlToPlay))

                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusable()
            .onKeyEvent { event ->
                if (event.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    showControls = true

                    val keyCode = event.nativeKeyEvent.keyCode
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        return@onKeyEvent true
                    }
                    if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (type != "tv") {
                            exoPlayer.seekTo(exoPlayer.currentPosition + 10_000)
                        }
                        return@onKeyEvent true
                    }
                    if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        exoPlayer.seekTo(exoPlayer.currentPosition - 10_000)
                        return@onKeyEvent true
                    }
                }
                false
            }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                AndroidView(
                    factory = { ctx ->
                        val themeWrapper = ContextThemeWrapper(ctx, R.style.Theme_MaterialSpinner)
                        CircularProgressIndicator(themeWrapper).apply {
                            isIndeterminate = true
                        }
                    }
                )
            }
        }

        if (hasError) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Error Loading Video", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { fetchVideoLink() }) {
                    Text("Retry")
                }
            }
        }

        if (showControls && !isLoading && !hasError) {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(32.dp)
                ) {
                    Text(
                        text = if (type == "tv") title else "$title (Episode $episode)",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind button
                        IconButton(
                            onClick = { exoPlayer.seekTo(exoPlayer.currentPosition - 10_000) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
                                contentDescription = "Rewind",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Play/Pause button
                        var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
                        DisposableEffect(exoPlayer) {
                            val listener = object : com.google.android.exoplayer2.Player.Listener {
                                override fun onIsPlayingChanged(playing: Boolean) {
                                    isPlaying = playing
                                }
                            }
                            exoPlayer.addListener(listener)
                            onDispose { exoPlayer.removeListener(listener) }
                        }

                        IconButton(
                            onClick = {
                                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                            },
                            modifier = Modifier.focusRequester(focusRequester)
                        ) {
                            Icon(
                                painter = painterResource(id = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                                contentDescription = "Play/Pause",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Fast Forward button (hidden for TV)
                        if (type != "tv") {
                            IconButton(
                                onClick = { exoPlayer.seekTo(exoPlayer.currentPosition + 10_000) }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_fast_forward_24),
                                    contentDescription = "Fast Forward",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
