package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.SearchResult
import com.pypisan.sanchitra.data.models.toDomain
import com.pypisan.sanchitra.utils.APIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: APIService
) : SearchRepository {

    override suspend fun searchContents(query: String): Flow<SearchResult> = flow {

        if (query.isBlank()) {
            emit(SearchResult(channels = emptyList(), videos = emptyList()))
            return@flow
        }

        val response = api.getSearchResults(query)

        if (response.isSuccessful) {
            val domainResult = response.body()?.toDomain() ?: SearchResult(
                channels = emptyList(),
                videos = emptyList()
            )
            emit(domainResult)
        } else {
            emit(SearchResult(channels = emptyList(), videos = emptyList()))
        }
    }.catch { e ->
        // Handle exception / log error
        emit(SearchResult(channels = emptyList(), videos = emptyList()))
    }.flowOn(Dispatchers.IO)
}
