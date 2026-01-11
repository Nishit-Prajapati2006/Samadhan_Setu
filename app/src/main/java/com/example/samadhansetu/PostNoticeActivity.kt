package com.example.samadhansetu

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.samadhansetu.databinding.ActivityPostNoticeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PostNoticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostNoticeBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityPostNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbarPostNotice)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Database reference to the "Notices" node
        database = FirebaseDatabase.getInstance().getReference("Notices")

        binding.btnPublishNotice.setOnClickListener {
            publishNotice()
        }
    }

    private fun publishNotice() {
        val title = binding.etNoticeTitle.text.toString().trim()
        val description = binding.etNoticeDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique key for the notice
        val noticeId = database.push().key

        if (noticeId == null) {
            Toast.makeText(this, "Failed to create notice entry.", Toast.LENGTH_SHORT).show()
            return
        }

        val notice = Notice(
            title = title,
            description = description,
            timestamp = System.currentTimeMillis(),
            noticeId = noticeId
        )

        database.child(noticeId).setValue(notice)
            .addOnSuccessListener {
                Toast.makeText(this, "Notice published successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after publishing
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to publish notice: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Handle back button on toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}