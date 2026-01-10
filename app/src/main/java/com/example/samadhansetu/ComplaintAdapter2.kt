package com.example.samadhansetu

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.samadhansetu.ComplaintAdapter.ComplaintViewHolder
import com.example.samadhansetu.databinding.ItemComplaintAllotedBinding
import com.example.samadhansetu.databinding.ItemComplaintBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ComplaintAdapter2(private val complaints: MutableList<AllComplaint>) :

    RecyclerView.Adapter<ComplaintAdapter2.ComplaintViewHolder>() {

    private lateinit var database : DatabaseReference

    // ViewHolder holds the view for each item
    inner class ComplaintViewHolder(private val binding: ItemComplaintAllotedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Set the click listener on the item view
            database = FirebaseDatabase.getInstance().getReference("Complaints")

            binding.btnMarkSolved.setOnClickListener{
                val complai = complaints[position].complaintId
                   val complaintRef = database.child("$complai")
                    complaintRef.child("state").setValue("Solved").addOnSuccessListener {
                        Toast.makeText(this.itemView.context, "Complaint Solved", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this.itemView.context, "Failed to make changes", Toast.LENGTH_SHORT).show()
                    }

            }
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
            binding.tvStudentDetails.text = "from ${complaint.name} (Room ${complaint.roomNo}, ${complaint.regNo})"


            // You can add an OnClickListener here to handle item clicks
            // itemView.setOnClickListener { ... }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.example.samadhansetu.ComplaintAdapter2.ComplaintViewHolder {
        val binding = ItemComplaintAllotedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComplaintViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        holder.bind(complaints[position])
    }

    override fun getItemCount(): Int {
        return complaints.size
    }
}