package com.example.houseclean

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.houseclean.databinding.ActivityAddHouseBinding
import com.example.houseclean.model.House
import com.example.houseclean.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class AddHouseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHouseBinding
    private val user = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://housecleanaveiro-default-rtdb.europe-west1.firebasedatabase.app/")
    private val storage = FirebaseStorage.getInstance().reference
    private var granted = true
    /*private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted = granted && it
        if (!granted) Toast.makeText(this, "Permissions needed!", Toast.LENGTH_SHORT).show()
    }*/
    @RequiresApi(Build.VERSION_CODES.N)
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.CAMERA, false) -> {
                Toast.makeText(this, "Camera permission needed!", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(android.Manifest.permission.CAMERA, false) -> {
                Toast.makeText(this, "Camera permission needed!", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(android.Manifest.permission.CAMERA, false) -> {
                Toast.makeText(this, "Camera permission needed!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Camera permission needed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private lateinit var tmpImageUri: Uri
    private var tmpImageFilePath = ""
    private val selectImg = registerForActivityResult(ActivityResultContracts.GetContent()) {
        tmpImageUri = it!!
        //uploadHouseImage(tmpImageUri)
        updateImage()
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            //uploadHouseImage(tmpImageUri)
            updateImage()
        }
    }
    private val getLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (it.data != null) {
                binding.etHouseAddress.setText(it.data?.getStringExtra("address").toString())
                binding.houseLocation.setText(it.data?.getStringExtra("location").toString())
            } else {
                Toast.makeText(this, "Fail getting address/location!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show()
        }
    }
    private lateinit var dbUser: User
    private lateinit var houseID: String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHouseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        dbUser = intent.getSerializableExtra("user") as User
        houseID = intent.getStringExtra("houseID").toString()

        binding.cameraBtn2.setOnClickListener {
            getPermissions()
            if (!granted) {
                granted = true
            }else{
                tmpImageUri = FileProvider.getUriForFile(this,
                    "com.example.houseclean.provider",
                    createImageFile(houseID).also {
                        tmpImageFilePath = it.absolutePath
                    }
                )
                takePicture.launch(tmpImageUri)
            }
        }

        binding.galleryBtn2.setOnClickListener {
            getPermissions()
            /*if (!granted) {
                granted = true
            }else{*/
            selectImg.launch("files/*")
            //}
        }

        binding.houseLocation.setOnClickListener {
            getLocation.launch(Intent(this, GetLocationActivity::class.java))
        }

        binding.cancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.addHouseCheckBtn.setOnClickListener{
            if (TextUtils.isEmpty(binding.etHouseAddress.text.toString().trim())) {
                Toast.makeText(this,"Please enter address!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.houseLocation.text.toString() == "Add location" || binding.houseLocation.text.toString() == "null") {
                Toast.makeText(this,"Please add location!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val house = House(houseID, binding.houseLocation.text.toString(), binding.etHouseAddress.text.toString())
            with(dbUser){
                if (houses.isNullOrEmpty()) houses = arrayListOf(house)
                else dbUser.houses?.add(house)
            }
            database.getReference("Users").child(user.uid.toString())
                .setValue(dbUser).addOnSuccessListener{
                    Toast.makeText(this, "House add success!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "Error adding house!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            /*database.getReference("Users").child(user.uid.toString()).child("houses/".plus(houseID))
                .setValue(house).addOnSuccessListener{
                    Toast.makeText(this, "House add success!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "Error adding house!", Toast.LENGTH_SHORT).show()
                    finish()
                }*/
            uploadHouseImage(tmpImageUri)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getPermissions() {
        requestPermission.launch(arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun createImageFile(id: String): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return  File.createTempFile("house".plus(id), ".png", storageDir)
    }

    private fun uploadHouseImage(uri: Uri) {
        storage.child(user.uid.toString().plus("/houses/").plus(houseID).plus("/housePic")).putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(this, "Imagede upload success!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Imaged upload failed!", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun updateImage() {
        Glide.with(this).load(tmpImageUri).into(binding.addHouseImg)
    }
}