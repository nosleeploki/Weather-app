package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.weatherapp.Login.DatabaseHelper
import com.example.weatherapp.Login.RegisterActivity
import com.example.weatherapp.Weather.Data.HomeActivity

class MainActivity : AppCompatActivity() {

        private lateinit var sharedPreferences: SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)
            sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
            var databaseHelper = DatabaseHelper(this)


            val usernameEditText: EditText = findViewById(R.id.username)
            val passwordEditText: EditText = findViewById(R.id.password)
            val checkBox: CheckBox = findViewById(R.id.checkBox)
            val loginButton: Button = findViewById(R.id.login_button)
            val registerTextView: TextView = findViewById(R.id.register)


            val savedUsername = sharedPreferences.getString("username", "")
            if (!savedUsername.isNullOrEmpty()) {
                usernameEditText.setText(savedUsername)
                checkBox.isChecked = true // Tự động đánh dấu "Remember me" nếu có username
            }

            registerTextView.setOnClickListener {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }

            loginButton.setOnClickListener {
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Please fill your information!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val user = databaseHelper.getUser(username, password)
                if (user != null) {
                    if (checkBox.isChecked) {
                        val editor = sharedPreferences.edit()
                        editor.putString("username", username)
                        editor.apply()
                        Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this,"Wrong username or password, please try again!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }