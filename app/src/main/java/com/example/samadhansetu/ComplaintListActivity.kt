package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samadhansetu.databinding.ActivityCaretakerDashboardBinding
import com.example.samadhansetu.databinding.ActivityComplaintListBinding
import com.google.firebase.database.*

class ComplaintListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComplaintListBinding
    private lateinit var database: DatabaseReference
    private lateinit var complaintAdapter: ComplaintAdapter
    private lateinit var complaintAdapter2: ComplaintAdapter
    private lateinit var complaintAdapter3: ComplaintAdapter
    private val complaintList = mutableListOf<AllComplaint>()
    private val oldcomplaints = mutableListOf<AllComplaint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar with a back button
        setSupportActionBar(binding.complaintListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var check = intent.getBooleanExtra("old",true)
        Toast.makeText(this@ComplaintListActivity, check.toString(), Toast.LENGTH_LONG).show()
//        var check2 = intent.getBooleanExtra("old2",false)
        // Initialize RecyclerView
        if(!check){
            setupRecyclerView()
            complaintAdapter3=complaintAdapter
        }
        else{
            setupRecyclerView2()
            complaintAdapter3=complaintAdapter2
        }


        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("Complaints")

        // Fetch the data
        fetchComplaints()



    }

    private fun setupRecyclerView() {
        complaintAdapter = ComplaintAdapter(complaintList)
        binding.rvComplaints.apply {
            layoutManager = LinearLayoutManager(this@ComplaintListActivity)
            adapter = complaintAdapter
        }
    }

    private fun setupRecyclerView2() {
        complaintAdapter2 = ComplaintAdapter(oldcomplaints)
        binding.rvComplaints.apply {
            layoutManager = LinearLayoutManager(this@ComplaintListActivity)
            adapter = complaintAdapter2
        }
    }

    private fun fetchComplaints() {
        binding.progressBar.visibility = View.VISIBLE

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear() // Clear the list before adding new data
                if (snapshot.exists()) {
                    for (complaintSnapshot in snapshot.children) {
                        val complaint = complaintSnapshot.getValue(AllComplaint::class.java)
                        if (complaint != null) {
                            complaint.complaintId = complaintSnapshot.key // Store the Firebase key
                            complaint.state=complaintSnapshot.child("state").value.toString()
                            if (complaint.state=="pending"){
                                complaintList.add(complaint)
                            }
                            else if (complaint.state=="Allotted"){
                                oldcomplaints.add(complaint)
                            }
                        }
                    }
                    // Reverse the list to show newest complaints first
                    complaintList.reverse()
                    oldcomplaints.reverse()
                    complaintAdapter3.notifyDataSetChanged() // Notify the adapter about the data change
                } else {
                    Toast.makeText(this@ComplaintListActivity, "No complaints found.", Toast.LENGTH_SHORT).show()
                }
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ComplaintListActivity, "Failed to load complaints: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Handle back button press on toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent= Intent(this, CaretakerDashboardActivity::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}