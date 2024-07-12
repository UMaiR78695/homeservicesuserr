package com.homeservices.user.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.homeservices.user.R
import com.homeservices.user.databinding.PastMaidLayoutBinding
import com.homeservices.user.model.Maid


class PastMaidListAdapter: RecyclerView.Adapter<PastMaidListAdapter.PastMaidViewHolder>() {
    private val differCallback = object : DiffUtil.ItemCallback<Maid>() {
        override fun areItemsTheSame(oldItem: Maid, newItem: Maid): Boolean {
            return oldItem.maidName == newItem.maidName
        }

        override fun areContentsTheSame(oldItem: Maid, newItem: Maid): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastMaidViewHolder {
        val itemBinding = PastMaidLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PastMaidViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PastMaidViewHolder, position: Int) {
        val maid = differ.currentList[position]
        holder.bind(maid)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class PastMaidViewHolder(val binding: PastMaidLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(maid: Maid){
            binding.maidNameTextView.text = maid.maidName
            binding.maidAgeTextViewExpend.text = maid.age
        }
    }
}