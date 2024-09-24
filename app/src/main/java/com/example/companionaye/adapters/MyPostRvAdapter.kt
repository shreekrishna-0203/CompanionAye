package com.example.companionaye.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.example.companionaye.databinding.ItemPostBinding
import com.example.companionaye.models.Post

class MyPostRvAdapter(private val context: Context, private var posts: MutableList<Post>) : RecyclerView.Adapter<MyPostRvAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.textViewUsername.text = post.username
            binding.textViewContent.text = post.content
            binding.textViewLikes.text = "${post.likes} likes"
            binding.textViewComments.text = "${post.comments} comments"

            if (!post.postUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(post.postUrl)
                    .fit()
                    .centerCrop()
                    .into(binding.imageViewPost)
            } else {
                binding.imageViewPost.setImageDrawable(null)
            }
        }
    }
}
