package com.example.companionaye.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.companionaye.adapters.MyReelsRvAdapter
import com.example.companionaye.databinding.FragmentMyReelsBinding
import com.example.companionaye.models.Reel
import com.example.companionaye.utils.REEL
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class MyReelsFragment : Fragment() {

    private var _binding: FragmentMyReelsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyReelsRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchReels()
    }

    private fun setupRecyclerView() {
        adapter = MyReelsRvAdapter(requireContext(), mutableListOf())
        binding.rv.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        binding.rv.adapter = adapter
    }

    private fun fetchReels() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textViewEmpty.visibility = View.GONE
        binding.textViewError.visibility = View.GONE

        val currentUserId = Firebase.auth.currentUser?.uid ?: return

        Firebase.firestore.collection("Reel")
            .whereEqualTo("userId", currentUserId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                binding.progressBar.visibility = View.GONE
                val reels = querySnapshot.toObjects(Reel::class.java)
                if (reels.isEmpty()) {
                    binding.textViewEmpty.visibility = View.VISIBLE
                } else {
                    adapter.updateReels(reels)
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.textViewError.visibility = View.VISIBLE
                binding.textViewError.text = "Error: ${e.message}"
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}