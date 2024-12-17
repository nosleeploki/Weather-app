package com.example.weatherapp.Weather.GetApi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String,         // Thành phố
        @Query("appid") apiKey: String,  // API Key
        @Query("units") units: String = "metric" // Đơn vị: Celsius
    ): Call<WeatherResponse>

    @GET("data/2.5/forecast")
    fun getWeatherForecastByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,  // API Key
        @Query("units") units: String = "metric" // Đơn vị: Celsius
    ): Call<ForecastResponse>  // Dự báo theo tọa độ
}
