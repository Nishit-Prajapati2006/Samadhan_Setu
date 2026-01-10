package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.samadhansetu.databinding.ActivitySplashscreenBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        auth = Firebase.auth

        // Uncomment these lines if you have the animation and logoImageView setup
        // val splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation)
        // binding.logoImageView.startAnimation(splashAnimation)

        // The Handler is still useful to ensure the splash screen is visible for a minimum duration.
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2500) // 2.5 second delay
    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // No cached user, go to Signup
            navigateTo(Signup::class.java)
        } else {
            // User is cached, but we need to verify they still exist on the server.
            currentUser.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // reload() was successful. Now check auth.currentUser again.
                    // If the user was deleted from the console, auth.currentUser will now be null.
                    if (auth.currentUser != null ) {
                        // User is still valid, go to MainActivity
//                        navigateTo(MainActivity::class.java)
                        // Get the current user
                        val user = auth.currentUser
                        if (user != null) {
                            val userId=user.uid
                            checkUserTypeAndRedirect(userId)
                        }
//                        navigateTo(MainActivity::class.java)

                    } else {
                        // User was deleted from the backend, go to Signup
                        Toast.makeText(this, "Your session has expired. Please sign up again.", Toast.LENGTH_LONG).show()
                        navigateTo(Signup::class.java)
                    }
                } else {
                    // reload() failed. This could be due to network issues or if the token is completely invalid.
                    // In this case, it's safest to send them to the signup/login page.
                    Toast.makeText(this, "Could not verify session. Please sign in again.", Toast.LENGTH_SHORT).show()
                    navigateTo(Signup::class.java)
                }
            }
        }
    }

    /**
     * Helper function to navigate to a new activity and clear the back stack.
     */
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finish SplashActivity
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
                val intent = Intent(this,StudentDashboardActivity::class.java)
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