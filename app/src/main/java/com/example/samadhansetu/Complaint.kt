package com.example.samadhansetu

// Data class to hold all complaint details for Firebase
data class Complaint(
    val originalComplaint: String,
    val summary: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Submitted" // Default status
)