package com.example.houseclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatDelegate
import com.example.houseclean.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.File

//import kotlinx.android.synthetic.main.fragment_perfil.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val database = FirebaseDatabase.getInstance("https://housecleanaveiro-default-rtdb.europe-west1.firebasedatabase.app/")
    private val mapFragment = MapFragment()
    private val inboxFragment = InboxFragment()
    private val housesFragment = HousesFragment()
    private val perfilFragment = PerfilFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setUpTapBar()

        binding.bottomNavBar.setItemSelected(R.id.nav_inbox)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, inboxFragment)
            commit()
        }
    }

    private fun setUpTapBar() {
        binding.bottomNavBar.setOnItemSelectedListener {
            when(it) {
                R.id.nav_map -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, mapFragment)
                        commit()
                    }
                }
                R.id.nav_inbox -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, inboxFragment)
                        commit()
                    }
                    binding.bottomNavBar.dismissBadge(R.id.nav_inbox)
                }
                R.id.nav_homes -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, housesFragment)
                        commit()
                    }
                }
                R.id.nav_perfil -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, perfilFragment)
                        commit()
                    }
                }
            }
        }
    }

    fun createImageFile(): File {
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile("profilePic", ".png", storageDir)
    }
}