package com.example.studychat.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.studychat.Adapter.UserAdapter
import com.example.studychat.R
import com.example.studychat.firebase.FirebaseService
import com.example.studychat.firebase.FirebaseService.Companion.token
import com.example.studychat.modal.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import de.hdodenhof.circleimageview.CircleImageView

class UsersActivity : AppCompatActivity() {

    private lateinit var imgBack: ImageView
    private lateinit var imgProfile: CircleImageView
    private lateinit var userRecyclerView: RecyclerView

    private var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users2)

        // Initialize views using synthetic properties
        imgBack = findViewById(R.id.imgBack)
        imgProfile = findViewById(R.id.imgProfile)
        userRecyclerView = findViewById(R.id.userRecyclerView)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FirebaseService.token = token
        }

        // Set click listeners for the views
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgProfile.setOnClickListener {
            val intent = Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Configure RecyclerView
        userRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // Fetch user list
        getUsersList()
    }

    private fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == "") {
                    imgProfile.setImageResource(R.drawable.profile_image)
                } else {
                    Glide.with(this@UsersActivity).load(currentUser.profileImage).into(imgProfile)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {
                        userList.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity, userList)
                userRecyclerView.adapter = userAdapter
            }
        })
    }
}
