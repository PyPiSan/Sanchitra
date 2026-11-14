package com.pypisan.sanchitra.data.repositories

import com.pypisan.sanchitra.data.entities.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchContents(query: String): Flow<SearchResult>
}