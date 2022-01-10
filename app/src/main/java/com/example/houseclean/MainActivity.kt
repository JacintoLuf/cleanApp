package com.example.houseclean

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.example.houseclean.databinding.ActivityMainBinding
import com.example.houseclean.model.House
import com.example.houseclean.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File

//import kotlinx.android.synthetic.main.fragment_perfil.*

class MainActivity : AppCompatActivity() {
    val Any.TAG: String get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }
    val database = FirebaseDatabase.getInstance("https://housecleanaveiro-default-rtdb.europe-west1.firebasedatabase.app/")
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var binding: ActivityMainBinding
    private val mapFragment = MapFragment()
    private val inboxFragment = InboxFragment()
    private val housesFragment = HousesFragment()
    private val perfilFragment = PerfilFragment()
    lateinit var dbUser: User
    lateinit var houseList: MutableList<House>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initDbValues()
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

    private fun initDbValues() {
        database.getReference("Users").child(user?.uid.toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "\n\ndata snapshot: " + snapshot.toString() + "\n\n")
                    //dbUser = User()
                    val tmpUser = snapshot.getValue(User::class.java)
                    if (tmpUser == null) dbUser = User()
                    else dbUser = tmpUser
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun createImageFile(): File {
        var storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile("profilePic", ".png", storageDir)
    }
}