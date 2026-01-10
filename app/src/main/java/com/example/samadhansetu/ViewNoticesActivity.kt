package com.example.samadhansetu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
//import androidx.glance.visibility
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.samadhansetu.databinding.ActivityViewNoticesBinding
import com.google.firebase.database.*

class ViewNoticesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewNoticesBinding
    private lateinit var database: DatabaseReference
    private lateinit var noticeAdapter: NoticeAdapter
    private val noticeList = mutableListOf<Notice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewNoticesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarViewNotices)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup RecyclerView
        setupRecyclerView()

        // Initialize database reference and fetch data
        database = FirebaseDatabase.getInstance().getReference("Notices")
        fetchNotices()
    }

    private fun setupRecyclerView() {
        noticeAdapter = NoticeAdapter(noticeList)
        binding.rvNotices.apply {
            layoutManager = LinearLayoutManager(this@ViewNoticesActivity)
            adapter = noticeAdapter
        }
    }

    private fun fetchNotices() {
        binding.progressBarNotices.visibility = View.VISIBLE
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noticeList.clear()
                if (snapshot.exists()) {
                    for (noticeSnapshot in snapshot.children) {
                        val notice = noticeSnapshot.getValue(Notice::class.java)
                        if (notice != null) {
                            noticeList.add(notice)
                        }
                    }
                    // Sort the list to show newest notices first
                    noticeList.sortByDescending { it.timestamp }
                    noticeAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@ViewNoticesActivity, "No notices found.", Toast.LENGTH_SHORT).show()
                }
                binding.progressBarNotices.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.progressBarNotices.visibility = View.GONE
                Toast.makeText(this@ViewNoticesActivity, "Failed to load notices.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

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