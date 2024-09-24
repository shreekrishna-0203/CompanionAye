package com.example.companionaye.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.companionaye.R
import com.example.companionaye.databinding.ItemReelBinding
import com.example.companionaye.models.Reel
import com.example.companionaye.VideoPlayerActivity
import com.squareup.picasso.Picasso

class ReelsAdapter(private val context: Context, private var reels: MutableList<Reel>) : RecyclerView.Adapter<ReelsAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(private val binding: ItemReelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reel: Reel) {
            // Bind reel data here
            binding.textViewUsername.text = reel.username
            binding.textViewContent.text = reel.content
            binding.textViewLikes.text = "${reel.likes} likes"
            binding.textViewComments.text = "${reel.comments} comments"

            // Load reel thumbnail if necessary
            if (!reel.reelUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(reel.reelUrl) // Assuming videoUrl is used for thumbnail
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon)
                    .into(binding.imageViewReel)

                // Handle click event
                binding.root.setOnClickListener {
                    val intent = Intent(context, VideoPlayerActivity::class.java)
                    intent.putExtra("VIDEO_URL", reel.reelUrl)
                    context.startActivity(intent)
                }
            } else {
                // Hide or remove the view if no valid URL is present
                binding.root.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReelViewHolder {
        val binding = ItemReelBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReelViewHolder, position: Int) {
        holder.bind(reels[position])
    }

    override fun getItemCount(): Int = reels.size

    fun updateReels(newReels: List<Reel>) {
        reels.clear()
        reels.addAll(newReels.filter { !it.reelUrl.isNullOrEmpty() })
        notifyDataSetChanged()
    }
}

