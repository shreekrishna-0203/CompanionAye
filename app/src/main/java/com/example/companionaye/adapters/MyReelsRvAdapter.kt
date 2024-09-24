package com.example.companionaye.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.companionaye.R
import com.example.companionaye.ReelPlaybackActivity
import com.example.companionaye.databinding.ItemReelBinding
import com.example.companionaye.models.Reel

class MyReelsRvAdapter(
    private val context: Context,
    private var reels: MutableList<Reel>
) : RecyclerView.Adapter<MyReelsRvAdapter.ReelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val binding = ItemReelBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.bind(reels[position])
    }

    override fun getItemCount() = reels.size

    fun updateReels(newReels: List<Reel>) {
        reels.clear()
        reels.addAll(newReels)
        notifyDataSetChanged()
    }

    inner class ReelViewHolder(private val binding: ItemReelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reel: Reel) {
            binding.textViewUsername.text = reel.username
            binding.textViewContent.text = reel.content
            binding.textViewLikes.text = "${reel.likes} likes"
            binding.textViewComments.text = "${reel.comments} comments"

            // Load reel thumbnail
            if (!reel.reelUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(reel.reelUrl)
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon)
                    .into(binding.imageViewReel)
            } else {
                binding.imageViewReel.setImageResource(R.drawable.icon)
            }

            // Set click listener to play the reel
            itemView.setOnClickListener {
                val intent = Intent(context, ReelPlaybackActivity::class.java)
                intent.putExtra("REEL_URL", reel.reelUrl)
                context.startActivity(intent)
            }
        }
    }
}