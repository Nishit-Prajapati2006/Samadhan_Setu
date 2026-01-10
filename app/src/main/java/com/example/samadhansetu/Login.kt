package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.get
import com.example.samadhansetu.databinding.ActivityLoginBinding // Make sure this matches your login XML file name
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlin.io.path.exists

class Login : AppCompatActivity() {

    // Declare the binding variable for View Binding
    private lateinit var binding: ActivityLoginBinding

    // Declare the Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- View Binding Setup ---
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // --- End View Binding Setup ---

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Set an OnClickListener for the login button
        binding.btnlogin.setOnClickListener {
            // Call the function to perform the login
            performLogin()
        }

        // Set an OnClickListener to redirect to the Signup page
        binding.redirecttosignup.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        // Get email and password from EditText fields using the binding object
        val email = binding.loginpemail.text.toString()
        val password = binding.loginpssword.text.toString()

        // --- Input Validation ---
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return // Stop the function if fields are empty
        }
        // --- End Input Validation ---

        // Use Firebase Auth to sign in a user with email and password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Toast.makeText(baseContext, "Login successful.", Toast.LENGTH_SHORT).show()

                    // Get the current user
                    val user = auth.currentUser
                    if (user != null) {
                        val userId=user.uid
                        checkUserTypeAndRedirect(userId)
                    }

//                    // Navigate to the MainActivity
//                    val intent = Intent(this, MainActivity::class.java)
//                    // Clear the activity stack so the user cannot go back to the login screen
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(intent)
//                    finish() // Finish the Login activity

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
    // Inside your login logic, after a successful Firebase Auth login...
    private fun checkUserTypeAndRedirect(userId: String) {
        val studentRef =
            FirebaseDatabase.getInstance().getReference("Students").child(userId)
        val caretakerRef =
            FirebaseDatabase.getInstance().getReference("Caretaker").child(userId)

        studentRef.get().addOnSuccessListener { studentSnapshot ->
            if (studentSnapshot.exists()) {
                // User is a student
                val intent = Intent(this, StudentDashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Not a student, check if they are a caretaker
                caretakerRef.get().addOnSuccessListener { caretakerSnapshot ->
                    if (caretakerSnapshot.exists()) {
                        // User is a caretaker
                        val intent = Intent(this, CaretakerDashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // User role not found, maybe a new user. Redirect to a role selection or profile creation page.
                        // For now, let's assume a default for new users.
                        Toast.makeText(this, "User role not defined.", Toast.LENGTH_LONG).show()
                        // e.g., startActivity(Intent(this, RoleSelectionActivity::class.java))
                    }
                }
            }
        }
    }
}