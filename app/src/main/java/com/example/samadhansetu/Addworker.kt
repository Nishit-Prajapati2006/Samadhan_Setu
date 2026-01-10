package com.example.samadhansetu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samadhansetu.databinding.ActivityAddWorkerBinding
import com.google.firebase.database.*
import kotlin.text.isNotEmpty
import kotlin.text.trim

class AddWorkerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWorkerBinding
    private lateinit var database: DatabaseReference
    private lateinit var workerAdapter: WorkerAdapter
    private var workerList = mutableListOf<Worker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the generated binding class name for your XML
        binding = ActivityAddWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarAddWorker)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().getReference("Workers")

        setupRecyclerView()

        // FAB to add a new worker
        binding.fabAddWorker.setOnClickListener {
            showAddOrEditWorkerDialog(null) // Pass null for adding a new worker
        }

        fetchWorkers()
    }

    private fun setupRecyclerView() {
        workerAdapter = WorkerAdapter(workerList,
            onEditClick = { worker ->
                showAddOrEditWorkerDialog(worker) // Pass worker data for editing
            },
            onDeleteClick = { worker ->
                showDeleteConfirmationDialog(worker)
            }
        )
        binding.rvWorkersList.apply {
            layoutManager = LinearLayoutManager(this@AddWorkerActivity)
            adapter = workerAdapter
        }
    }

    private fun fetchWorkers() {
        binding.tvNoWorkers.visibility = View.GONE
        // Use addValueEventListener to listen for real-time updates
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workerList.clear()
                if (snapshot.exists()) {
                    for (workerSnapshot in snapshot.children) {
                        val worker = workerSnapshot.getValue(Worker::class.java)
                        if (worker != null) {
                            worker.workerId = workerSnapshot.key
                            workerList.add(worker)
                        }
                    }
                }

                if (workerList.isEmpty()) {
                    binding.rvWorkersList.visibility = View.GONE
                    binding.tvNoWorkers.visibility = View.VISIBLE
                } else {
                    binding.rvWorkersList.visibility = View.VISIBLE
                    binding.tvNoWorkers.visibility = View.GONE
                }
                workerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddWorkerActivity, "Failed to load workers: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showAddOrEditWorkerDialog(worker: Worker?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_worker, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_worker_name)
        val etPhone = dialogView.findViewById<EditText>(R.id.et_worker_phone)
        val spinnerProfession = dialogView.findViewById<Spinner>(R.id.spinner_profession)

        // Setup Spinner
        val professions = arrayOf("Electrician", "Plumber", "Carpenter", "Cleaner", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professions)
        spinnerProfession.adapter = spinnerAdapter

        val dialogTitle = if (worker == null) "Add New Worker" else "Edit Worker Details"

        // If editing, pre-fill the fields
        worker?.let {
            etName.setText(it.name)
            etPhone.setText(it.phone)
            val professionIndex = professions.indexOf(it.profession)
            if (professionIndex != -1) {
                spinnerProfession.setSelection(professionIndex)
            }
        }

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(dialogTitle)
            .setPositiveButton(if (worker == null) "Add" else "Save") { dialog, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val profession = spinnerProfession.selectedItem.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    saveWorkerToFirebase(worker?.workerId, name, phone, profession)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)

        builder.create().show()
    }

    private fun saveWorkerToFirebase(workerId: String?, name: String, phone: String, profession: String) {
        val currentWorkerId = workerId ?: database.push().key!! // Create new key if workerId is null
        val newWorker = Worker(currentWorkerId, name, phone, profession)

        database.child(currentWorkerId).setValue(newWorker)
            .addOnSuccessListener {
                val message = if (workerId == null) "Worker added successfully" else "Worker updated successfully"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog(worker: Worker) {
        AlertDialog.Builder(this)
            .setTitle("Delete Worker")
            .setMessage("Are you sure you want to delete ${worker.name}?")
            .setPositiveButton("Delete") { _, _ ->
                worker.workerId?.let {
                    database.child(it).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(this, "${worker.name} deleted", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to delete worker", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Handle back button press on toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}