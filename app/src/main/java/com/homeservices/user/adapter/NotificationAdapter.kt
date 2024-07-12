package com.homeservices.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.homeservices.user.R
import com.homeservices.user.model.Notification

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.notification_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var notification = differ.currentList[position]
        holder.notificationTitleTextView.text = notification.message
        holder.notificationDateTextView.text = notification.date
        if (notification.appointed) {
            holder.notificationImageView.setBackgroundResource(R.drawable.ic_notification_appointed)
        } else {
            holder.notificationImageView.setBackgroundResource(R.drawable.ic_notification_cancelled)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationImageView: ImageView = itemView.findViewById(R.id.notificationImageView)
        val notificationTitleTextView: TextView = itemView.findViewById(R.id.notificationTitleTextView)
        val notificationDateTextView: TextView = itemView.findViewById(R.id.notificationDateTextView)
    }
}