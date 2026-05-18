package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.Channel

@androidx.annotation.OptIn(UnstableApi::class)
fun buildDrmExoPlayer(
    context: Context,
    channel: Channel,
    onError: (PlaybackException) -> Unit,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
): ExoPlayer {

    val mediaItem =
        MediaItem.Builder().setUri(channel.streamUrl).setMimeType(MimeTypes.APPLICATION_MPD)
            .setMediaMetadata(
                MediaMetadata.Builder().setTitle(channel.name).build()
            ).build()

    val trackSelector = DefaultTrackSelector(context)
    val loadControl = DefaultLoadControl()
    val videoMetaHelper = VideoMetaHelper()

    val drmSessionManager = when {

        // ===== CLEARKEY SUPPORT =====
        !channel.licenseKey.isNullOrEmpty() -> {

            val (keyHex, kidHex) = channel.getDrmKeys()!!

            val drmKeyBytes = kidHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

            val encodedDrmKey = Base64.encodeToString(
                drmKeyBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )

            val drmKeyIdBytes = keyHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

            val encodedDrmKeyId = Base64.encodeToString(
                drmKeyIdBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
            )

            val drmBody = """
                {
                  "keys":[
                    {
                      "kty":"oct",
                      "k":"$encodedDrmKey",
                      "kid":"$encodedDrmKeyId"
                    }
                  ],
                  "type":"temporary"
                }
            """.trimIndent()

            val drmCallback = LocalMediaDrmCallback(drmBody.toByteArray())

            DefaultDrmSessionManager.Builder().setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false).setUuidAndExoMediaDrmProvider(
                    C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER
                ).build(drmCallback)
        }

        // ===== WIDEVINE LICENSE URL SUPPORT =====
        !channel.licenseUrl.isNullOrEmpty() -> {

            val dataSourceFactory = DefaultHttpDataSource.Factory()

            val drmCallback = HttpMediaDrmCallback(
                channel.licenseUrl, dataSourceFactory
            )

            DefaultDrmSessionManager.Builder().setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false).setUuidAndExoMediaDrmProvider(
                    C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER
                ).build(drmCallback)
        }

        else -> {
            null
        }
    }

    val mediaSourceFactory = DefaultMediaSourceFactory(context).also { factory ->
        drmSessionManager?.let { manager ->
            factory.setDrmSessionManagerProvider { manager }
        }
    }

    return ExoPlayer.Builder(context).setTrackSelector(trackSelector).setLoadControl(loadControl)
        .setSeekForwardIncrementMs(10_000L).setSeekBackIncrementMs(10_000L).build().apply {

            trackSelectionParameters =
                trackSelectionParameters.buildUpon().setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                    .setForceHighestSupportedBitrate(true).setPreferredAudioLanguage("en").build()

            setMediaSource(
                mediaSourceFactory.createMediaSource(mediaItem), true
            )

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
//                    Log.e("TV", "Error: ${error.message}", error)
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

            prepare()
            playWhenReady = true
        }
}


fun Channel.getDrmKeys(): Pair<String, String>? {
    if (!isDrm || licenseKey.isNullOrEmpty()) return null
    val parts = licenseKey.split(":")
    if (parts.size != 2) return null
    return parts[0] to parts[1]
}