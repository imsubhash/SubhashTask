package com.subhash.subhashtask.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HoldingsResponseDto(
    @SerializedName("data") val data: UserHoldingsDto
)

data class UserHoldingsDto(
    @SerializedName("userHolding") val userHolding: List<HoldingDto>
)
