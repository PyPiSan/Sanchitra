package com.example.sanchitra.data.repositories


import android.content.Context
import android.util.Log
import com.example.sanchitra.utils.APIService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.collections.emptyList
import com.example.sanchitra.data.models.Channel
import com.example.sanchitra.data.models.UserDetailResponse
import com.example.sanchitra.utils.APIClient
import kotlin.collections.orEmpty


class TVRepositoryImpl @Inject constructor(
    private val api: APIService
) : TVRepository {

    override fun getChannels(): Flow<List<Channel>> = flow {
        val response = api.getLiveTV()

        if (response.isSuccessful) {
            val channels = response.body()?.channels.orEmpty()
//            Log.d("API_DEBUG", "Channels size: ${channels.size}")
            emit(channels)
        } else {
//            Log.e("API_DEBUG", "Error ${response.code()} ${response.message()}")
            emit(emptyList())
        }
    }
        .catch { e ->
//            Log.e("API_DEBUG", "Exception: ${e.message}", e)
            emit(emptyList())
        }
        .flowOn(Dispatchers.IO)


    override suspend fun getChannelData(id: String, type: String): ApiResult<Channel> {
        return try {
            val response = api.getChannelDetail(id.toInt(), type)

            if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response.body()!!)
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
}