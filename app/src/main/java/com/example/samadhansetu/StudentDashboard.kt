package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import com.example.samadhansetu.databinding.ActivityCaretakerDashboardBinding
import com.example.samadhansetu.databinding.ActivityStudentDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class StudentDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var auth: FirebaseAuth
    lateinit var name: String
    lateinit var email: String
    lateinit var phone: String
    lateinit var regNo: String
    lateinit var roomNo: String



    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            // If for some reason the user is not signed in, redirect to login
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }
        val user_id = user.uid
        val db= Firebase.database.reference
        db.child("Students").child(user_id).get().addOnSuccessListener {
            name= it.child("name").value.toString()
            regNo=it.child("registrationNo").value.toString()
            email=it.child("email").value.toString()
            phone=it.child("mobileNo").value.toString()
            roomNo = it.child("roomNo").value.toString()
//            val email = FirebaseDatabase.getInstance().getReference("Students").child(user_id).child("email")
//            val phone = FirebaseDatabase.getInstance().getReference("Students").child(user_id).child("mobileNo")

        }

        // Setup Toolbar and Navigation Drawer
        setSupportActionBar(binding.studentToolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.studentDrawerLayout, binding.studentToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.studentDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.studentNavView.setNavigationItemSelectedListener(this)

        // Load user info into nav header
        loadNavHeaderInfo()

        // Set listeners for dashboard cards
        binding.cardRegisterComplaint.setOnClickListener {
            // TODO: Create and navigate to an activity that shows a list of complaints

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


            Toast.makeText(this, "Register Complaint Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardViewNotice.setOnClickListener {
            // TODO: Create and navigate to an activity for adding/managing workers
            val intent = Intent(this, ViewNoticesActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "View Notice Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardSolvedComplaints.setOnClickListener {
            // TODO: Create and navigate to an activity for posting notices
            val intent = Intent(this, SolvedComplaints::class.java)
            intent.putExtra("reg",regNo)
            startActivity(intent)
            Toast.makeText(this, "solved complains Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardOngoingComplaints.setOnClickListener {
            // TODO: Create and navigate to an activity for posting notices
            val intent = Intent(this, ItemComplaintAllotedActivity::class.java)
            intent.putExtra("reg",regNo)
            startActivity(intent)
            Toast.makeText(this, "Ongoing Complaints Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNavHeaderInfo() {
        val user = auth.currentUser
        if (user != null) {
            val headerView = binding.studentNavView.getHeaderView(0)
            val nameTextView = headerView.findViewById<TextView>(R.id.header_user_name)
            val emailTextView = headerView.findViewById<TextView>(R.id.header_user_email)

            val dbRef = FirebaseDatabase.getInstance().getReference("Students").child(user.uid)
            dbRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    nameTextView.text = name
                    emailTextView.text = user.email
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("roomNo",roomNo)
                intent.putExtra("regNo",regNo)
                intent.putExtra("email",email)
                intent.putExtra("phone",phone)
                startActivity(intent)
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()

            }

            R.id.nav_logout -> {
                auth.signOut()
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.studentDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.studentDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.studentDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}