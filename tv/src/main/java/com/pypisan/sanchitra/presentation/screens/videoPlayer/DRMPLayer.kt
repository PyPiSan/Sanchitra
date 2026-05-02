package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import android.util.Base64
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.pypisan.sanchitra.data.models.Channel

@androidx.annotation.OptIn(UnstableApi::class)
fun buildDrmExoPlayer(
    context: Context,
    channel: Channel
): ExoPlayer {

    val (keyHex, kidHex) = channel.getDrmKeys()!!

    val drmKeyBytes = kidHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val encodedDrmKey = Base64.encodeToString(
        drmKeyBytes,
        Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
    )

    val drmKeyIdBytes = keyHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    val encodedDrmKeyId = Base64.encodeToString(
        drmKeyIdBytes,
        Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
    )

    val drmBody =
        "{\"keys\":[{\"kty\":\"oct\",\"k\":\"${encodedDrmKey}\",\"kid\":\"${encodedDrmKeyId}\"}],\"type\":\"temporary\"}"

    val mediaItem = MediaItem.Builder()
        .setUri(channel.streamUrl)
        .setMimeType(MimeTypes.APPLICATION_MPD)
        .setMediaMetadata(MediaMetadata.Builder().setTitle("test").build())
        .build()

    val trackSelector = DefaultTrackSelector(context)
    val loadControl = DefaultLoadControl()

    val drmCallback = LocalMediaDrmCallback(drmBody.toByteArray())

    val drmSessionManager = DefaultDrmSessionManager.Builder()
        .setPlayClearSamplesWithoutKeys(true)
        .setMultiSession(false)
        .setKeyRequestParameters(HashMap())
        .setUuidAndExoMediaDrmProvider(
            C.CLEARKEY_UUID,
            FrameworkMediaDrm.DEFAULT_PROVIDER
        )
        .build(drmCallback)

    val mediaSourceFactory = DefaultMediaSourceFactory(context)
        .setDrmSessionManagerProvider { drmSessionManager }
        .createMediaSource(mediaItem)

    return ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .setSeekForwardIncrementMs(10_000L)
        .setSeekBackIncrementMs(10_000L)
        .build()
        .apply {

            trackSelectionParameters = trackSelectionParameters
                .buildUpon()
                .setMaxVideoSize(Int.MAX_VALUE, Int.MAX_VALUE)
                .setForceHighestSupportedBitrate(true)
                .setPreferredAudioLanguage("en")
                .build()

            setMediaSource(mediaSourceFactory, true)
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