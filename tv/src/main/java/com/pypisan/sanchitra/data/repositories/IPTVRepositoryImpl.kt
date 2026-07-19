package com.pypisan.sanchitra.data.repositories


import android.util.Log
import com.pypisan.sanchitra.data.entities.IPTVCategoryDto
import com.pypisan.sanchitra.data.entities.IPTVChannel
import com.pypisan.sanchitra.data.entities.IPTVChannelDetail
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


class IPTVRepositoryImpl @Inject constructor(
    private val api: APIService
) : IPTVRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Cache per category
    private val categoryCache = mutableMapOf<String, StateFlow<List<IPTVChannel>>>()

    override fun getIPTVChannelsByCategory(category: String): Flow<List<IPTVChannel>> {
        return categoryCache.getOrPut(category) {
            flow {
                val response = api.getIPTVCategory(category)

                if (response.isSuccessful) {
                    emit(
                        response.body()
                            ?.channels
                            .orEmpty()
                            .map { it.toDomain() }
                    )
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
        }
    }

    // Cache categories list also
    private val categoriesFlow: StateFlow<List<IPTVCategoryDto>> = flow {
        val response = api.getIPTVCategoryList()

        if (response.isSuccessful) {
            emit(response.body().orEmpty())
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

    override fun getIPTVCategories(): Flow<List<IPTVCategoryDto>> = categoriesFlow

    override suspend fun getIPTVChannelDetail(id: String): ApiResult<IPTVChannelDetail> {
        return try {
            val response = api.getIPTVChannelDetail(id)
            Log.d("IPTV", "Response is: $response")
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

}