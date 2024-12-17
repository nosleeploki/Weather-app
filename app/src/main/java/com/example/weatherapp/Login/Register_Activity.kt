package com.example.weatherapp.Login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class RegisterActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        val nameEditText: EditText = findViewById(R.id.name)
        val usernameEditText: EditText = findViewById(R.id.username)
        val phoneEditText: EditText = findViewById(R.id.numberphone)
        val passwordEditText: EditText = findViewById(R.id.password)
        val termsCheckBox: CheckBox = findViewById(R.id.checkBox)
        val signUpButton: Button = findViewById(R.id.signUp_button)
        val goback: TextView = findViewById(R.id.goback)

        signUpButton.setOnClickListener {
            handleSignUp(nameEditText, usernameEditText, phoneEditText, passwordEditText, termsCheckBox)
        }

        goback.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun handleSignUp(nameEditText: EditText, usernameEditText: EditText, phoneEditText: EditText, passwordEditText: EditText, termsCheckBox: CheckBox) {
        val name = nameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Kiểm tra trường trống
        if (TextUtils.isEmpty(name)) {
            nameEditText.error = "Name is required"
            return
        }
        if (TextUtils.isEmpty(username)) {
            usernameEditText.error = "Username is required"
            return
        }
        if (TextUtils.isEmpty(phone)) {
            phoneEditText.error = "Phone number is required"
            return
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.error = "Password is required"
            return
        }

        // Kiểm tra độ dài username và password
        if (username.length < 6 || username.length > 30) {
            usernameEditText.error = "Username must be between 6 and 30 characters"
            return
        }

        if (password.length < 6 || password.length > 30) {
            passwordEditText.error = "Password must be between 6 and 30 characters"
            return
        }

        // Kiểm tra độ dài số điện thoại
        if (phone.length != 10) {
            phoneEditText.error = "Phone number must be 10 numbers"
            return
        }

        // Kiểm tra người dùng đã đồng ý điều khoản
        if (!termsCheckBox.isChecked) {
            Toast.makeText(this, "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show()
            return
        }

        // Kiểm tra trùng lặp tên người dùng và số điện thoại
        if (databaseHelper.isUsernameOrPhoneExist(username, phone)) {
            Toast.makeText(this, "Username or phone number already exists.", Toast.LENGTH_SHORT).show()
            return
        }

        // Lưu mật khẩu mà không mã hóa
        val isInserted = databaseHelper.insertUser(name, username, phone, password)

        if (isInserted) {
            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
