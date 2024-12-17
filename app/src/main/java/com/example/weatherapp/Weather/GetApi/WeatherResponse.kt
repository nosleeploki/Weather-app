package com.example.weatherapp.Weather.GetApi


data class WeatherResponse(
    val name: String,
    val main: Main,
    val coord: Coord,
    val weather: List<Weather>
)

data class Coord(
    val lat: Double, // Vĩ độ
    val lon: Double  // Kinh độ
)

data class Main(
    val temp: Double, // Nhiệt độ
    val feels_like: Double, // Nhiệt độ cảm nhận
    val temp_min: Double, // Nhiệt độ thấp nhất
    val temp_max: Double, // Nhiệt độ cao nhất
    val humidity: Int, // Độ ẩm
    val pressure: Int, // Áp suất
    val name: String

)

data class Weather(
    val description: String,
    val icon: String
)

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City

)

data class City(
    val name: String
)

data class ForecastItem(
    val dt: Long,
    val dt_txt: String,
    val main: Main,
    val weather: List<Weather>,
)

data class FavoriteLocation(
    val id: Int,
    val locationName: String,
    val latitude: Double,
    val longitude: Double
)
