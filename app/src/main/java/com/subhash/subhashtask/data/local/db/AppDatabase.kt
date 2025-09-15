package com.subhash.subhashtask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.subhash.subhashtask.data.local.dao.HoldingDao
import com.subhash.subhashtask.data.local.entity.HoldingEntity

@Database(
    entities = [HoldingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun holdingDao(): HoldingDao
}
