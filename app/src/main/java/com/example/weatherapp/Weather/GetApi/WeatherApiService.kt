package com.example.weatherapp.Weather.GetApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String,         // Thành phố
        @Query("cf91be677ee47654898a53d6f6d6d887") apiKey: String,  // API Key
        @Query("units") units: String = "metric" // Đơn vị: Celsius
    ): Call<WeatherResponse>
}
