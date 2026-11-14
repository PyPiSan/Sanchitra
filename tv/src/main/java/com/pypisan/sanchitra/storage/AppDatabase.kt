package com.pypisan.sanchitra.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pypisan.sanchitra.data.entities.ChannelEntity


@Database(
    entities = [ChannelEntity::class],
    version = 1,
    exportSchema = true
    // When you add new columns in a future app update, bump version = 2 and add:
    // autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
}