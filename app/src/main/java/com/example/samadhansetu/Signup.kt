package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.samadhansetu.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class Signup : AppCompatActivity() {

    // Declare the binding variable for View Binding
    private lateinit var binding: ActivitySignupBinding

    // Declare the Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No need for enableEdgeToEdge() if you are using a simple layout

        // --- View Binding Setup ---
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // --- End View Binding Setup ---

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Set an OnClickListener for the signup button
        binding.btnRegister.setOnClickListener {
            // Call the function to perform the signup
            performSignUp()
        }
    }

    private fun performSignUp() {
        // Get email and password from EditText fields using the binding object
        val email = binding.signupemail.text.toString()
        val password = binding.signuppssword.text.toString()

        // --- Input Validation ---
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return // Stop the function if fields are empty
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            return // Stop the function if password is too short
        }
        // --- End Input Validation ---

        // Use Firebase Auth to create a user with email and password
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success
                    Toast.makeText(baseContext, "Account created successfully.", Toast.LENGTH_SHORT).show()

                    // Navigate to the MainActivity
                    val intent = Intent(this@Signup, catagory::class.java)
                    // Clear the activity stack so the user cannot go back to the signup screen
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Finish the Signup activity

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }
}