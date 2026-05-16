package com.pypisan.sanchitra.data.repositories



import android.util.Log
import com.pypisan.sanchitra.data.models.TrendingMovieResponse
import com.pypisan.sanchitra.data.models.TrendingResponse
import com.pypisan.sanchitra.data.models.toTrendingMovieResponse
import com.pypisan.sanchitra.data.models.toTrendingResponse
import com.pypisan.sanchitra.utils.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.collections.emptyList

class HomeRepositoryImpl @Inject constructor(
    private val api: APIService
) : HomeRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val liveChannelTrendingFlow: StateFlow<TrendingResponse> = flow {
        val response = api.getTrending()
        Log.d("TV", "response is $response")
        if (response.isSuccessful) {
            emit(
                response.body()?.toTrendingResponse()
                    ?: TrendingResponse(emptyList())
            )
        } else {
            emit(TrendingResponse(emptyList()))
        }
    }.catch {
        emit(TrendingResponse(emptyList()))
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = TrendingResponse(emptyList())
    )

    override fun getTrendingLiveChannels(): StateFlow<TrendingResponse> = liveChannelTrendingFlow

    private val movieTrendingFlow: StateFlow<TrendingMovieResponse> = flow {
        val response = api.getTrendingMovies()

        if (response.isSuccessful) {
            emit(
                response.body()?.toTrendingMovieResponse()
                    ?: TrendingMovieResponse(emptyList())
            )
        } else {
            emit(TrendingMovieResponse(emptyList()))
        }
    }.catch {
        emit(TrendingMovieResponse(emptyList()))
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = TrendingMovieResponse(emptyList())
    )

    override fun getTrendingMovies(): StateFlow<TrendingMovieResponse> = movieTrendingFlow

}