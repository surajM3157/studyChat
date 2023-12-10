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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference


    private lateinit var editUserId: EditText
    private lateinit var editFullName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editConfirmPassword: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhoneNumber: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Initialize EditText variables
        editUserId = findViewById(R.id.editUserId)
        editFullName = findViewById(R.id.editFullName)
        editPassword = findViewById(R.id.editPassword)
        editConfirmPassword = findViewById(R.id.editConfirmPassword)
        editEmail = findViewById(R.id.editEmail)
        editPhoneNumber = findViewById(R.id.editPhoneNumber)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnLogin = findViewById(R.id.btnLogin)

        btnSubmit.setOnClickListener {
            val userName = editFullName.text.toString()
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            val confirmPassword = editConfirmPassword.text.toString()

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(applicationContext, "username is required", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "email is required", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "password is required", Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(applicationContext, "confirm password is required", Toast.LENGTH_SHORT).show()
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(applicationContext, "password not match", Toast.LENGTH_SHORT).show()
            }
            registerUser(userName, email, password)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

   private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    val userId: String = user!!.uid

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap["userId"] = userId
                    hashMap["userName"] = userName
                    hashMap["profileImage"] = ""

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            // open home activity
                            editFullName.setText("")
                            editEmail.setText("")
                            editPassword.setText("")
                            editConfirmPassword.setText("")
                            val intent = Intent(this, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
    }
}
