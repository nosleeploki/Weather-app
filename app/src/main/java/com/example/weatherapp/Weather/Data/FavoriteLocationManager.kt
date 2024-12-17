package com.example.weatherapp.Weather.Data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.example.weatherapp.Login.DatabaseHelper

class FavoriteLocationManager(private val context: Context) {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dbHelper: DatabaseHelper

    init {
        sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        dbHelper = DatabaseHelper(context)
        Log.d("FavoriteLocationManager", "Initialized with sharedPreferences and dbHelper")
    }

    // Phương thức lấy userId từ SharedPreferences
    fun getUserId(): Int {
        return sharedPreferences.getInt("user_id", -1)  // Trả về user_id hoặc -1 nếu không có
    }

    fun getFavoriteLocations(userId: Int): List<DatabaseHelper.FavoriteLocation> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "user_favorites",
            arrayOf("id", "location_name", "latitude", "longitude"),
            "user_id=?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )
        val favorites = mutableListOf<DatabaseHelper.FavoriteLocation>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"))
            val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))
            val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            favorites.add(DatabaseHelper.FavoriteLocation(id, locationName, latitude, longitude))
        }
        cursor.close()
        db.close()
        return favorites
    }

    fun addFavoriteLocation(location: String, latitude: Double, longitude: Double) {
        val userId = sharedPreferences.getInt("user_id", -1)
        Log.d("FavoriteLocationManager", "User ID retrieved: $userId")

        if (userId != -1) {
            Log.d("FavoriteLocationManager", "Attempting to add favorite location: $location ($latitude, $longitude)")
            val success = dbHelper.addFavoriteLocation(userId, location, latitude, longitude)
            if (success) {
                Toast.makeText(context, "$location đã được thêm vào danh sách của bạn", Toast.LENGTH_SHORT).show()

                Log.d("FavoriteLocationManager", "Location added successfully: $location")
            } else {
                Log.e("FavoriteLocationManager", "Failed to add location: $location")
                Toast.makeText(context, "Thêm địa điểm thất bại", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w("FavoriteLocationManager", "User not logged in. Cannot add location.")
            Toast.makeText(context, "Vui lòng đăng nhập để thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeFavoriteLocation(locationName: String) {
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            val success = dbHelper.removeFavoriteLocation(userId, locationName)
            if (success) {
                Log.d("FavoriteLocationManager", "$locationName đã bị xóa khỏi danh sách yêu thích.")
                Toast.makeText(context, "$locationName đã được thêm xóa khỏi danh sách của bạn", Toast.LENGTH_SHORT).show()

            } else {
                Log.e("FavoriteLocationManager", "Không thể xóa địa điểm $locationName.")
            }
        } else {
            Log.w("FavoriteLocationManager", "Người dùng chưa đăng nhập.")
        }
    }

    fun isLocationFavorite(userId: Int, locationName: String): Boolean {
        val favoriteLocations = getFavoriteLocations(userId)  // Lấy tất cả các địa điểm yêu thích của người dùng
        return favoriteLocations.any { it.locationName == locationName }  // Kiểm tra xem địa điểm có trong danh sách yêu thích không
    }
}
