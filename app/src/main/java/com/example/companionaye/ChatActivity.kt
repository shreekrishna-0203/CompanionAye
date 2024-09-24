package com.example.companionaye

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.companionaye.adapters.MessageAdapter
import com.example.companionaye.databinding.ActivityChatBinding
import com.example.companionaye.models.Message
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var senderId: String
    private lateinit var receiverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve senderId and receiverId from intent or wherever you set them
        senderId = "currentUserId" // Replace with actual sender ID
        receiverId = "selectedUserId" // Replace with actual receiver ID

        setupRecyclerView()
        fetchMessages()

        binding.buttonSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(mutableListOf())
        binding.recyclerViewMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = messageAdapter
        }
    }

    private fun fetchMessages() {
        firestore.collection("Messages")
            .whereEqualTo("senderId", senderId)
            .whereEqualTo("receiverId", receiverId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val messages = result.toObjects(Message::class.java)
                messageAdapter.updateMessages(messages)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                showError("Failed to load messages: ${exception.localizedMessage}")
            }
    }

    private fun sendMessage() {
        val messageText = binding.editTextMessage.text.toString().trim()
        if (messageText.isNotEmpty()) {
            val message = Message(
                senderId = senderId,
                receiverId = receiverId,
                content = messageText,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("Messages")
                .whereEqualTo("senderId", senderId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    // Handle success
                    Log.d("Firestore", "Documents fetched successfully")
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                    Log.e("Firestore", "Error fetching documents", exception)
                }


        }
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
        binding.textViewError.visibility = View.VISIBLE
    }
}


