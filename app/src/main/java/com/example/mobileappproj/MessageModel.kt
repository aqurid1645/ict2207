package com.example.mobileappproj

data class MessageModel(
    val sender: String = "",
    val content: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)