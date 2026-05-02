package com.pypisan.sanchitra.data.repositories

import android.util.Log
import com.pypisan.sanchitra.data.entities.IPTVCategoryDto
import com.pypisan.sanchitra.data.models.IPTVChannel
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
}