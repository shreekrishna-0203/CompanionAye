package com.example.companionaye.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.companionaye.ChatActivity
import com.example.companionaye.Models.User
import com.example.companionaye.adapters.UserAdapter
import com.example.companionaye.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchUsers()
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter(requireContext(), mutableListOf()) { user ->
            // Handle user click to start chat
            startChatWithUser(user)
        }
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
    }

    private fun fetchUsers() {
        showLoading(true)
        firestore.collection("User")
            .get()
            .addOnSuccessListener { result ->
                showLoading(false)
                val users = result.toObjects(User::class.java)
                if (users.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    userAdapter.updateUsers(users.toMutableList())
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showError("Failed to load users: ${exception.localizedMessage}")
            }
    }

    private fun startChatWithUser(user: User) {
        val intent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra("USER_NAME", user.name)

        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerViewUsers.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.textViewEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
        binding.textViewError.visibility = View.VISIBLE
        binding.recyclerViewUsers.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
