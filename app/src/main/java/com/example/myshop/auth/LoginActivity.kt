package com.example.myshop.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshop.R
import com.example.myshop.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var phoneNumber = ""

    companion object {
        const val PHONE_NUMBER = "phone-number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.sendCodeBTN.setOnClickListener {
            phoneNumber = binding.phoneNumberET.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                binding.phoneNumberET.error = "Enter your phone number"
            } else {
                if (phoneNumber.length != 11) {
                    binding.phoneNumberET.error = "Enter your 11 digit phone number"
                } else {
                    val intent = Intent(this@LoginActivity, VerificationActivity::class.java)
                    intent.putExtra(PHONE_NUMBER, phoneNumber)
                    startActivity(intent)
                }
            }
        }
    }
}