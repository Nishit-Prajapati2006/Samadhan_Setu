package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.samadhansetu.databinding.ActivityCaretakerInterfaceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.text.isEmpty
import kotlin.text.trim

class caretaker_interface : AppCompatActivity() {

    private lateinit var binding: ActivityCaretakerInterfaceBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaretakerInterfaceBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Caretaker")

        // Set listener for the finish button
        binding.btnFinish.setOnClickListener {
            saveCaretakerProfile()
        }
    }

    private fun saveCaretakerProfile() {
        // Get text from all EditText fields
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val mobileNo = binding.etMobileNo.text.toString().trim()
        val hostel= binding.etHostel.text.toString().trim()

        // --- Input Validation ---
        if (name.isEmpty() || email.isEmpty() || mobileNo.isEmpty() || hostel.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the unique ID of the currently logged-in user
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_LONG).show()
            // Optionally, redirect to login screen
            // startActivity(Intent(this, Login::class.java))
            // finish()
            return
        }

        // Create a StudentData object
        val caretakerData= mapOf(
            "name" to name,
            "email" to email,
            "phone" to mobileNo,
            "hostel" to hostel
        )
//        sessionManager=SessionManager(this)
//        sessionManager.saveUser(name,registrationNo,email,mobileNo,roomNo)
        // Save the data to Firebase Realtime Database under the user's unique ID
        database.child(userId).setValue(caretakerData)
            .addOnSuccessListener {
                // Success listener
                Toast.makeText(this, "Profile completed successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to the MainActivity or another destination
                val intent = Intent(this, CaretakerDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // Failure listener
                Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}