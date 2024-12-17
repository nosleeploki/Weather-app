package com.example.weatherapp.Weather.Data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.weatherapp.Login.DatabaseHelper
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private lateinit var favoriteLocationManager: FavoriteLocationManager
    private lateinit var ivFavorite: ImageView
    lateinit var etSearchLocation: EditText
    lateinit var tvCity: TextView
    lateinit var tvTemperature: TextView
    lateinit var tvHumidity: TextView
    lateinit var tvWeatherDescription: TextView
    lateinit var imgGlide: ImageView
    lateinit var icRecent: ImageView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var weatherAdapter: WeatherAdapter
    lateinit var forecastAdapter: ForecastAdapter
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Kiểm tra thời gian hiện tại
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour in 6..18) { // Ban ngày từ 6 giờ sáng đến 6 giờ chiều
            setContentView(R.layout.activity_home)
        } else { // Ban đêm
            setContentView(R.layout.activity_home_night)
        }

        // Khởi tạo FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Log.d("Checkperm", "FusedLocationClient đã được khởi tạo.")

        // Khởi tạo adapter
        weatherAdapter = WeatherAdapter(this)
        forecastAdapter = ForecastAdapter(this)
        locationAdapter = LocationAdapter(this, forecastAdapter)

        // Ánh xạ các view
        etSearchLocation = findViewById(R.id.etSearchLocation)
        tvCity = findViewById(R.id.tvCity)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvWeatherDescription = findViewById(R.id.tvWeatherDescription)
        imgGlide = findViewById(R.id.weatherIcon)

        // Tìm kiếm vị trí hiện tại
        locationAdapter.getCurrentLocation()

        icRecent = findViewById(R.id.icRecent)
        icRecent.setOnClickListener {
            locationAdapter.getCurrentLocation()
        }

        etSearchLocation.setOnEditorActionListener { v, actionId, event ->
            val location = etSearchLocation.text.toString()
            if (!TextUtils.isEmpty(location)) {
                weatherAdapter.fetchWeather(location)
                true
            } else {
                Toast.makeText(this, "Please write your city", Toast.LENGTH_SHORT).show()
                false
            }
        }

        // Ánh xạ menu
        val icMenu: ImageView = findViewById(R.id.ivMenu)
        icMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, icMenu)
            val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getInt("user_id", -1)

            if (userId != -1) {
                val dbHelper = DatabaseHelper(this)
                val favoriteLocations = dbHelper.getFavoriteLocations(userId)

                // Xóa các mục menu cũ
                popupMenu.menu.clear()

                // Thêm tên người dùng vào menu
                // Tạo một mục menu tùy chỉnh để hiển thị tên người dùng
                val userNameMenuItem = popupMenu.menu.add(0, 0, 0, "Your favorite location")
                userNameMenuItem.isEnabled = false  // Để không thể chọn mục này

                // Thêm các địa điểm yêu thích vào menu
                favoriteLocations.forEachIndexed { index, location ->
                    popupMenu.menu.add(0, location.id, index, location.locationName)
                }

                // Thêm tùy chọn "Signout"
                val SIGNOUT_MENU_ID = 9999
                popupMenu.menu.add(0, SIGNOUT_MENU_ID, favoriteLocations.size + 1, "Signout")

                // Xử lý sự kiện click vào mục menu
                popupMenu.setOnMenuItemClickListener { item ->
                    val selectedLocation = favoriteLocations.find { it.id == item.itemId }
                    if (selectedLocation != null) {
                        // Hiển thị thời tiết cho địa điểm được chọn
                        val forecastAdapter = ForecastAdapter(this)
                        forecastAdapter.fetchWeatherForecast(selectedLocation.latitude, selectedLocation.longitude, selectedLocation.locationName)
                        Log.d("MenuItem", "ItemId: ${item.itemId}, SignoutId: ${favoriteLocations.size + 1}")
                        Toast.makeText(this, "Hiển thị thời tiết cho: ${selectedLocation.locationName}", Toast.LENGTH_SHORT).show()
                    }
                    else if (item.itemId == SIGNOUT_MENU_ID) {
                        Log.d("MenuItem", "Signout menu clicked")
                        // Xóa thông tin người dùng khỏi SharedPreferences
                        with(sharedPreferences.edit()) {
                            remove("user_id") // Xóa user_id khỏi SharedPreferences
                            apply() // Áp dụng thay đổi
                        }

                        // Hiển thị thông báo đăng xuất thành công
                        Toast.makeText(this, "Sign out Success", Toast.LENGTH_SHORT).show()

                        // Chuyển hướng về màn hình đăng nhập (hoặc bất kỳ hành động cần thiết nào)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Đóng màn hình hiện tại
                    }
                    true
                }

                // Hiển thị menu
                popupMenu.show()
            } else {
                // Nếu người dùng chưa đăng nhập
                Toast.makeText(this, "Vui lòng đăng nhập để xem danh sách yêu thích", Toast.LENGTH_SHORT).show()
            }
        }

    }


}
