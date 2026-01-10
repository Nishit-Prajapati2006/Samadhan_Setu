package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samadhansetu.databinding.ActivityOngoingComplaintsBinding // Use the new layout binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ItemComplaintAllotedActivity : AppCompatActivity() {

    // 1. Use the correct binding for the activity layout
    private lateinit var binding: ActivityOngoingComplaintsBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // 2. Use only ONE adapter and ONE list
    private lateinit var complaintAdapter: ComplaintAdapter2
    private val allotedComplaintsList = mutableListOf<AllComplaint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the correct layout
        binding = ActivityOngoingComplaintsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarOngoing)
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
        complaintAdapter = ComplaintAdapter2(allotedComplaintsList)
        binding.rvOngoingComplaints.apply { // Bind to the correct RecyclerView ID
            layoutManager = LinearLayoutManager(this@ItemComplaintAllotedActivity)
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

        binding.tvNoOngoingComplaints.visibility = View.GONE

        // Query the database to get complaints filtered by the student's registration number
        val regNo = intent.getStringExtra("reg")
        if (regNo.isNullOrEmpty()) {
            Toast.makeText(this, "Registration number not found.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database.orderByChild("regNo").equalTo(regNo).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allotedComplaintsList.clear() // Clear the list before adding new data
                if (snapshot.exists()) {
                    for (complaintSnapshot in snapshot.children) {
                        val complaint = complaintSnapshot.getValue(AllComplaint::class.java)
                        // Check if the complaint status is "Alloted"
                        if (complaint != null && complaint.state.equals("Allotted", ignoreCase = true)) {
                            complaint.complaintId = complaintSnapshot.key // Store the Firebase key
                            allotedComplaintsList.add(complaint)
                        }
                    }
                }

                if (allotedComplaintsList.isEmpty()) {
                    binding.tvNoOngoingComplaints.visibility = View.VISIBLE
                }

                // Reverse the list to show newest complaints first
                allotedComplaintsList.reverse()
                complaintAdapter.notifyDataSetChanged() // Notify the single adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ItemComplaintAllotedActivity, "Failed to load complaints: ${error.message}", Toast.LENGTH_LONG).show()
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
        val intent = Intent(this, StudentDashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}