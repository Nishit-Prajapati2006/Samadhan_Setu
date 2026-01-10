package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.samadhansetu.databinding.ActivityCaretakerDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class CaretakerDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityCaretakerDashboardBinding
    private lateinit var auth: FirebaseAuth
    lateinit var name: String
    lateinit var hostel: String
    lateinit var email: String
    lateinit var phone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaretakerDashboardBinding.inflate(layoutInflater)
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
        db.child("Caretaker").child(user_id).get().addOnSuccessListener {
            name= it.child("name").value.toString()
            hostel=it.child("hostel").value.toString()
            email=it.child("email").value.toString()
            phone=it.child("phone").value.toString()
//            val regNo = it.child("registrationNo")
//            val email = FirebaseDatabase.getInstance().getReference("Students").child(user_id).child("email")
//            val phone = FirebaseDatabase.getInstance().getReference("Students").child(user_id).child("mobileNo")

        }

        // Setup Toolbar and Navigation Drawer
        setSupportActionBar(binding.caretakerToolbar)
        val toggle = ActionBarDrawerToggle(
            this, binding.caretakerDrawerLayout, binding.caretakerToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.caretakerDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.caretakerNavView.setNavigationItemSelectedListener(this)

        // Load user info into nav header
        loadNavHeaderInfo()

        // Set listeners for dashboard cards
        binding.cardViewComplaints.setOnClickListener {
            // TODO: Create and navigate to an activity that shows a list of complaints

            val intent = Intent(this, ComplaintListActivity::class.java)
            intent.putExtra("old",false)
            startActivity(intent)


            Toast.makeText(this, "View Complaints Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardAddWorker.setOnClickListener {
            // TODO: Create and navigate to an activity for adding/managing workers

            val intent = Intent(this, AddWorkerActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Add Worker Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardPostNotice.setOnClickListener {
            // TODO: Create and navigate to an activity for posting notices
            val intent = Intent(this, PostNoticeActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Post Notice Clicked", Toast.LENGTH_SHORT).show()
        }
        binding.cardOldComplaints.setOnClickListener {
            // TODO: Create and navigate to an activity for posting notices
            val intent = Intent(this, ComplaintListActivity::class.java)
            intent.putExtra("old",true)
            startActivity(intent)
            Toast.makeText(this, "Old complaints Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNavHeaderInfo() {
        val user = auth.currentUser
        if (user != null) {
            val headerView = binding.caretakerNavView.getHeaderView(0)
            val nameTextView = headerView.findViewById<TextView>(R.id.nav_header_caretaker_name)
            val emailTextView = headerView.findViewById<TextView>(R.id.nav_header_caretaker_email)

            val dbRef = FirebaseDatabase.getInstance().getReference("Caretaker").child(user.uid)
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
            R.id.nav_caretaker_profile -> {
                val intent = Intent(this@CaretakerDashboardActivity, CaretakerProfile::class.java)
                intent.putExtra("name",name)
                intent.putExtra("hostel",hostel)
                intent.putExtra("email",email)
                intent.putExtra("phone",phone)
                startActivity(intent)
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show()

            }
            R.id.nav_caretaker_settings -> {
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CaretakerDashboardActivity,WorkerSolved::class.java)
                startActivity(intent)
            }
            R.id.nav_caretaker_logout -> {
                auth.signOut()
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.caretakerDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.caretakerDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.caretakerDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}