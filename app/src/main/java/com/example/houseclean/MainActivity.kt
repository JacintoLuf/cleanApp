package com.example.houseclean

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.example.houseclean.databinding.ActivityMainBinding
import com.example.houseclean.model.Transaction
import com.example.houseclean.model.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File

class MainActivity : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance("https://housecleanaveiro-default-rtdb.europe-west1.firebasedatabase.app/")
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var binding: ActivityMainBinding
    private val mapFragment = MapFragment()
    private val inboxFragment = InboxFragment()
    private val housesFragment = HousesFragment()
    private val perfilFragment = PerfilFragment()
    private lateinit var dialog: AlertDialog
    lateinit var dbUser: User
    private var not_Trans: Transaction? = null
    private var granted = false
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        granted = permissions[Manifest.permission.INTERNET]!! &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION]!! &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION]!!
        if (!granted) {
            Toast.makeText(this, "Location permission needed!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        initDbValues()
        setUpTapBar()
        checkTransactions()
        if (!granted) getPermissions()

        val builder = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.apply_house_dialog, null)
        dialog = builder.setView(view).setCancelable(false).create()
        val btn1 = view.findViewById<FloatingActionButton>(R.id.acceptBtn)
        val btn2 = view.findViewById<FloatingActionButton>(R.id.cancelBtn)
        btn1.setOnClickListener {
            updateTransStatus(1)
            dialog.dismiss()
        }
        btn2.setOnClickListener {
            updateTransStatus(0)
            dialog.dismiss()
        }

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

    private fun checkTransactions() {
        database.getReference("Transactions").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                   if (it.child("clientID").getValue(String::class.java).equals(user?.uid.toString())
                       && it.child("status").getValue(String::class.java).equals("applying")) {
                       not_Trans = it.getValue(Transaction::class.java)
                       dialog.show()
                       dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                   }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateTransStatus(status: Int) {
        if (status == 1) {
            not_Trans?.status = "onTheWay"
            database.getReference("Transaction/${not_Trans?.transactionID}").setValue(not_Trans)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cleaner on the way", Toast.LENGTH_SHORT).show()
                }
        } else if (status == 0) {
            not_Trans?.status = "canceled"
            database.getReference("Transaction/${not_Trans?.transactionID}").setValue(not_Trans)
                .addOnSuccessListener {
                    Toast.makeText(this, "Transaction canceled", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun initDbValues() {
        database.getReference("Users").child(user?.uid.toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tmpUser = snapshot.getValue(User::class.java)
                    dbUser = if (tmpUser == null) User() else tmpUser
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun not(i: Int) {
        when(i) {
            0 -> binding.bottomNavBar.showBadge(R.id.nav_map)
            1 -> binding.bottomNavBar.showBadge(R.id.nav_inbox)
        }
    }

    fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile("profilePic", ".png", storageDir)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getPermissions() {
        requestPermission.launch(arrayOf(Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
}