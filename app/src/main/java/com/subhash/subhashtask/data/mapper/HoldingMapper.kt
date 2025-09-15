package com.subhash.subhashtask.data.mapper

import com.subhash.subhashtask.data.remote.dto.HoldingDto
import com.subhash.subhashtask.domain.model.Holding

fun HoldingDto.toDomain(): Holding {
    return Holding(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        avgPrice = avgPrice,
        close = close
    )
}