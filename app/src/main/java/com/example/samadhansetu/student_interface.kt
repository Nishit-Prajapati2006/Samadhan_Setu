package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.samadhansetu.databinding.ActivityStudentInterfaceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.text.isEmpty
import kotlin.text.trim

class student_interface : AppCompatActivity() {


    private lateinit var binding: ActivityStudentInterfaceBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityStudentInterfaceBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Students")

        // Pre-fill email address from the authenticated user
//        val currentUserEmail = auth.currentUser?.email
//        binding.etEmail.setText(currentUserEmail)

        // Set listener for the finish button
        binding.btnFinish.setOnClickListener {
            saveStudentProfile()
        }
    }

    private fun saveStudentProfile() {
        // Get text from all EditText fields
        val name = binding.etName.text.toString().trim()
        val registrationNo = binding.etRegistrationNo.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val mobileNo = binding.etMobileNo.text.toString().trim()
        val roomNo = binding.etRoomNo.text.toString().trim()

        // --- Input Validation ---
        if (name.isEmpty() || registrationNo.isEmpty() || email.isEmpty() || mobileNo.isEmpty() || roomNo.isEmpty()) {
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
        val studentData = StudentData(name, registrationNo, email, mobileNo, roomNo)
//        sessionManager=SessionManager(this)
//        sessionManager.saveUser(name,registrationNo,email,mobileNo,roomNo)
        // Save the data to Firebase Realtime Database under the user's unique ID
        database.child(userId).setValue(studentData)
            .addOnSuccessListener {
                // Success listener
                Toast.makeText(this, "Profile completed successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to the MainActivity or another destination
                val intent = Intent(this, StudentDashboardActivity::class.java)
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