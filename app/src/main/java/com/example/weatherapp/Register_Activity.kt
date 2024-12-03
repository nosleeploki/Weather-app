package com.example.weatherapp

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var databaseHelper = DatabaseHelper(this)

        val nameEditText: EditText = findViewById(R.id.name)
        val usernameEditText: EditText = findViewById(R.id.username)
        val phoneEditText: EditText = findViewById(R.id.numberphone)
        val passwordEditText: EditText = findViewById(R.id.password)
        val termsCheckBox: CheckBox = findViewById(R.id.checkBox)
        val signUpButton:Button = findViewById(R.id.signUp_button)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the terms and conditions.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isInserted = databaseHelper.insertUser(name, username, phone, password)

            if (isInserted) {
                Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        //Quay tro lai Main Activity
        val goback: TextView = findViewById(R.id.goback)

        goback.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
