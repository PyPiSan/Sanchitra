package com.pypisan.sanchitra.data.repositories

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pypisan.sanchitra.utils.MediaAPIService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.collections.emptyList
import com.pypisan.sanchitra.data.models.EPGResponse
import com.pypisan.sanchitra.data.models.toDomain
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.Duration
import java.time.LocalTime
import kotlin.coroutines.cancellation.CancellationException


class EPGManager @Inject constructor(
    private val mediaApi: MediaAPIService
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun observeEPG(channelId: Int): Flow<EPGResponse> = flow {

        while (currentCoroutineContext().isActive) {

            try {

                val response = mediaApi.getChannelEPG(channelId)

                val epg = if (response.isSuccessful) {
                    response.body()?.toDomain() ?: EPGResponse(emptyList())
                } else {
                    EPGResponse(emptyList())
                }

                emit(epg)

                val nextRefreshDelay = calculateNextRefreshDelay(epg)

                Log.d(
                    "EPGManager", "Next refresh in ${nextRefreshDelay / 1000}s"
                )

                delay(nextRefreshDelay)

            } catch (e: Exception) {

                // 1. ADD THIS LINE: Let normal cancellations happen silently!
                if (e is CancellationException) throw e

                // 2. Keep your existing error logging for real errors
                Log.e("EPG", "EPG fetch failed", e)

                // retry after 1 minute on failure
                delay(60_000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateNextRefreshDelay(
        epg: EPGResponse
    ): Long {

        val now = LocalTime.now()

        // Empty EPG → retry in 30 mins
        if (epg.epg.isEmpty()) {
            val now = LocalTime.now()
            return if (now.hour in 0..2) {
                // midnight refresh window
                5 * 60 * 1000L
            } else {
                // daytime no-EPG fallback
                30 * 60 * 1000L
            }
        }

        val lastProgram = epg.epg.last()

        return try {

            val endTime = LocalTime.parse(lastProgram.end)

            // If last EPG already expired → refresh immediately
            if (now.isAfter(endTime)) {
                5_000L
            } else {

                val millisUntilEnd = Duration.between(now, endTime).toMillis()

                // refresh 2 sec after program ends
                millisUntilEnd + 2_000L
            }

        } catch (e: Exception) {

            // fallback retry
            5 * 60 * 1000L
        }
    }
}