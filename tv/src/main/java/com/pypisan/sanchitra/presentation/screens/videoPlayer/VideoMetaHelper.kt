package com.pypisan.sanchitra.presentation.screens.videoPlayer

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.pypisan.sanchitra.data.entities.AudioTrack
import com.pypisan.sanchitra.data.entities.SubtitleTrack
import com.pypisan.sanchitra.data.entities.VideoQuality
import java.util.Locale

class VideoMetaHelper {
    fun getSubtitleTracks(
        player: ExoPlayer
    ): List<SubtitleTrack> {

        val subtitles = mutableListOf<SubtitleTrack>()

        player.currentTracks.groups.forEach { group ->

            if (group.type == C.TRACK_TYPE_TEXT) {

                for (i in 0 until group.length) {

                    val format = group.getTrackFormat(i)

                    subtitles.add(
                        SubtitleTrack(
                            label = buildSubtitleLabel(format),
                            language = format.language,
                            group = group,
                            trackIndex = i,

                            // IMPORTANT
                            isSelected = group.isTrackSelected(i)
                        )
                    )
                }
            }
        }

        return subtitles
    }

    fun getAudioTracks(player: Player): List<AudioTrack> {

        val list = mutableListOf<AudioTrack>()

        // Loop through all track groups currently loaded in the player
        player.currentTracks.groups.forEach { group ->

            // We only care about AUDIO tracks here
            if (group.type == C.TRACK_TYPE_AUDIO) {

                // Loop through every track inside this specific group
                for (trackIndex in 0 until group.length) {

                    val format = group.getTrackFormat(trackIndex)

                    // Create a readable label (e.g. "English" instead of "en", or fallback to "Unknown")
                    val displayLanguage = format.language?.let { Locale(it).displayLanguage }
                    val readableLabel = format.label ?: displayLanguage ?: "Unknown Audio"

                    // Optional: Append channel count (e.g., "English (5.1)" vs "English (Stereo)")
                    val channelString = when (format.channelCount) {
                        2 -> " (Stereo)"
                        6 -> " (5.1)"
                        8 -> " (7.1)"
                        else -> ""
                    }

                    list.add(
                        AudioTrack(
                            group = group,
                            trackIndex = trackIndex,
                            language = format.language ?: "und",
                            label = readableLabel + channelString,
                            isSelected = group.isTrackSelected(trackIndex)
                        )
                    )
                }
            }
        }

        // Optional: Add an "Auto / Default" option at the top
        val autoSelected = list.none { it.isSelected }
        list.add(
            0,
            AudioTrack(
                group = null,
                trackIndex = -1,
                language = null,
                label = "Auto",
                isSelected = autoSelected
            )
        )

        return list
    }

    @OptIn(UnstableApi::class)
    fun getVideoQualities(player: ExoPlayer): List<VideoQuality> {

        val qualities = mutableListOf<VideoQuality>()

        player.currentTracks.groups.forEachIndexed { groupIndex, group ->

            if (group.type == C.TRACK_TYPE_VIDEO) {

                for (i in 0 until group.length) {

                    val format = group.getTrackFormat(i)

                    qualities.add(
                        VideoQuality(
                            label = qualityLabel(format.height),
                            group = group,
                            trackIndex = i,
                            width = format.width,
                            height = format.height,
                            bitrate = format.bitrate,
                            isSelected = group.isTrackSelected(i)
                        )
                    )
                }
            }
        }

        return qualities
    }

    fun buildSubtitleLabel(format: Format): String {

        val language =
            format.language
                ?.let { Locale(it).displayLanguage }
                ?.takeIf { it.isNotBlank() }

        val label =
            format.label
                ?.takeIf { it.isNotBlank() }

        return when {
            language != null &&
                    label != null &&
                    !label.equals(language, true) -> {
                "$language ($label)"
            }
            language != null -> language
            label != null -> label
            else -> "English"
        }
    }

    fun qualityLabel(height: Int): String {
        val type = when {
            height >= 2160 -> "UHD"
            height >= 1080 -> "FHD"
            height >= 720 -> "HD"
            else -> "SD"
        }

        return "$type (${height}p)"
    }
}