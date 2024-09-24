package com.example.companionaye.models

import com.google.firebase.Timestamp

data class Post(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val postUrl: String? = null,  // Changed from imageUrl to postUrl
    val timestamp: Timestamp = Timestamp.now(),
    val likes: Int = 0,
    val comments: Int = 0,
    val imageUrl: String? ="",
    val caption: String? = ""
)