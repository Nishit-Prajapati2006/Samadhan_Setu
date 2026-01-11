package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samadhansetu.databinding.ActivitySolvedComplaintsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class WorkerSolved : AppCompatActivity() {

    // 1. Use the correct binding for the activity layout
    private lateinit var binding: ActivitySolvedComplaintsBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // 2. Use only ONE adapter and ONE list
    private lateinit var complaintAdapter: ComplaintAdapter3
    private val solvedcomplaintslist = mutableListOf<AllComplaint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        // Inflate the correct layout
        binding = ActivitySolvedComplaintsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarSolved)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Complaints")

        // Setup the RecyclerView correctly
        setupRecyclerView()

        // Fetch only the relevant complaints for the current student
        fetchStudentComplaints()
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with the list for "Alloted" complaints
        complaintAdapter = ComplaintAdapter3(solvedcomplaintslist)
        binding.rvSolvedComplaints.apply { // Bind to the correct RecyclerView ID
            layoutManager = LinearLayoutManager(this@WorkerSolved)
            adapter = complaintAdapter
        }
    }

    private fun fetchStudentComplaints() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in.", Toast.LENGTH_SHORT).show()
            finish() // Close activity if user is not logged in
            return
        }

        binding.tvNoSolvedComplaints.visibility = View.GONE

        // Query the database to get complaints filtered by the student's registration number
//        val regNum = intent.getStringExtra("reg")
//        if (regNum.isNullOrEmpty()) {
//            Toast.makeText(this, "Registration number not found.", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }

        database.orderByChild("state").equalTo("Solved").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                solvedcomplaintslist.clear() // Clear the list before adding new data
                if (snapshot.exists()) {
                    for (complaintSnapshot in snapshot.children) {
                        val complaint = complaintSnapshot.getValue(AllComplaint::class.java)
                        // Check if the complaint status is "Alloted"
                        if (complaint != null ) {
                            complaint.complaintId = complaintSnapshot.key // Store the Firebase key
                            solvedcomplaintslist.add(complaint)
                        }
                    }
                }

                if (solvedcomplaintslist.isEmpty()) {
                    binding.tvNoSolvedComplaints.visibility = View.VISIBLE
                }

                // Reverse the list to show newest complaints first
                solvedcomplaintslist.reverse()
                complaintAdapter.notifyDataSetChanged() // Notify the single adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkerSolved, "Failed to load complaints: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Handle the toolbar's back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    // Correct the onBackPressed to go back to the student's MainActivity
    override fun onBackPressed() {
        super.onBackPressed()
        // You might want to remove this if you just want the default back behavior
        // But if you want to ensure it goes to the main screen, this is how.
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}