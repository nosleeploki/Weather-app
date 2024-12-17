package com.example.weatherapp.Weather.Data

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationAdapter(
    private val activity: FragmentActivity,
    private val forecastAdapter: ForecastAdapter // Truyền ForecastAdapter vào
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)

    fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.d("LocationAdapter", "Vị trí hiện tại: Lat: ${location.latitude}, Lng: ${location.longitude}")
                Toast.makeText(activity, "Load location", Toast.LENGTH_SHORT).show()
                // Gọi ForecastAdapter để lấy thông tin thời tiết
                forecastAdapter.fetchWeatherForecast(location.latitude, location.longitude)
            } else {
                Log.e("LocationAdapter", "Không thể lấy vị trí hiện tại")
                Toast.makeText(activity, "Can't get location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("LocationAdapter", "Lỗi khi lấy vị trí: ${e.message}")
            Toast.makeText(activity, "Can't get location", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}
