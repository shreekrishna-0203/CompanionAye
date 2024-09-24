package com.example.companionaye.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.companionaye.R
import com.example.companionaye.adapters.ReelsAdapter
import com.example.companionaye.databinding.FragmentReelBinding
import com.example.companionaye.models.Reel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ReelFragment : Fragment() {

    private var _binding: FragmentReelBinding? = null
    private val binding get() = _binding!!
    private lateinit var reelsAdapter: ReelsAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchReels()
    }

    private fun setupRecyclerView() {
        reelsAdapter = ReelsAdapter(requireContext(), mutableListOf())
        binding.recyclerViewReels.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reelsAdapter
        }
    }

    private fun fetchReels() {
        showLoading(true)
        firestore.collection("Reel")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { result ->
                showLoading(false)
                val reels = result.toObjects(Reel::class.java)
                val validReels = reels.filter { !it.reelUrl.isNullOrEmpty() }
                if (validReels.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    reelsAdapter.updateReels(validReels)
                }
            }
            .addOnFailureListener { exception ->
                showLoading(false)
                showError("Failed to load reels: ${exception.localizedMessage}")
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerViewReels.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.textViewEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewReels.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        binding.textViewError.text = message
        binding.textViewError.visibility = View.VISIBLE
        binding.recyclerViewReels.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
