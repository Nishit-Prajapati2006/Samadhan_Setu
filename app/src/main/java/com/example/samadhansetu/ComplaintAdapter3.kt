package com.example.samadhansetu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.example.samadhansetu.databinding.ItemComplaintSolvedBinding
import com.google.firebase.database.FirebaseDatabase

// 1. Removed mutable list of adapters and other unnecessary global variables.
// The adapter should be self-contained.
class ComplaintAdapter3(private val complaints: List<AllComplaint>) :
    RecyclerView.Adapter<ComplaintAdapter3.ComplaintViewHolder>() {

    // 2. ViewHolder now correctly takes binding as its constructor parameter.
    // The logic has been moved from `init` to the `bind` method where it belongs.
    inner class ComplaintViewHolder(private val binding: ItemComplaintSolvedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // The init block is for setting listeners, not for data binding logic.
        init {
            // You can set an OnClickListener here if you want to open a detail view
            // for solved complaints. For now, it's kept clean.
        }

        // 3. The `bind` method is the correct place for all data-related logic.
        fun bind(complaint: AllComplaint) {
            // Bind basic complaint details first.
            binding.tvComplaintTitle.text = complaint.title
            val studentDetails = "From: ${complaint.name} (Room ${complaint.roomNo}, ${complaint.regNo})"
            binding.tvStudentDetails.text = studentDetails

            // 4. Fetch worker details for THIS specific complaint.
            // This logic MUST be inside `bind` to get the correct worker for each item.
            if (!complaint.workerId.isNullOrEmpty() && complaint.workerId != "null") {
                val workerRef = FirebaseDatabase.getInstance().getReference("Workers").child(complaint.workerId!!)

                // Set a placeholder while loading.
                binding.tvSolvedBy.text = "Loading worker details..."

                workerRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val workerName = snapshot.child("name").value.toString()
                        val workerProfession = snapshot.child("profession").value.toString()
                        val workerPhone = snapshot.child("phone").value.toString()

                        // 5. Update the UI INSIDE the success listener.
                        // This prevents the race condition.
                        binding.tvSolvedBy.text = "Solved by: $workerName ($workerProfession)\nContact: $workerPhone"
                    } else {
                        binding.tvSolvedBy.text = "Solved by: Worker details not found."
                    }
                }.addOnFailureListener {
                    // Handle potential failure to fetch data.
                    binding.tvSolvedBy.text = "Failed to load worker details."
                }
            } else {
                binding.tvSolvedBy.text = "Solved by: Details unavailable."
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val binding = ItemComplaintSolvedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        // Get the specific complaint for this position and pass it to the bind method.
        holder.bind(complaints[position])
    }

    override fun getItemCount(): Int {
        return complaints.size
    }
}