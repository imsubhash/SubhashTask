package com.subhash.subhashtask.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subhash.subhashtask.data.local.entity.HoldingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldings(holdings: List<HoldingEntity>)

    @Query("SELECT * FROM holdings")
    suspend fun getHoldings(): List<HoldingEntity>

    @Query("DELETE FROM holdings")
    suspend fun clearHoldings()
}
