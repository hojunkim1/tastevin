package com.taba.tastevin.network

import com.squareup.moshi.Json
import com.taba.tastevin.domain.Wine

data class NetworkWine(
    @Json(name = "wine_id") val id: Int,
    @Json(name = "name_kr") val nameKr: String?,
    @Json(name = "name_en") val nameEn: String?,
    @Json val producer: String,
    @Json val nation: String,
    @Json val type: String,
    @Json val sweet: Int,
    @Json val acidity: Int,
    @Json val body: Int,
    @Json val tannin: Int,
    @Json val price: String?,
    @Json val food: String,
    @Json(name = "pic_url") val url: String,
    @Json val count: Int,
    @Json val re1: Int,
    @Json val re2: Int,
    @Json val re3: Int
)

fun NetworkWine.asDomainModel(): Wine {
    return Wine(
        id = id,
        nameKr = nameKr,
        nameEn = nameEn,
        producer = producer,
        nation = nation,
        type = type,
        sweet = sweet,
        acidity = acidity,
        body = body,
        tannin = tannin,
        price = price,
        food = food,
        url = url,
        count = count,
        recommend1 = re1,
        recommend2 = re2,
        recommend3 = re3
    )
}