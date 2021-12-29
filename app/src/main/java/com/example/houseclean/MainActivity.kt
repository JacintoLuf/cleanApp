package com.example.houseclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt3 = findViewById<TextView>(R.id.textView3)
        val txt4 = findViewById<TextView>(R.id.textView4)

        val userID = intent.getStringExtra("user_id")
        val emailID = intent.getStringExtra("email_id")

        txt3.setText(userID)
        txt4.setText(emailID)
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val loginPreferences: SharedPreferences = getSharedPreferences(
            "loginPrefs",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = loginPreferences.edit()
        editor.apply {
            editor.putBoolean("IS_LOGGED", false)
        }.apply()

        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
    }
}