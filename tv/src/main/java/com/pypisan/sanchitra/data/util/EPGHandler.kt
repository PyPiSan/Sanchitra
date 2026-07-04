package com.pypisan.sanchitra.data.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.models.ProgramDisplayModel
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
private val jsonTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
@RequiresApi(Build.VERSION_CODES.O)
private val uiTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.US)

@RequiresApi(Build.VERSION_CODES.O)
fun prepareEPGProgramData(
    epgResponse: EPGResponse?,
    now: LocalDateTime = LocalDateTime.now()
): Pair<List<ProgramDisplayModel>, Int> {
    if (epgResponse == null || epgResponse.epg.isEmpty()) {
        return Pair(emptyList(), 0)
    }

    // 1. Sort the EPG by parsed start time to guarantee a stable timeline
    val sortedEpg = epgResponse.epg.sortedBy {
        try {
            LocalDateTime.parse(it.start, jsonTimeFormatter)
        } catch (e: Exception) {
            LocalDateTime.MIN
        }
    }

    var currentlyAiringIndex = 0

    // 2. Map EPGItems to ProgramDisplayModels
    val mappedPrograms = sortedEpg.mapIndexed { index, item ->
        val startDateTime = LocalDateTime.parse(item.start, jsonTimeFormatter)
        val endDateTime = LocalDateTime.parse(item.end, jsonTimeFormatter)

        val progress: Float
        val status: String

        when {
            // Case 1: Program has already finished relative to 'now'
            now.isAfter(endDateTime) -> {
                status = "PREVIOUSLY AIRED"
                progress = 1.0f
            }
            // Case 2: Program is in the future relative to 'now'
            now.isBefore(startDateTime) -> {
                status = "UP NEXT"
                progress = 0.0f
            }
            // Case 3: Currently airing
            else -> {
                status = "NOW AIRING"
                currentlyAiringIndex = index // Capture this index as the active show

                val total = Duration.between(startDateTime, endDateTime).toMinutes()
                val elapsed = Duration.between(startDateTime, now).toMinutes()
                progress = if (total > 0) elapsed.toFloat() / total.toFloat() else 0f
            }
        }

        ProgramDisplayModel(
            status = status,
            title = item.name,
            description = item.description,
            imageUrl = item.image,
            startTime = startDateTime.format(uiTimeFormatter),
            endTime = endDateTime.format(uiTimeFormatter),
            progress = progress
        )
    }

    return Pair(mappedPrograms, currentlyAiringIndex)
}