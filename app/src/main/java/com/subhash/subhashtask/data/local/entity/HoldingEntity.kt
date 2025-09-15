package com.subhash.subhashtask.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.subhash.subhashtask.domain.model.Holding

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
) {
    fun toDomain(): Holding {
        return Holding(
            symbol = symbol,
            quantity = quantity,
            ltp = ltp,
            avgPrice = avgPrice,
            close = close
        )
    }

    companion object {
        fun fromDomain(holding: Holding): HoldingEntity {
            return HoldingEntity(
                symbol = holding.symbol,
                quantity = holding.quantity,
                ltp = holding.ltp,
                avgPrice = holding.avgPrice,
                close = holding.close
            )
        }
    }
}
