package com.example.presa2.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val main: MainData,
    val weather: List<Weather>,
    val name: String,
    val rain: RainData?
)

data class MainData(
    val temp: Float
)

data class Weather(
    val description: String
)

data class RainData(
    @SerializedName("3h")
    val `3h`: Float = 0f
)