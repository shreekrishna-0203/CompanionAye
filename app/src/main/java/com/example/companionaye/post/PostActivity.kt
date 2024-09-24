package com.example.companionaye.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.companionaye.HomeActivity
import com.example.companionaye.models.Post
import com.example.companionaye.databinding.ActivityPostBinding
import com.example.companionaye.utils.POST
import com.example.companionaye.utils.POST_FOLDER
import com.example.companionaye.utils.uploadImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }
    var imageUrl: String? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.selectImage.setImageURI(uri)
            uploadImage(uri, POST_FOLDER) { url ->
                imageUrl = url // Set imageUrl once the upload is successful
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.selectImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.cancel.setOnClickListener {
            startActivity(Intent(this@PostActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton?.setOnClickListener {
            val imageUrl = imageUrl // Local copy of the imageUrl
            val caption = binding.captionEditText?.text.toString()

            if (imageUrl != null && caption.isNotBlank()) {
                val post = Post(
                    postUrl = imageUrl, // Assuming postUrl is the same as imageUrl
                    caption = caption,
                    imageUrl = imageUrl,
                    timestamp = com.google.firebase.Timestamp.now(),
                    userId = Firebase.auth.currentUser?.uid ?: ""
                )

                Firebase.firestore.collection(POST).document().set(post).addOnSuccessListener {
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document()
                        .set(post)
                    startActivity(Intent(this@PostActivity, HomeActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed to post. Try again later.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle the case where imageUrl or caption is null/empty
                Toast.makeText(this, "Image and caption cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
