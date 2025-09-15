package com.subhash.subhashtask.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HoldingDto(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("ltp") val ltp: Double,
    @SerializedName("avgPrice") val avgPrice: Double,
    @SerializedName("close") val close: Double
)
