package com.example.weatherapp.Login

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_PHONE TEXT,
                $COLUMN_PASSWORD TEXT,
                $COLUMN_CREATED_AT TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)

        // Tạo bảng cho địa điểm yêu thích
        val createFavoritesTableQuery = """
        CREATE TABLE user_favorites (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            location_name TEXT,
            latitude REAL,
            longitude REAL,
            FOREIGN KEY(user_id) REFERENCES users(id)
        );
    """.trimIndent()
        db?.execSQL(createFavoritesTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Nếu phiên bản >= 2, thêm bảng yêu thích
        if (oldVersion < 3) {
            val createFavoritesTableQuery = """
            CREATE TABLE user_favorites (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                location_name TEXT,
                latitude REAL,
                longitude REAL,
                FOREIGN KEY(user_id) REFERENCES users(id)
            );
        """.trimIndent()
            db?.execSQL(createFavoritesTableQuery)
        }
    }

    // Thêm địa điểm yêu thích
    fun addFavoriteLocation(userId: Int, locationName: String, latitude: Double, longitude: Double): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("location_name", locationName)
            put("latitude", latitude)
            put("longitude", longitude)
        }
        val result = db.insert("user_favorites", null, values)
        return result != -1L
    }

    // Lấy danh sách địa điểm yêu thích của người dùng
    fun getFavoriteLocations(userId: Int): List<FavoriteLocation> {
        val db = readableDatabase
        val cursor = db.query(
            "user_favorites",
            arrayOf("id", "location_name", "latitude", "longitude"),
            "user_id=?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        val favorites = mutableListOf<FavoriteLocation>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"))
            val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"))
            val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
            favorites.add(FavoriteLocation(id, locationName, latitude, longitude))
        }
        cursor.close()
        db.close()
        return favorites
    }

    // Xóa địa điểm yêu thích
    fun removeFavoriteLocation(userId: Int, locationName: String): Boolean {
        val db = writableDatabase
        val result = db.delete(
            "user_favorites",
            "user_id=? AND location_name=?",
            arrayOf(userId.toString(), locationName)
        )
        return result > 0
    }

    // Lớp dữ liệu cho địa điểm yêu thích
    data class FavoriteLocation(
        val id: Int,
        val locationName: String,
        val latitude: Double,
        val longitude: Double
    )


    fun insertUser(name: String, username: String, phone: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_USERNAME, username)
            put(COLUMN_PHONE, phone)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_CREATED_AT, getCurrentTime())
        }
        val result = db.insert(TABLE_NAME, null, values)
        return result != -1L
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getUser(username: String, password: String): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_USERNAME, COLUMN_PHONE, COLUMN_PASSWORD, COLUMN_CREATED_AT),
            "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?",
            arrayOf(username, password),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    data class User(
        val id: Int,
        val name: String,
        val username: String,
        val phone: String,
        val password: String,
        val createdAt: String
    )

    fun isUsernameOrPhoneExist(username: String, phone: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID),
            "$COLUMN_USERNAME=? OR $COLUMN_PHONE=?",
            arrayOf(username, phone),
            null,
            null,
            null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

}
