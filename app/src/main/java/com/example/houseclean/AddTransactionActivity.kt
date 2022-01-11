package com.example.houseclean

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.houseclean.databinding.ActivityAddHouseBinding
import com.example.houseclean.databinding.ActivityAddTransactionBinding
import com.example.houseclean.model.House
import com.example.houseclean.model.Transaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val user = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://housecleanaveiro-default-rtdb.europe-west1.firebasedatabase.app/")
    private val storage = FirebaseStorage.getInstance().reference
    private lateinit var transaction: Transaction
    private lateinit var house: House

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        house = intent.getSerializableExtra("house") as House

        binding.addTransactionCheckBtn.setOnClickListener {

        }
    }
}