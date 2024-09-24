package com.example.companionaye.post

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.companionaye.HomeActivity
import com.example.companionaye.models.Reel
import com.example.companionaye.databinding.ActivityPostReelBinding
import com.example.companionaye.utils.REEL
import com.example.companionaye.utils.REEL_FOLDER
import com.example.companionaye.utils.uploadVideo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PostReelActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityPostReelBinding.inflate(layoutInflater)
    }
    private var videoUrl: String? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.reelPreview.setVideoURI(uri)
            binding.reelPreview.start()
            uploadVideo(uri, REEL_FOLDER) { url ->
                videoUrl = url
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.materialToolbar.setNavigationOnClickListener {
            finish()
        }

        binding.selectVideo.setOnClickListener {
            launcher.launch("video/*")
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this@PostReelActivity, HomeActivity::class.java))
            finish()
        }

        binding.postButton.setOnClickListener {
            postReel()
        }
    }

    private fun postReel() {
        val caption = binding.captionEditText.text.toString()

        if (videoUrl != null && caption.isNotBlank()) {
            val reel = Reel(
                userId = Firebase.auth.currentUser!!.uid,
                content = caption,  // Changed from caption to content
                reelUrl = videoUrl,  // Changed from videoUrl to reelUrl
                username = Firebase.auth.currentUser?.displayName ?: "Anonymous"  // Add username
            )

            Firebase.firestore.collection(REEL).document().set(reel).addOnSuccessListener {
                Toast.makeText(this, "Reel posted successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@PostReelActivity, HomeActivity::class.java))
                finish()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to post reel: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please select a video and add a caption", Toast.LENGTH_SHORT).show()
        }
    }
}