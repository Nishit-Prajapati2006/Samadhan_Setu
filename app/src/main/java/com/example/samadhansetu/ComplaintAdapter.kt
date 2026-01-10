package com.example.samadhansetu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.samadhansetu.databinding.ItemComplaintBinding

class ComplaintAdapter(private val complaints: MutableList<AllComplaint>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {


    // ViewHolder holds the view for each item
    inner class ComplaintViewHolder(private val binding: ItemComplaintBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set the click listener on the item view
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedComplaint = complaints[position]
                    val context = itemView.context

                    // Create an Intent to start ComplaintDetailActivity
                    val intent = Intent(context, ItemComplaintPendingActivity::class.java).apply {
                        // Pass the entire complaint object to the new activity
                       putExtra("name", clickedComplaint.name)
                        putExtra("reg", clickedComplaint.regNo)
                        putExtra("room", clickedComplaint.roomNo)
                        putExtra("discription", clickedComplaint.description)
                        putExtra("title", clickedComplaint.title)
                        putExtra("complaintId", clickedComplaint.complaintId)
                        putExtra("state",clickedComplaint.state)
                        putExtra("workerId",clickedComplaint.workerId)
                    }
                    context.startActivity(intent)
                }
            }
        }
        fun bind(complaint: AllComplaint) {
            binding.tvComplaintTitle.text = complaint.title
            binding.tvComplaintCategory.text = complaint.category
            binding.state.text = complaint.state?.replaceFirstChar { it.titlecase() } ?: "Unknown"
            binding.tvStudentName.text = complaint.name
            binding.tvRoomNo.text = complaint.roomNo
            binding.tvRegNo.text = complaint.regNo
            binding.tvRegNo2.text = complaint.phone


            // You can add an OnClickListener here to handle item clicks
            // itemView.setOnClickListener { ... }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val binding = ItemComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        holder.bind(complaints[position])
    }

    override fun getItemCount(): Int {
        return complaints.size
    }
}