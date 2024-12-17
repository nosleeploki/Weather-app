package com.example.weatherapp.Weather.Data

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.weatherapp.R
import com.example.weatherapp.Weather.GetApi.ForecastResponse
import com.example.weatherapp.Weather.GetApi.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class ForecastAdapter(private val activity: HomeActivity) {

    private fun updateBackgroundBasedOnWeather(iconCode: String?) {
        val weatherLayout = activity.findViewById<LinearLayout>(R.id.weatherlo)

        val backgroundResId = when (iconCode) {
            "01d" -> R.drawable.clearskyday // Example: Clear sky during the day
            "01n" -> R.drawable.clearskynight // Clear sky at night
            "02d" -> R.drawable.clearskyday // Few clouds during the day
            "02n" -> R.drawable.clearskynight // Few clouds at night
            "03d" -> R.drawable.scrattedclouds // Scattered clouds during the day
            "03n" -> R.drawable.scatternight // Scattered clouds at night
            "04d" -> R.drawable.scattercloudsday // Broken clouds during the day
            "04n" -> R.drawable.scatternight // Broken clouds at night
            "09d" -> R.drawable.showerrainday // Shower rain during the day
            "09n" -> R.drawable.showerrainnight // Shower rain at night
            "10d" -> R.drawable.showerrainday // Rain during the day
            "10n" -> R.drawable.showerrainnight // Rain at night
            "11d" -> R.drawable.thunder // Thunderstorm during the day
            "11n" -> R.drawable.thunder // Thunderstorm at night
            "13d" -> R.drawable.snowday // Snow during the day
            "13n" -> R.drawable.snownight // Snow at night
            "50d" -> R.drawable.mistday // Mist during the day
            "50n" -> R.drawable.mistnight // Mist at night
            else -> R.drawable.remove // Default background
        }

        // Set the background image based on the iconCode
        weatherLayout.setBackgroundResource(backgroundResId)
    }


    fun fetchWeatherForecast(lat: Double, lon: Double, name: String? = null) {
        val apiKey = "79f8f1afc528c17f6e0122251d47c3ce" // Thay bằng API Key của bạn

        RetrofitInstance.api.getWeatherForecastByCoordinates(lat, lon, apiKey).enqueue(object : Callback<ForecastResponse> {
            override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { forecastResponse ->
                        val forecastList = forecastResponse.list
                        if (forecastList.isNullOrEmpty()) {
                            Toast.makeText(activity, "Không có dữ liệu thời tiết", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // Hiển thị thông tin thời tiết hiện tại từ bản ghi đầu tiên
                        val currentWeather = forecastList.firstOrNull()
                        if (currentWeather != null) {
                            val tvCurrentTemp = activity.findViewById<TextView>(R.id.tvTemperature)
                            val tvCurrentDescription = activity.findViewById<TextView>(R.id.tvWeatherDescription)
                            val tvHumidity = activity.findViewById<TextView>(R.id.tvHumidity)
                            val tvCity = activity.findViewById<TextView>(R.id.tvCity)

                            tvCity.text = forecastResponse.city.name
                            tvCurrentTemp.text = "${currentWeather.main.temp.toInt()}°C"
                            tvCurrentDescription.text = currentWeather.weather[0].description.capitalize()
                            tvHumidity.text = "Humidity: ${currentWeather.main.humidity}%"
                            Log.d("APIResponse", "Response: $forecastResponse")

                            // Get the weather icon code
                            val iconCode = currentWeather.weather.get(0).icon

                            // Update background based on weather icon
                           updateBackgroundBasedOnWeather(iconCode)
                        }




                        // Lưu trữ nhiệt độ theo ngày
                        val dailyTemperatures = mutableMapOf<String, MutableList<Int>>()
                        val dailyHumidity = mutableMapOf<String, MutableList<Int>>()

                        fun convertDateToDayOfWeek(dateString: String): String {
                            // Định dạng ngày đầu vào (yyyy-MM-dd)
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val localDate = LocalDate.parse(dateString, formatter) // Chuyển đổi chuỗi thành LocalDate

                            // Lấy thứ trong tuần và định dạng tên
                            val dayOfWeek: DayOfWeek = localDate.dayOfWeek
                            return dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("en")).take(3)
                        }

                        for (forecast in forecastList) {
                            // Chuyển đổi thời gian từ UTC sang múi giờ địa phương
                            val utcDateTime = LocalDateTime.parse(forecast.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            val localDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
                                .withZoneSameInstant(ZoneId.systemDefault())
                                .toLocalDateTime()
                            val formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                            // Chuyển đổi ngày thành thứ trong tuần
                            val dayOfWeek = convertDateToDayOfWeek(formattedDate)

                            // Lấy nhiệt độ
                            val temperature = forecast.main.temp.toInt()
                            val humidity = forecast.main.humidity.toInt()
                            dailyTemperatures.computeIfAbsent(formattedDate) { mutableListOf() }.add(temperature)
                            dailyHumidity.computeIfAbsent(formattedDate) { mutableListOf() }.add(humidity)

                        }

                        // Tính nhiệt độ trung bình cho mỗi ngày
                        val averageTemperatures = dailyTemperatures.mapValues { entry ->
                            entry.value.average().toInt()
                        }

                        val averageHumidity = dailyHumidity.mapValues { entry ->
                            entry.value.average().toInt()
                        }

                        // Lấy 5 ngày tiếp theo và hiển thị nhiệt độ trung bình
                        val days = averageTemperatures.keys.take(5)
                        val dayHs = averageHumidity.keys.take(5)


                        // Cập nhật giao diện người dùng
                        val dailyTemp1 = activity.findViewById<TextView>(R.id.dailyTemp1)
                        val dailyTemp2 = activity.findViewById<TextView>(R.id.dailyTemp2)
                        val dailyTemp3 = activity.findViewById<TextView>(R.id.dailyTemp3)
                        val dailyTemp4 = activity.findViewById<TextView>(R.id.dailyTemp4)
                        val dailyTemp5 = activity.findViewById<TextView>(R.id.dailyTemp5)

                        // Cập nhật nội dung cho từng TextView
                        for (i in 0..4) {  // Duyệt qua 5 ngày đầu tiên
                            // Lấy ngày và nhiệt độ trung bình cho từng ngày
                            val currentDay = days.elementAtOrNull(i)
                            val currentHumidity = dayHs.elementAtOrNull(i)
                            val temperature = averageTemperatures[currentDay] ?: "N/A"
                            val humidity = averageHumidity[currentHumidity] ?: "N/A"

                            // Chuyển đổi ngày (currentDay) thành thứ trong tuần
                            val dayOfWeek = convertDateToDayOfWeek(currentDay ?: "")

                            // Cập nhật TextView tương ứng
                            when (i) {
                                0 -> dailyTemp1.text = "$dayOfWeek: $temperature°C - Humidity: $humidity%"
                                1 -> dailyTemp2.text = "$dayOfWeek: $temperature°C - Humidity: $humidity%"
                                2 -> dailyTemp3.text = "$dayOfWeek: $temperature°C - Humidity: $humidity%"
                                3 -> dailyTemp4.text = "$dayOfWeek: $temperature°C - Humidity: $humidity%"
                                4 -> dailyTemp5.text = "$dayOfWeek: $temperature°C - Humidity: $humidity%"
                            }
                        }



                        // Khởi tạo ivFavorite sau khi giao diện đã sẵn sàng
                        val ivFavorite = activity.findViewById<ImageView>(R.id.ivFavorite)

                        // Lấy tên thành phố và tọa độ
                        val cityName = forecastResponse.city.name
                        val latitude = lat
                        val longitude = lon

                        // Khởi tạo FavoriteLocationManager và lấy userId từ SharedPreferences
                        val favoriteLocationManager = FavoriteLocationManager(activity)
                        val userId = favoriteLocationManager.getUserId()  // Lấy userId từ SharedPreferences

                        // Kiểm tra nếu địa điểm yêu thích, nếu có thì thay đổi icon
                        val locationExists = favoriteLocationManager.isLocationFavorite(userId, cityName)

                        if (locationExists) {
                            ivFavorite.setImageResource(R.drawable.star2)  // Biểu tượng ngôi sao đầy
                        } else {
                            ivFavorite.setImageResource(R.drawable.star)  // Biểu tượng ngôi sao rỗng
                        }

                        ivFavorite.setOnClickListener {
                            // Kiểm tra nếu địa điểm đã tồn tại trong danh sách yêu thích
                            val favoriteLocations = favoriteLocationManager.getFavoriteLocations(userId)
                            val locationExists = favoriteLocations.any { it.locationName == cityName }

                            // Thêm hoặc xóa địa điểm khỏi danh sách yêu thích
                            if (!locationExists) {
                                // Thêm địa điểm vào danh sách yêu thích
                                favoriteLocationManager.addFavoriteLocation(cityName, latitude, longitude)
                                ivFavorite.setImageResource(R.drawable.star2)  // Cập nhật icon thành ngôi sao đầy
                            } else {
                                // Xóa địa điểm khỏi danh sách yêu thích
                                favoriteLocationManager.removeFavoriteLocation(cityName)
                                ivFavorite.setImageResource(R.drawable.star)  // Cập nhật icon thành ngôi sao rỗng
                            }
                        }


                        //Hiển thị icon thời tiết
                       val iconCode = currentWeather?.weather?.get(0)?.icon

                      // Lấy ID tài nguyên drawable tương ứng với iconCode
                      val iconResId = when (iconCode) {
                           "01d" -> R.drawable.clearskyd
                            "01n" -> R.drawable.clearskyn
                            "02d" -> R.drawable.fewclouds
                            "02n" -> R.drawable.fewcloudsn
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
                            "13d" -> R.drawable.snow
                            "13n" -> R.drawable.snowy
                            "50d" -> R.drawable.mistd
                            "50n" -> R.drawable.mistn
                            else -> R.drawable.remove // Biểu tượng mặc định nếu không tìm thấy
                        }

                        // Hiển thị hình ảnh từ drawable
                        activity.imgGlide.setImageResource(iconResId)



                        // Hiển thị thông tin thời tiết theo giờ
                        val hourlyForecastLayout = activity.findViewById<LinearLayout>(R.id.hourlyForecastLayoutLayout)
                        hourlyForecastLayout.removeAllViews()

                        for (forecast in forecastList.take(8)) {
                            // Chuyển đổi thời gian từ UTC sang múi giờ địa phương
                            val utcDateTime = LocalDateTime.parse(forecast.dt_txt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            val localDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
                                .withZoneSameInstant(ZoneId.systemDefault())
                                .toLocalDateTime()
                            val formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))

                            // Lấy thông tin khác
                            val temperature = "${forecast.main.temp.toInt()}°C"
                            val weatherDescription = forecast.weather[0].description
                            val iconCode = forecast.weather[0].icon

                            val iconResId = when (iconCode) {
                                "01d" -> R.drawable.clearskyd
                                "01n" -> R.drawable.clearskyn
                                "02d" -> R.drawable.fewclouds
                                "02n" -> R.drawable.fewcloudsn
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
                                "13d" -> R.drawable.snow
                                "13n" -> R.drawable.snowy
                                "50d" -> R.drawable.mistd
                                "50n" -> R.drawable.mistn
                                else -> R.drawable.remove
                            }

                            // Layout chứa icon và TextView cho mỗi dự báo
                            val forecastLayout = LinearLayout(activity).apply {
                                orientation = LinearLayout.HORIZONTAL
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    setMargins(8, 8, 8, 8)
                                }
                                gravity = Gravity.CENTER_VERTICAL
                            }

                            val weatherIcon = ImageView(activity).apply {
                                layoutParams = LinearLayout.LayoutParams(55, 55) // Kích thước icon
                                setImageResource(iconResId)
                            }

                            // Tạo TextView mới cho từng thời gian
                            val textView = TextView(activity).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    marginEnd = 16
                                }
                                text = "$formattedTime\n$temperature"
                                textSize = 14f
                                gravity = Gravity.CENTER
                                setTextColor(Color.parseColor("#ffffff"))
                            }

                            forecastLayout.addView(weatherIcon)
                            forecastLayout.addView(textView)

                            // Thêm TextView vào layout
                            hourlyForecastLayout.addView(forecastLayout)
                            val divider = View(activity).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    5 // Chiều cao của đường ngăn cách
                                ).apply {
                                    setMargins(16, 8, 16, 8)
                                }
                                setBackgroundColor(activity.resources.getColor(R.color.black)) // Màu sắc của Divider


                            }

                            // Thêm Divider vào layout chính
                            hourlyForecastLayout.addView(divider)
                        }

                    }
                } else {
                    Toast.makeText(activity, "Lỗi: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Toast.makeText(activity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
