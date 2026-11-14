package com.pypisan.sanchitra.storage

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.pypisan.sanchitra.data.entities.ChannelEntity
import kotlinx.coroutines.flow.Flow

data class ChannelIdentity(
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "stream_url") val streamUrl: String,
    @ColumnInfo(name = "inserted_on") val insertedOn: String
)

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels ORDER BY updated_on DESC, id DESC")
    fun observeAll(): Flow<List<ChannelEntity>>

    @Query("SELECT DISTINCT category FROM channels WHERE is_active = 1 ORDER BY category ASC")
    fun observeCategories(): Flow<List<String>>

    @Query("SELECT * FROM channels WHERE category = :category AND is_active = 1 ORDER BY name ASC")
    fun observeByCategory(category: String): Flow<List<ChannelEntity>>

    @Query("SELECT id, stream_url, inserted_on FROM channels")
    suspend fun getAllIdentities(): List<ChannelIdentity>

    @Upsert
    suspend fun upsertAll(channels: List<ChannelEntity>)

    @Update
    suspend fun update(channel: ChannelEntity)

    @Query("DELETE FROM channels WHERE id = :channelId")
    suspend fun deleteById(channelId: Long)

    @Query("DELETE FROM channels WHERE category = :category")
    suspend fun deleteByCategory(category: String)

    @Query("SELECT * FROM channels WHERE id = :channelId LIMIT 1")
    suspend fun getById(channelId: Long): ChannelEntity?

    @Query("DELETE FROM channels")
    suspend fun clearAll()
}