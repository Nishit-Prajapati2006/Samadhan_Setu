package com.example.samadhansetu

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Worker(
    var workerId: String? = null, // Firebase key
    var name: String? = null,
    var phone: String? = null,
    var profession: String? = null
) : Serializable // Serializable to pass between activities