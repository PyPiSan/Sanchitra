package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.utils.APIService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import com.pypisan.sanchitra.data.entities.Channel
import com.pypisan.sanchitra.data.models.toDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.map
import kotlin.collections.orEmpty


class TVRepositoryImpl @Inject constructor(
    private val api: APIService,
) : TVRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val channelsFlow: StateFlow<Map<String, List<Channel>>> = flow {
        val response = api.getLiveTV()

        if (response.isSuccessful) {
            emit(
                response.body()
                    ?.channels
                    ?.mapValues { (_, channels) ->
                        channels.map { it.toDomain() }
                    }
                    .orEmpty()
            )
        } else {
            emit(emptyMap())
        }
    }.catch {
        emit(emptyMap())
    }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap()
    )

    private val carouselFlow: StateFlow<Map<String, List<Channel>>> = flow {
        val response = api.getCarouselTV()

        if (response.isSuccessful) {
            emit(
                response.body()
                    ?.channels
                    ?.mapValues { (_, channels) ->
                        channels.map { it.toDomain() }
                    }
                    .orEmpty()
            )
        } else {
            emit(emptyMap())
        }
    }.catch {
        emit(emptyMap())
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    override fun getChannels(): StateFlow<Map<String, List<Channel>>> = channelsFlow
    override fun getCarouselTV(): StateFlow<Map<String, List<Channel>>> = carouselFlow


    override suspend fun getChannelData(id: String, type: String): ApiResult<Channel> {
        return try {
            val response = api.getChannelDetail(id.toInt(), type)

            if (response.isSuccessful) {

                val dto = response.body()

                if (dto != null) {
                    ApiResult.Success(dto.toDomain())
                } else {
                    ApiResult.Error("Empty response body", response.code())
                }
            } else {
                ApiResult.Error(
                    message = response.errorBody()?.string() ?: "Unknown error",
                    code = response.code()
                )
            }

        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Exception occurred")
        }
    }

    sealed class ApiResult<out T> {
        data class Success<T>(val data: T) : ApiResult<T>()
        data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    }

    override suspend fun updateViewCount(channelId: Int) {
        api.updateViewCount(channelId)
    }
}