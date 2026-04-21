package com.example.sanchitra.data.repositories

import com.example.sanchitra.data.entities.IPTVCategoryDto
import com.example.sanchitra.data.models.IPTVChannel
import com.example.sanchitra.data.models.toDomain
import com.example.sanchitra.utils.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.collections.map
import kotlin.collections.orEmpty

class IPTVRepositoryImpl @Inject constructor(
    private val api: APIService
): IPTVRepository{

    override fun getIPTVChannelsByCategory(category: String): Flow<List<IPTVChannel>> =
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
                throw Exception("API Error: ${response.code()}")
            }
        }
            .catch {
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)

    override fun getIPTVCategories(): Flow<List<IPTVCategoryDto>> =
        flow {
            val response = api.getIPTVCategoryList()

            if (response.isSuccessful) {
                val categories = response.body().orEmpty()
                emit(categories)
            } else {
                emit(emptyList())
            }
        }
            .catch {
                emit(emptyList())
            }
            .flowOn(Dispatchers.IO)

}