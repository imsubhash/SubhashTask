package com.subhash.subhashtask.data.remote.api

import com.subhash.subhashtask.data.remote.dto.HoldingsResponseDto
import retrofit2.http.GET

interface HoldingsApi {

    @GET("/")
    suspend fun getHoldings(): HoldingsResponseDto
}
