package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import android.media.session.PlaybackState
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.pypisan.sanchitra.data.models.Channel

@androidx.annotation.OptIn(UnstableApi::class)
fun buildDefaultExoPlayer(
    context: Context,
    channel: Channel,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {

    return ExoPlayer.Builder(context, renderersFactory)
        .build()
        .apply {

            trackSelectionParameters = trackSelectionParameters
                .buildUpon()
                .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                .setForceHighestSupportedBitrate(true)
                .setPreferredAudioLanguage("en")
                .setPreferredTextLanguage("en")
                .setSelectUndeterminedTextLanguage(true)
                .build()

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
//                    Log.e("TV", "Error: ${error.message}", error)
                    onError(error)
                }

                override fun onPlaybackStateChanged(state: Int) {
                    onBuffering(state)

//                    when (state) {
//                        Player.STATE_BUFFERING -> {
//                            Log.d("TV", "BUFFERING")
//                        }
//
//                        Player.STATE_READY -> {
//                            Log.d("TV", "READY")
//                        }
//
//                        Player.STATE_ENDED -> {
//                            Log.d("TV", "ENDED")
//                        }
//
//                        Player.STATE_IDLE -> {
//                            Log.d("TV", "IDLE")
//                        }
//                    }
                }
            })

            setMediaItem(MediaItem.fromUri(channel.streamUrl))
            prepare()
            playWhenReady = true
        }
}