package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.weatherapp.Login.DatabaseHelper
import com.example.weatherapp.Login.RegisterActivity
import com.example.weatherapp.Weather.Data.HomeActivity
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val databaseHelper = DatabaseHelper(this)

        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)
        val checkBox: CheckBox = findViewById(R.id.checkBox)
        val loginButton: Button = findViewById(R.id.login_button)
        val registerTextView: TextView = findViewById(R.id.register)

        val savedUsername = sharedPreferences.getString("username", "")
        if (!savedUsername.isNullOrEmpty()) {
            usernameEditText.setText(savedUsername)
            checkBox.isChecked = true // Automatically check "Remember me" if username exists
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Kiểm tra độ dài username và password
            if (username.length < 6 || username.length > 30) {
                usernameEditText.error = "Username must be between 6 and 30 characters"
                return@setOnClickListener
            }
            if (password.length < 6 || password.length > 30) {
                passwordEditText.error = "Password must be between 6 and 30 characters"
                return@setOnClickListener
            }

            // Kiểm tra các trường trống
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please fill your information!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mã hóa mật khẩu và so sánh với cơ sở dữ liệu
            val user = databaseHelper.getUser(username, password)
            if (user != null) {
                // Lưu ID người dùng vào SharedPreferences
                val editor = sharedPreferences.edit()
                editor.putInt("user_id", user.id)  // Lưu ID người dùng
                editor.putString("username", username)
                editor.apply()

                // Lưu "Remember me" nếu người dùng chọn
                if (checkBox.isChecked) {
                    editor.putString("username", username)
                    editor.apply()
                } else {

                }

                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show()

                // Chuyển đến HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Wrong username or password, please try again!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
