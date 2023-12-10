package com.example.studychat.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studychat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var editUserId: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnSign: Button
    private lateinit var btnLogin: Button

    private var auth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        editUserId = findViewById(R.id.editUserId)
        editPassword = findViewById(R.id.editPassword)
        btnSign = findViewById(R.id.btnSign)
        btnLogin = findViewById(R.id.btnLogin)



        if (firebaseUser != null) {
            val intent = Intent(
                this,
                UsersActivity::class.java
            )
            startActivity(intent)
            finish()
        }


        btnSign.setOnClickListener {
            val email = editUserId.text.toString()
            val password = editPassword.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                Toast.makeText(
                    applicationContext,
                    "email and password are required",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            editUserId.setText("")
                            editPassword.setText("")
                            val intent = Intent(
                                this,
                                UsersActivity::class.java
                            )
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "email or password invalid",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

        btnLogin.setOnClickListener {
            val intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }
}