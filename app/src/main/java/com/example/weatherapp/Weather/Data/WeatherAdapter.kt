package com.example.weatherapp.Weather.Data

import android.util.Log
import android.widget.Toast
import com.example.weatherapp.R
import com.example.weatherapp.Weather.GetApi.RetrofitInstance
import com.example.weatherapp.Weather.GetApi.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class WeatherAdapter(private val activity: HomeActivity) {

    fun fetchWeather(city: String) {
        val apiKey = "79f8f1afc528c17f6e0122251d47c3ce" // Thay bằng API Key của bạn

        RetrofitInstance.api.getCurrentWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherResponse ->
                        Log.d("WeatherInfo", "City: ${weatherResponse.name}")
                        Log.d("WeatherInfo", "Temperature: ${weatherResponse.main.temp}")
                        Log.d("WeatherInfo", "Humidity: ${weatherResponse.main.humidity}")

                        // Hiển thị thông tin lên các trường
                        activity.tvCity.text = weatherResponse.name
                        activity.tvTemperature.text = "${weatherResponse.main.temp.toInt()}°C"
                        activity.tvHumidity.text = "Humidity: ${weatherResponse.main.humidity}%"
                        activity.tvWeatherDescription.text = weatherResponse.weather[0].description.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                        }

                        // Hiển thị icon thời tiết
                        val iconCode = weatherResponse.weather[0].icon

                        // Lấy ID tài nguyên drawable tương ứng với iconCode
                        val iconResId = when (iconCode) {
                            "01d" -> R.drawable.clearskyn
                            "01n" -> R.drawable.clearskyn
                            "02d" -> R.drawable.fewclouds
                            "02n" -> R.drawable.fewcloudsn // Thay đổi theo tên drawable của bạn
                            "03d" -> R.drawable.scattercloudsd
                            "03n" -> R.drawable.scattercloudsn
                            "04d" -> R.drawable.brokenclouds
                            "04n" -> R.drawable.brokenclouds
                            "09d" -> R.drawable.showerraind
                            "09n" -> R.drawable.showerrainn
                            "10d" -> R.drawable.raind
                            "10n" -> R.drawable.rainn
                            "11d" -> R.drawable.thunderd
                            "11n" -> R.drawable.thundern
                            "13d" -> R.drawable.mistd
                            "13n" -> R.drawable.mistn
                            else -> R.drawable.remove // Biểu tượng mặc định nếu không tìm thấy
                        }

                        // Hiển thị hình ảnh từ drawable
                        activity.imgGlide.setImageResource(iconResId)


                        // Get location
                        val lat = weatherResponse.coord.lat
                        val lon = weatherResponse.coord.lon
                        activity.forecastAdapter.fetchWeatherForecast(lat, lon) // Gọi hàm fetchWeatherForecast
                        Log.d("WeatherInfo", "Location lat, lon: $lat , $lon")
                        Log.d("WeatherInfo", "City: ${weatherResponse.name}")
                    }
                } else {
                    Log.e("WeatherInfo", "API Error: ${response.code()} - ${response.message()}")
                    handleErrorResponse(response.code())
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("WeatherInfo", "Request failed: ${t.message}")
                Toast.makeText(activity, "Error connected", Toast.LENGTH_SHORT).show()
            }

            private fun handleErrorResponse(code: Int) {
                when (code) {
                    404 -> Toast.makeText(activity, "Wrong city name", Toast.LENGTH_SHORT).show()
                    401 -> Toast.makeText(activity, "API Key không hợp lệ", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(activity, "Lỗi: $code", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}
