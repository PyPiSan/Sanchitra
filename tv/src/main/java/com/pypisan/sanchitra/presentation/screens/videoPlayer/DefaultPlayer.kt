package com.pypisan.sanchitra.presentation.screens.videoPlayer

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import androidx.core.net.toUri

@OptIn(UnstableApi::class)
fun buildDefaultExoPlayer(
    context: Context,
    stream: String,
    mediaType: String? = null,
    subtitleUrl: String?,
    onBuffering: (Int) -> Unit,
    onSubtitlesChanged: (List<SubtitleTrack>) -> Unit,
    onAudiosChanged: (List<AudioTrack>) -> Unit,
    onQualitiesChanged: (List<VideoQuality>) -> Unit,
    renderersFactory: DefaultRenderersFactory
): ExoPlayer {

    val loadControl =
        DefaultLoadControl.Builder().setBufferDurationsMs(30000, 60000, 3000, 2000).build()
    val httpDataSourceFactory: DefaultHttpDataSource.Factory

    if (mediaType.equals("movie", ignoreCase = true)) {
        httpDataSourceFactory = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(60_000).setReadTimeoutMs(90_000).setDefaultRequestProperties(
                mapOf(
                    "Origin" to "https://cinesrc.st"
                )
            )
    } else {

        httpDataSourceFactory = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(60_000).setReadTimeoutMs(90_000)
    }

    // Configure the Extractors Factory correctly
    val extractorsFactory = DefaultExtractorsFactory()
        //Only pass SEI closed caption types here (CEA-608 / CEA-708)
        .setTsSubtitleFormats(
            com.google.common.collect.ImmutableList.of(
                Format.Builder().setSampleMimeType(MimeTypes.APPLICATION_CEA608).build(),
                Format.Builder().setSampleMimeType(MimeTypes.APPLICATION_CEA708).build()
            )
        )


    val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory, extractorsFactory)

    val mediaItemBuilder = MediaItem.Builder().setUri(stream)

    if (!subtitleUrl.isNullOrBlank()) {

        val mimeType = when {
            subtitleUrl.endsWith(".vtt", ignoreCase = true) -> MimeTypes.TEXT_VTT

            subtitleUrl.endsWith(".srt", ignoreCase = true) -> MimeTypes.APPLICATION_SUBRIP

            else -> null
        }

        mimeType?.let {
            mediaItemBuilder.setSubtitleConfigurations(
                listOf(
                    MediaItem.SubtitleConfiguration.Builder(subtitleUrl.toUri()).setMimeType(it)
                        .setLanguage("en").setLabel("English")
                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build()
                )
            )
        }
    }


    val videoMetaHelper = VideoMetaHelper()

    return ExoPlayer.Builder(context, renderersFactory).setLoadControl(loadControl)
        .setMediaSourceFactory(mediaSourceFactory).build().apply {

            trackSelectionParameters =
                trackSelectionParameters.buildUpon().setForceHighestSupportedBitrate(true)
                    .setPreferredAudioLanguage("en").setPreferredTextLanguage("en")
                    .setSelectUndeterminedTextLanguage(true).build()

            addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(state: Int) {
                    onBuffering(state)
                }

                override fun onTracksChanged(tracks: Tracks) {
                    onSubtitlesChanged(videoMetaHelper.getSubtitleTracks(this@apply))
                    onAudiosChanged(videoMetaHelper.getAudioTracks(this@apply))
                    onQualitiesChanged(videoMetaHelper.getVideoQualities(this@apply))
                }
            })

            setMediaItem(mediaItemBuilder.build())
            prepare()
            playWhenReady = true
        }
}