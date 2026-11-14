package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.models.toDomain
import com.pypisan.sanchitra.utils.APIService
import com.pypisan.sanchitra.utils.AnalyticsTracker
import com.pypisan.sanchitra.utils.MediaAPIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.orEmpty

class VideoRepositoryImpl  @Inject constructor (
    private val api: APIService,
    private val mediaApi: MediaAPIService
): VideoRepository{

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val videosFlow: StateFlow<Map<String, List<Videos>>> = flow {
        val response = api.getMoviesList()

        if (response.isSuccessful) {
            emit(
                response.body()
                    ?.videos
                    ?.mapValues { (_, videos) ->
                        videos.map { it.toDomain() }
                    }
                .orEmpty()
            )
        } else {
            emit(emptyMap())
        }
    }
        .catch {
            emit(emptyMap())
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    private val carouselVideosFlow: StateFlow<Map<String, List<Videos>>>  = flow {
        val response = api.getCarouselVideoList()

        if (response.isSuccessful) {
            emit(
                response.body()
                    ?.videos
                    ?.mapValues { (_, videos) ->
                        videos.map { it.toDomain() }
                    }
                    .orEmpty()
            )
        } else {
            emit(emptyMap())
        }
    }
        .catch { e ->
            AnalyticsTracker.recordNonFatal(
                throwable = e,
                message = "getMoviesList failed after retries"
            )
            emit(emptyMap())
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyMap()
        )

    override fun getVideoDetails(movieId: Int): StateFlow<Videos?> =
        flow {
            // 1. TIMEOUT per attempt
            val response = withTimeoutOrNull(30_000L) {
                mediaApi.getMovieDetails(movieId)
            } ?: throw IOException("Request timed out for movieId: $movieId")

            if (response.isSuccessful) {
                emit(response.body()?.toDomain())
            } else {
                // Throw on failure so retryWhen can catch and retry 5xx errors
                throw HttpException(response)
            }
        }
            // 2. RETRY (must be BEFORE .catch)
            .retryWhen { cause, attempt ->
                val isRetryable = cause is IOException || (cause is HttpException && cause.code() >= 500)
                if (isRetryable && attempt < 2) {
                    delay(1_000L * (attempt + 1)) // backoff: 1s, 2s, 3s
                    true
                } else {
                    false
                }
            }
            // 3. CATCH (catches errors after all retries fail)
            .catch { e ->
                AnalyticsTracker.recordNonFatal(
                    throwable = e,
                    message = "getVideoDetails failed after retries (movieId=$movieId)"
                )
                emit(null)
            }
            // 4. STATE IN
            .stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )

    override fun getVideos(): StateFlow<Map<String, List<Videos>>> = videosFlow
    override fun getCarouselVideos(): StateFlow<Map<String, List<Videos>>> = carouselVideosFlow

    override suspend fun updateMovieViewCount(movieID: Int) {
        api.updateMovieViewCount(movieID)
    }
}