package com.example.companionaye.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.companionaye.Models.User
import com.example.companionaye.databinding.ItemUserBinding
import com.squareup.picasso.Picasso

class UserAdapter(
    private val context: Context,
    private var users: MutableList<User>,
    private val onUserClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.textViewName.text = user.name


            // Load the profile picture using Picasso
            if (!user.image.isNullOrEmpty()) {
                Picasso.get()
                    .load(user.image)
                    .fit()
                    .centerCrop()
                    .into(binding.imageViewProfile) // Replace with the actual ImageView ID in your layout
            } else {
                binding.imageViewProfile.setImageDrawable(null)
            }

            binding.root.setOnClickListener { onUserClick(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateUsers(newUsers: MutableList<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }
}
