package com.example.sanchitra.data.repositories


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
import kotlin.collections.orEmpty


class TVRepositoryImpl @Inject constructor(
    private val api: APIService
) : TVRepository {

    override fun getChannels(): Flow<List<Channel>> = flow {
        val response = api.getLiveTV()

        if (response.isSuccessful) {
            val channels = response.body()?.channels.orEmpty()
            Log.d("API_DEBUG", "🟢 Channels size: ${channels.size}")
            emit(channels)
        } else {
            Log.e("API_DEBUG", "Error ${response.code()} ${response.message()}")
            emit(emptyList())
        }
    }
        .catch { e ->
            Log.e("API_DEBUG", "Exception: ${e.message}", e)
            emit(emptyList())
        }
        .flowOn(Dispatchers.IO)
}