package com.example.samadhansetu

// Use @IgnoreExtraProperties to prevent crashes if Firebase has extra fields
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AllComplaint(
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val roomNo: String? = null,
    val regNo: String? = null,
    val status: String? = null,
    var state: String?=null,
    val workerId: String? = null,
    val timestamp: Long? = null,
    var complaintId: String? = null // To store the Firebase key
)