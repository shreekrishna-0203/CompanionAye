package com.example.companionaye

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.companionaye.Models.User
import com.example.companionaye.databinding.ActivitySignUpBinding
import com.example.companionaye.utils.USER_NODE
import com.example.companionaye.utils.USER_PROFILE_FOLDER
import com.example.companionaye.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    private lateinit var user: User
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) { imageUrl ->
                if (imageUrl != null) {
                    user.image = imageUrl
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        user = User()

        val mode = intent.getIntExtra("MODE", -1)
        if (mode == 1) {
            binding.signup.text = "Update Profile"
            loadUserProfile()
        }

        binding.signup.setOnClickListener {
            if (areFieldsValid()) {
                if (mode == 1) {
                    updateUserProfile()
                } else {
                    registerNewUser()
                }
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserProfile() {
        Firebase.firestore.collection(USER_NODE)
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject<User>()!!
                if (!user.image.isNullOrEmpty()) {
                    Picasso.get().load(user.image).into(binding.profileImage)
                }
                binding.name.editText?.setText(user.name)
                binding.email.editText?.setText(user.email)
                // Do not set password field with user.password for security reasons
            }
    }

    private fun areFieldsValid(): Boolean {
        return binding.name.editText?.text?.isNotEmpty() == true &&
                binding.email.editText?.text?.isNotEmpty() == true &&
                binding.password.editText?.text?.isNotEmpty() == true
    }

    private fun updateUserProfile() {
        user.name = binding.name.editText?.text.toString()
        user.email = binding.email.editText?.text.toString()
        Firebase.firestore.collection(USER_NODE)
            .document(FirebaseAuth.getInstance().currentUser!!.uid).set(user)
            .addOnSuccessListener {
                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                finish()
            }
    }

    private fun registerNewUser() {
        val email = binding.email.editText?.text.toString()
        val password = binding.password.editText?.text.toString()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.name = binding.name.editText?.text.toString()
                    user.email = email
                    Firebase.firestore.collection(USER_NODE)
                        .document(FirebaseAuth.getInstance().currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                            finish()
                        }
                } else {
                    Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }
}
