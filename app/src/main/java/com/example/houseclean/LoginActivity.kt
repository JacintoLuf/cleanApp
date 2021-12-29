package com.example.houseclean

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import java.lang.NullPointerException

class LoginActivity : AppCompatActivity() {

    var isLogged: Boolean = false
    var rememberLogin: Boolean = false
    var email: String = ""
    var password: String = ""
    var login: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginPreferences()

        val loginTxt = findViewById<TextView>(R.id.logintext)
        val registerTxt = findViewById<TextView>(R.id.registerText)
        val etMail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val rememberCheckBox = findViewById<CheckBox>(R.id.rememberCheckBox)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        rememberCheckBox.isChecked = rememberLogin
        if(rememberLogin) {
            etMail.setText(email)
            etPass.setText(password)
        }
        if (isLogged) login(etMail.text.toString(), etPass.text.toString(), rememberCheckBox.isChecked)

        loginBtn.setOnClickListener {
            when {
                TextUtils.isEmpty(etMail.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter email!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(etPass.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter password!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                !login -> {
                    val mail: String = etMail.text.toString().trim{ it <= ' '}
                    val pass: String = etPass.text.toString().trim{ it <= ' '}
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, pass)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(
                                    this@LoginActivity,
                                    "User registered sucessfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                saveLoginPreferences(
                                    true,
                                    rememberCheckBox.isChecked,
                                    etMail.text.toString(),
                                    etPass.text.toString()
                                )
                                val intent =
                                    Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", firebaseUser.email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "User registration failed!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
                login -> {
                    login(etMail.text.toString(), etPass.text.toString(), rememberCheckBox.isChecked)
                }

            }
        }
        registerTxt.setOnClickListener {
            login = !login
            if(login){
                loginTxt.setText("LOGIN")
                loginBtn.setText("LOGIN")
            } else {
                loginTxt.setText("REGISTER")
                loginBtn.setText("REGISTER")
            }
        }
    }

    private fun login(etmail: String, etpass: String, remember: Boolean) {
        val mail: String = etmail.trim{ it <= ' '}
        val pass: String = etpass.trim{ it <= ' '}
        FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveLoginPreferences(true, remember, mail, pass)
                    val intent =
                        Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                    intent.putExtra("email_id", mail)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login Failed!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun loginPreferences() {
        val loginPreferences: SharedPreferences = getSharedPreferences(
            "loginPrefs",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = loginPreferences.edit()
        if(!loginPreferences.contains("INITIALIZED")){
            editor.apply {
                editor.putBoolean("INITIALIZED", true)
                editor.putBoolean("IS_LOGGED", isLogged)
                editor.putBoolean("REMEMBER_LOGIN", rememberLogin)
                editor.putString("EMAIL", email)
                editor.putString("PASSWORD", password)
            }.apply()
            Toast.makeText(this, "Login Prefs Created!", Toast.LENGTH_SHORT).show()
        } else {
            isLogged = loginPreferences.getBoolean("IS_LOGGED", false)
            rememberLogin = loginPreferences.getBoolean("REMEMBER_LOGIN", false)
            email = loginPreferences.getString("EMAIL", "").toString()
            password = loginPreferences.getString("PASSWORD", "").toString()
            Toast.makeText(this, "Login prefs loaded!", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveLoginPreferences(logged: Boolean, remember: Boolean, em: String, pwd: String) {
        val loginPreferences: SharedPreferences = getSharedPreferences(
            "loginPrefs",
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = loginPreferences.edit()
        editor.apply {
            editor.putBoolean("IS_LOGGED", logged)
            editor.putBoolean("REMEMBER_LOGIN", remember)
            if (remember) {
                editor.putString("EMAIL", em)
                editor.putString("PASSWORD", pwd)
            } else {
                editor.putString("EMAIL", "")
                editor.putString("PASSWORD", "")
            }
        }.apply()
    }
}