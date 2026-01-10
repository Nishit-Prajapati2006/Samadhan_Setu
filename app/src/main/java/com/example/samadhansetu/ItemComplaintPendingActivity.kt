package com.example.samadhansetu

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.samadhansetu.databinding.ItemComplaintPendingBinding
import com.google.firebase.database.*

class ItemComplaintPendingActivity : AppCompatActivity() {
    private lateinit var binding: ItemComplaintPendingBinding
    private lateinit var databaseWorkers: DatabaseReference
    private lateinit var databaseComplaints: DatabaseReference
    var selectedItem: String? = null
    private var workerList = mutableListOf<String>()
    private var workerIDList = mutableListOf<String>()

    // 1. Declare the adapter as a class property
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemComplaintPendingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Set Complaint Details ---
        val name = intent.getStringExtra("name")
        val reg = intent.getStringExtra("reg")
        val room = intent.getStringExtra("room")
        val description = intent.getStringExtra("discription")
        val title = intent.getStringExtra("title")
        val complaintId = intent.getStringExtra("complaintId")
        val workerIdFromIntent = intent.getStringExtra("workerId")
        val state = intent.getStringExtra("state")

        binding.tvComplaintDescription.text = description
        binding.tvComplaintTitle.text = title
        val data = "From:$name   (Room $room ,$reg)"
        binding.tvStudentDetails.text = data

        // --- Database References ---
        databaseWorkers = FirebaseDatabase.getInstance().getReference("Workers")
        databaseComplaints = FirebaseDatabase.getInstance().getReference("Complaints")

        // --- Handle UI state based on the complaint's status ---
        handleComplaintState(state, workerIdFromIntent)

        // --- Setup Spinner for Pending state ---
        setupSpinner()

        // --- Handle "Allot" button click ---
        binding.btnAllot.setOnClickListener {
            if (selectedItem != null && complaintId != null) {
                val selectedWorkerIndex = workerList.indexOf(selectedItem)
                if (selectedWorkerIndex != -1) {
                    val workerToAllotId = workerIDList[selectedWorkerIndex]
                    allotComplaintToWorker(complaintId, workerToAllotId)
                }
            } else {
                Toast.makeText(this, "Please select a worker", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleComplaintState(state: String?, workerId: String?) {
        if (state == "Allotted" && workerId != null && workerId != "null") {
            // State is Allotted, hide spinner and show worker details
            binding.badgeStatusPending.text = "Allotted"
            binding.badgeStatusPending.setBackgroundResource(R.drawable.alloted_bg)
            binding.spinnerWorkers.visibility = View.GONE
            binding.btnAllot.visibility = View.GONE

            // --- THIS IS THE FIX ---
            // Fetch worker details and update UI inside the success listener
            databaseWorkers.child(workerId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val workerName = snapshot.child("name").value.toString()
                    val workerProfession = snapshot.child("profession").value.toString()
                    val workerPhone = snapshot.child("phone").value.toString()


                    binding.labelAllotWorker.text = "Alloted to ${workerName}(${workerProfession})\nphone:+91${workerPhone}"
                } else {
                    binding.labelAllotWorker.text = "Alloted to: Worker not found"
                }
            }.addOnFailureListener {
                binding.labelAllotWorker.text = "Failed to load worker details"
            }

        } else {
            // State is Pending, show the spinner and button
            binding.badgeStatusPending.text = "Pending"
            binding.badgeStatusPending.setBackgroundResource(R.drawable.pending_bg)
            binding.spinnerWorkers.visibility = View.VISIBLE
            binding.btnAllot.visibility = View.VISIBLE
            binding.labelAllotWorker.text = "Allot a Worker:"
            fetchWorkersForSpinner() // Fetch workers only if needed
        }
    }

    private fun setupSpinner() {
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workerList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkers.adapter = spinnerAdapter

        binding.spinnerWorkers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@ItemComplaintPendingActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) { }
        }
    }

    private fun fetchWorkersForSpinner() {
        databaseWorkers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workerList.clear()
                workerIDList.clear()
                if (snapshot.exists()) {
                    for (workerSnapshot in snapshot.children) {
                        val worker = workerSnapshot.getValue(Worker::class.java)
                        if (worker != null) {
                            worker.workerId = workerSnapshot.key
                            workerList.add("${worker.name}(${worker.profession})")
                            workerIDList.add(worker.workerId.toString())
                        }
                    }
                }
                spinnerAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ItemComplaintPendingActivity, "Failed to load workers: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun allotComplaintToWorker(complaintId: String, workerId: String) {
        val complaintRef = databaseComplaints.child(complaintId)
        complaintRef.child("workerId").setValue(workerId)
        complaintRef.child("state").setValue("Allotted").addOnSuccessListener {
            Toast.makeText(this, "Worker Allotted Successfully", Toast.LENGTH_SHORT).show()
            // Re-call handleComplaintState to update the UI to the "Allotted" view
            handleComplaintState("Allotted", workerId)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to allot worker", Toast.LENGTH_SHORT).show()
        }
    }
}