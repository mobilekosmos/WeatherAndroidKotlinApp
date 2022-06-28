package com.mobilekosmos.android.weather.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherForecastEntity(
    @field:Json(name = "day") val day: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "sunrise") val sunrise: Long,
    @field:Json(name = "sunset") val sunset: Long,
    @field:Json(name = "chance_rain") val chance_rain: Double,
    @field:Json(name = "high") val high: Short,
    @field:Json(name = "low") val low: Short,
    @field:Json(name = "image") val image: String) : Parcelable