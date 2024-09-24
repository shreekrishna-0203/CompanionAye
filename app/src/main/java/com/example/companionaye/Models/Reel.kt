package com.example.companionaye.models

import com.google.firebase.Timestamp

data class Reel(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val reelUrl: String? = null,  // URL for the reel (video/image)
    val timestamp: Timestamp = Timestamp.now(),
    val likes: Int = 0,
    val comments: Int = 0,



)
