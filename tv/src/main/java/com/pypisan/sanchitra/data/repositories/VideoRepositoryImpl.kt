package com.pypisan.sanchitra.data.repositories

import android.util.Log
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.models.toDomain
import com.pypisan.sanchitra.utils.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.collections.map
import kotlin.collections.orEmpty

class VideoRepositoryImpl  @Inject constructor (
    private val api: APIService
): VideoRepository{

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val videosFlow: StateFlow<List<Videos>> = flow {
        val response = api.getMoviesList()

        if (response.isSuccessful) {
            emit(response.body()?.videos.orEmpty().map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }
        .catch {
            emit(emptyList())
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val carouselVideosFlow: StateFlow<List<Videos>> = flow {
        val response = api.getCarouselVideoList()

        if (response.isSuccessful) {
            emit(response.body()?.videos.orEmpty().map { it.toDomain() })
        } else {
            emit(emptyList())
        }
    }
        .catch {
            emit(emptyList())
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    override fun getVideos(): Flow<List<Videos>> = videosFlow
    override fun getCarouselVideos(): Flow<List<Videos>> = carouselVideosFlow
}