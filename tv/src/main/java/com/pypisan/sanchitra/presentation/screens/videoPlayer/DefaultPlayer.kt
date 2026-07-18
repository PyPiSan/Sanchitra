package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality

@OptIn(UnstableApi::class)
fun buildDefaultExoPlayer(
    context: Context,
    stream: String,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {

    val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(30000, 60000, 3000, 2000)
        .build()

    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(60_000)
        .setReadTimeoutMs(90_000)

    val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

    val videoMetaHelper = VideoMetaHelper()

    return ExoPlayer.Builder(context, renderersFactory)
        .setLoadControl(loadControl)
        .setMediaSourceFactory(mediaSourceFactory)
        .build()
        .apply {

            trackSelectionParameters = trackSelectionParameters
                .buildUpon()
//                .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                .setForceHighestSupportedBitrate(true)
                .setPreferredAudioLanguage("en")
                .setPreferredTextLanguage("en")
                .setSelectUndeterminedTextLanguage(true)
                .build()

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    onError(error)
                }

                override fun onPlaybackStateChanged(state: Int) {
                    onBuffering(state)
                }

                override fun onTracksChanged(tracks: Tracks) {
                    val subtitles =
                        videoMetaHelper.getSubtitleTracks(this@apply)

                    val audios =
                        videoMetaHelper.getAudioTracks(this@apply)

                    val qualities =
                        videoMetaHelper.getVideoQualities(this@apply)

                    onSubtitlesChanged(subtitles)

                    onAudiosChanged(audios)

                    onQualitiesChanged(qualities)
                }
            })

            setMediaItem(MediaItem.fromUri(stream))
            prepare()
            playWhenReady = true
        }
}