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

    fun encryptPasswordMD5(password: String): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(password.toByteArray())
            val hexString = StringBuilder()
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return password // Trả về mật khẩu gốc nếu có lỗi
    }

    private fun handleSignUp(nameEditText: EditText, usernameEditText: EditText, phoneEditText: EditText, passwordEditText: EditText, termsCheckBox: CheckBox) {
        val name = nameEditText.text.toString().trim()
        val username = usernameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Kiểm tra trường trống
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
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

        // Mã hóa mật khẩu bằng MD5
        val encryptedPassword = encryptPasswordMD5(password)

        // Thêm người dùng vào cơ sở dữ liệu
        val isInserted = databaseHelper.insertUser(name, username, phone, encryptedPassword)

        if (isInserted) {
            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
