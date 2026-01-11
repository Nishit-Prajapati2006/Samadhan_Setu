package com.example.samadhansetu

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.samadhansetu.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    // We don't need lateinit properties anymore, which makes the code safer.

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar with a back button
        setSupportActionBar(binding.profileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Load and display the user's data
        loadAndDisplayUserProfile()
    }

    private fun loadAndDisplayUserProfile() {
        val name=intent.getStringExtra("name")
        val roomNo=intent.getStringExtra("roomNo")
        val regNo=intent.getStringExtra("regNo")
        val email=intent.getStringExtra("email")
        val phone=intent.getStringExtra("phone")
        // Set the text for each TextView
        binding.tvProfileName.text = name
        binding.tvProfileRegNo.text = regNo
        binding.tvProfileRoom.text = roomNo
        binding.tvProfileEmail.text = email
        binding.tvProfilePhone.text = phone

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish() // Close activity if there's no user
            return
        }

//        val userId = user.uid
//        val dbRef = FirebaseDatabase.getInstance().getReference("Students").child(userId)
//
//        dbRef.get().addOnSuccessListener { snapshot ->
//            if (snapshot.exists()) {
//                // --- THIS IS THE FIX ---
//                // All UI updates are now safely inside the success listener.
//                val name = snapshot.child("name").value.toString()
//                val roomNo = snapshot.child("roomNo").value.toString()
//                val regNo = snapshot.child("registrationNo").value.toString()
//                val email = snapshot.child("email").value.toString()
//                val phone = snapshot.child("mobileNo").value.toString()
//
//
//            } else {
//                // Handle case where user profile doesn't exist in the database
//                Toast.makeText(this, "Profile data not found.", Toast.LENGTH_SHORT).show()
//                binding.tvProfileName.text = "Not available"
//                binding.tvProfileRegNo.text = "Not available"
//                binding.tvProfileRoom.text = "Not available"
//                binding.tvProfileEmail.text = user.email ?: "Not available" // Use auth email as fallback
//                binding.tvProfilePhone.text = "Not available"
//            }
//        }.addOnFailureListener {
//            // Handle failure to fetch data
//            Toast.makeText(this, "Failed to load profile.", Toast.LENGTH_SHORT).show()
//        }
    }

    // Handle the back button click in the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed() // Go back to the previous screen
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}