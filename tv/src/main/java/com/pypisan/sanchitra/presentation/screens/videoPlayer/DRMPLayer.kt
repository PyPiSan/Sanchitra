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
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.ExoMediaDrm
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.drm.HttpMediaDrmCallback
import androidx.media3.exoplayer.drm.LocalMediaDrmCallback
import androidx.media3.exoplayer.drm.MediaDrmCallback
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import com.pypisan.sanchitra.data.models.Channel
import com.pypisan.sanchitra.data.util.CustomDRMSessionManager
import okhttp3.RequestBody.Companion.toRequestBody

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

    val isInternal = true

    val mediaItem = MediaItem.Builder()
        .setUri(channel.streamUrl)
        .setMimeType(MimeTypes.APPLICATION_MPD)
        .setMediaMetadata(MediaMetadata.Builder().setTitle(channel.name).build())
        .build()

    val trackSelector = DefaultTrackSelector(context)
    val loadControl = DefaultLoadControl()
    val videoMetaHelper = VideoMetaHelper()

    // 1. Declare dataSourceFactory OUTSIDE the 'when' block so we can modify it and use it later
    var dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)

    val drmSessionManager = when {

        // ===== CLEARKEY SUPPORT =====
        !channel.licenseKey.isNullOrEmpty() -> {

            val (keyHex, kidHex) = channel.getDrmKeys()!!
            val drmKeyBytes = kidHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val encodedDrmKey = Base64.encodeToString(drmKeyBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val drmKeyIdBytes = keyHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            val encodedDrmKeyId = Base64.encodeToString(drmKeyIdBytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)

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

            DefaultDrmSessionManager.Builder()
                .setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false)
                .setUuidAndExoMediaDrmProvider(C.CLEARKEY_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER)
                .build(drmCallback)
        }

        // ===== STANDARD WIDEVINE LICENSE URL SUPPORT =====
        !channel.licenseUrl.isNullOrEmpty() && !isInternal -> {

            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            val drmCallback = HttpMediaDrmCallback(channel.licenseUrl, httpDataSourceFactory)

            val customDrmProvider = ExoMediaDrm.Provider { uuid ->
                val drm = FrameworkMediaDrm.newInstance(uuid)
                try {
                    if (isFireTvDevice()) drm.setPropertyString("securityLevel", "L3")
                } catch (e: Exception) { e.printStackTrace() }
                drm
            }

            DefaultDrmSessionManager.Builder()
                .setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false)
                .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, customDrmProvider)
                .build(drmCallback)
        }

        // ===== SPECIFIC INTERNAL DRM SCENARIO (JioTV Custom Headers & Cookies) =====
        !channel.licenseUrl.isNullOrEmpty() && isInternal -> {

            // Set up DataSource with custom Cookie for fetching DASH segments and Manifests
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("plaYtv/7.1.7 (Linux;Android 8.1.0) ExoPlayerLib/2.11.7")
                .setDefaultRequestProperties(
                    mapOf("Cookie" to "__hdnea__=")
                )

            // Override the outer variable
            dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

            // Custom MediaDrmCallback connecting ExoPlayer to your CustomDRMSessionManager
            val drmCallback = object : MediaDrmCallback {

                override fun executeKeyRequest(uuid: java.util.UUID, request: ExoMediaDrm.KeyRequest): ByteArray {
                    return CustomDRMSessionManager.fetchDrmLicense(
                        licenseUrl = channel.licenseUrl ?: request.licenseServerUrl,
                        challenge = request.data, // Payload ExoPlayer generated
                        authToken =  "",
                        subscriberId = "",
                        uniqueId =  "",
                        ssoToken =  "",
                        channelId = "",
                        hdnea = ""
                    )
                }

                override fun executeProvisionRequest(uuid: java.util.UUID, request: ExoMediaDrm.ProvisionRequest): ByteArray {
                    val url = request.defaultUrl + "&signedRequest=" + String(request.data)
                    val okReq = okhttp3.Request.Builder()
                        .url(url)
                        .post(ByteArray(0).toRequestBody(null)) // Requires import okhttp3.RequestBody.Companion.toRequestBody
                        .build()
                    CustomDRMSessionManager.client.newCall(okReq).execute().use {
                        return it.body?.bytes() ?: ByteArray(0)
                    }
                }
            }

            // Set up the Widevine L3 workaround if necessary
            val customDrmProvider = ExoMediaDrm.Provider { uuid ->
                val drm = FrameworkMediaDrm.newInstance(uuid)
                try {
                    if (isFireTvDevice()) drm.setPropertyString("securityLevel", "L3")
                } catch (e: Exception) { e.printStackTrace() }
                drm
            }

            // Build and return the session manager for this branch
            DefaultDrmSessionManager.Builder()
                .setPlayClearSamplesWithoutKeys(true)
                .setMultiSession(false)
                .setUuidAndExoMediaDrmProvider(C.WIDEVINE_UUID, customDrmProvider)
                .build(drmCallback)
        }

        else -> {
            null
        }
    }

    // 2. Feed our updated dataSourceFactory into the MediaSourceFactory
    val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory).also { factory ->
        drmSessionManager?.let { manager ->
            factory.setDrmSessionManagerProvider { manager }
        }
    }

    return ExoPlayer.Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(loadControl)
        .setSeekForwardIncrementMs(10_000L)
        .setSeekBackIncrementMs(10_000L)
        .build().apply {

            trackSelectionParameters = trackSelectionParameters.buildUpon()
                .setForceHighestSupportedBitrate(true)
                .setPreferredAudioLanguage("en")
                .build()

            // 3. Set the media source using the correct factory
            setMediaSource(mediaSourceFactory.createMediaSource(mediaItem), true)

            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    onError(error)
                }
                override fun onTracksChanged(tracks: Tracks) {
                    onSubtitlesChanged(videoMetaHelper.getSubtitleTracks(this@apply))
                    onAudiosChanged(videoMetaHelper.getAudioTracks(this@apply))
                    onQualitiesChanged(videoMetaHelper.getVideoQualities(this@apply))
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

fun isFireTvDevice(): Boolean {
    val manufacturer = android.os.Build.MANUFACTURER ?: ""
    val model = android.os.Build.MODEL ?: ""
    // Checks if the manufacturer is Amazon and the model name starts with "AFT"
    return manufacturer.equals("Amazon", ignoreCase = true) &&
            model.startsWith("AFT", ignoreCase = true)
}