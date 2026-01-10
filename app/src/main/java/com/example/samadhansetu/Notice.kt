package com.example.samadhansetu

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Notice(
    val title: String? = null,
    val description: String? = null,
    val timestamp: Long? = null, // To sort notices by date
    var noticeId: String? = null // To store the Firebase key
)