package com.example.retrofitdemo

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitdemo.databinding.ItemUserBinding

class UserAdapter : ListAdapter<User, UserAdapter.UserViewHolder>(UserDiffCallback()) {

    private val avatarColors = listOf(
        0xFF6C63FF.toInt(), 0xFF43A047.toInt(), 0xFFE53935.toInt(),
        0xFF1E88E5.toInt(), 0xFFFF8F00.toInt(), 0xFF8E24AA.toInt(),
        0xFF00897B.toInt(), 0xFFD81B60.toInt(), 0xFF3949AB.toInt(),
        0xFF00ACC1.toInt()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {

                val initials = user.name
                    .split(" ")
                    .take(2)
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .joinToString("")
                tvAvatar.text = initials

                val color = avatarColors[(user.id - 1) % avatarColors.size]
                val circle = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(color)
                }
                tvAvatar.background = circle

                tvName.text = user.name
                tvUsername.text = "@${user.username}"
                tvEmail.text = user.email
                tvPhone.text = user.phone
                tvCompany.text = user.company.name
                tvCity.text = "${user.address.city}, ${user.address.zipcode}"
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}