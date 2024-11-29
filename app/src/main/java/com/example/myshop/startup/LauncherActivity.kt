package com.example.myshop.startup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshop.MainActivity
import com.example.myshop.R
import com.example.myshop.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        if (FirebaseAuth.getInstance().currentUser != null){
            startActivity(Intent(this@LauncherActivity, MainActivity::class.java))
        }else{
            startActivity(Intent(this@LauncherActivity, LoginActivity::class.java))
        }
    }
}