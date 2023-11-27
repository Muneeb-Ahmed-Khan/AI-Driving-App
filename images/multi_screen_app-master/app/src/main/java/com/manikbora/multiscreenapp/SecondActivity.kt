package com.manikbora.multiscreenapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class SecondActivity : AppCompatActivity() {
    private lateinit var editText2: EditText
    private lateinit var editText: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnFirstActivity: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        initializeViews()
        setupButtonClick()
    }

    private fun initializeViews() {
        editText2 = findViewById(R.id.editTextText2)
        editText = findViewById(R.id.editTextText)
        editTextEmail = findViewById(R.id.editTextTextEmailAddress)
        editTextAddress = findViewById(R.id.editTextTextPostalAddress)
        editTextPassword = findViewById(R.id.editTextTextPassword)
        btnFirstActivity = findViewById(R.id.btnFirstActivity)
    }

    private fun setupButtonClick() {
        btnFirstActivity.setOnClickListener {
            if (validateCredentials()) {
                navigateToMainActivity()
            } else {
                // Handle a case where not all credentials are filled
                // You can show a toast message or perform any other action here
            }
        }
    }

    private fun validateCredentials(): Boolean {
        return listOf(
            editText2.text.toString(),
            editText.text.toString(),
            editTextEmail.text.toString(),
            editTextAddress.text.toString(),
            editTextPassword.text.toString()
        ).all { it.isNotEmpty() }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
