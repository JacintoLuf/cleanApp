package com.example.houseclean

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.houseclean.databinding.FragmentPerfilBinding
import com.example.houseclean.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private var granted = false
    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val user = FirebaseAuth.getInstance().currentUser
    private var dbUser: User? = null
    private val storage = FirebaseStorage.getInstance().reference
    private lateinit var mainActivity: MainActivity
    @RequiresApi(Build.VERSION_CODES.N)
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        granted = permissions.get(android.Manifest.permission.CAMERA)!! &&
                permissions.get(android.Manifest.permission.READ_EXTERNAL_STORAGE)!! &&
                permissions.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!!
        //if (!granted) Toast.makeText(activity, "Camera permission needed!", Toast.LENGTH_SHORT).show()
        when {
            permissions.getOrDefault(android.Manifest.permission.CAMERA, false) -> {
                Toast.makeText(activity, "Camera permission needed!", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(android.Manifest.permission.READ_EXTERNAL_STORAGE, false) -> {
                Toast.makeText(activity, "Storage permission needed!", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, false) -> {
                Toast.makeText(activity, "Storage permission needed!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(activity, "Camera permission needed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private lateinit var tmpImageUri: Uri
    private var tmpImageFilePath = ""
    private val selectImg = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            uploadProfileImage(it)
            updateImage()
        }
    }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            uploadProfileImage(tmpImageUri)
            updateImage()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDbUser()
        updateImage()

        binding.perfilImage.setOnClickListener{
            getCameraPermissions()
            showImagePicDialog()
        }

        binding.logOutBtn.setOnClickListener {
            Toast.makeText(
                activity?.applicationContext,
                "Signing Out",
                Toast.LENGTH_SHORT
            )
            signOut()
        }
    }

    private fun showImagePicDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Pick Image From")
        builder.setItems(arrayOf("Camera", "Gallery"), object : DialogInterface.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when(which) {
                    0 -> {
                        if (!granted) getCameraPermissions()
                        else {
                            tmpImageUri = FileProvider.getUriForFile(mainActivity,
                                "com.example.houseclean.provider",
                                mainActivity.createImageFile().also {
                                    tmpImageFilePath = it.absolutePath
                                }
                            )
                            takePicture.launch(tmpImageUri)
                        }
                    }
                    1 -> {
                        if (!granted) getCameraPermissions()
                        else selectImg.launch("images/*")
                    }
                }
            }
        })
        builder.create().setCancelable(true)
        builder.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCameraPermissions() {
        requestPermission.launch(arrayOf(android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun uploadProfileImage(uri: Uri) {
        storage.child(user?.uid.toString().plus("/profilePic")).putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(activity, "Imagede upload success!", Toast.LENGTH_SHORT).show()
                updateImage()
            }.addOnFailureListener {
                Toast.makeText(activity, "Imaged upload failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateImage() {
        storage.child(user?.uid.toString().plus("/profilePic")).downloadUrl.addOnSuccessListener {
            if (it != null) {
                Glide.with(this).load(it).into(binding.perfilImage)
                //binding.perfilImage.setImageURI(it)
            }
        }.addOnFailureListener{
            Toast.makeText(activity, "Couldn't load profile image!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity?.applicationContext, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun updateDbUser() {
        mainActivity.database.getReference("Users").child(user?.uid.toString())
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    dbUser = snapshot.getValue(User::class.java)
                    binding.perfilName.text = dbUser?.name
                    binding.perfilEmail.text = dbUser?.email
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}