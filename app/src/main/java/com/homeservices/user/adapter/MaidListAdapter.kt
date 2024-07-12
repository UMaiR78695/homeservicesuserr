package com.homeservices.user.adapter

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
import com.homeservices.user.model.Maid


class MaidListAdapter: RecyclerView.Adapter<MaidListAdapter.ShowMaidListViewHolder>() {
    companion object{
        const val TAG = "ShowAddMaidListAdapterInfo"
    }
    private var database = Firebase.database.reference
    private val differCallback = object : DiffUtil.ItemCallback<Maid>() {
        override fun areItemsTheSame(oldItem: Maid, newItem: Maid): Boolean {
            return oldItem.maidName == newItem.maidName
        }

        override fun areContentsTheSame(oldItem: Maid, newItem: Maid): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowMaidListViewHolder {
        return ShowMaidListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.maid_name_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ShowMaidListViewHolder, position: Int) {
        var maid = differ.currentList[position]
        holder.maidName.text = maid.maidName
        holder.maidAge.text = "Age: ${maid.age}"
        holder.maidSalaryPkg.text = "RS ${maid.priceRange}"
        holder.locationNameTextView.text = maid.area
        holder.availabilityTimeTextView.text = maid.availability
        holder.experienceYearTextView.text = maid.experience
        holder.maidNameTextViewExpend.text = maid.maidName
        holder.maidAgeTextViewExpend.text = "Age: ${maid.age}"
        holder.maidSalaryPackageTextViewExpend.text  = "RS ${maid.priceRange}"

        //iterate list and separate with comma
        val builder = StringBuilder()
        for (services in maid.services) {
            builder.append(services)
            builder.append(",")
        }

        //remove last coma from list of string
        var finalServiceList = builder.substring(0,builder.length-1)
        holder.serviceNameTextView.text = finalServiceList

        holder.itemView.setOnClickListener{
            if (maid.isExpanded) {
                collapse(holder,maid)
            } else {
                expand(holder,maid)
            }
        }

        holder.hireMeButton.isVisible = !maid.active
        holder.hireMeButton.setOnClickListener{
            onItemClickListener?.let {
                it(maid)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ShowMaidListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var maidName: TextView = itemView.findViewById(R.id.maidNameTextView)
        var maidAge: TextView = itemView.findViewById(R.id.maidAgeTextView)
        var maidSalaryPkg: TextView = itemView.findViewById(R.id.maidSalaryPackageTextView)
        var maidCollapseConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.maidCollapseConstraintLayout)
        var maidExpendConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.maidExpendConstraintLayout)
        var maidNameTextViewExpend: TextView = itemView.findViewById(R.id.maidNameTextViewExpend)
        var maidAgeTextViewExpend: TextView = itemView.findViewById(R.id.maidAgeTextViewExpend)
        var maidSalaryPackageTextViewExpend: TextView = itemView.findViewById(R.id.maidSalaryPackageTextViewExpend)
        var locationNameTextView: TextView = itemView.findViewById(R.id.locationNameTextView)
        var availabilityTimeTextView: TextView = itemView.findViewById(R.id.availabilityTimeTextView)
        var serviceNameTextView: TextView = itemView.findViewById(R.id.serviceNameTextView)
        var experienceYearTextView: TextView = itemView.findViewById(R.id.experienceYearTextView)
        var hireMeButton: Button = itemView.findViewById(R.id.hireMeButton)
    }

    private fun expand(holder:ShowMaidListViewHolder,maidList:Maid) {
        holder.maidCollapseConstraintLayout.visibility = View.GONE
        holder.maidExpendConstraintLayout.visibility = View.VISIBLE
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val size = (interpolatedTime * 100).toInt()
                holder.maidCollapseConstraintLayout.layoutParams.height = size
                holder.maidCollapseConstraintLayout.layoutParams.height = size
            }
        }
        animation.duration = 200
        animation.start()
        maidList.isExpanded = true
    }

    private fun collapse(holder:ShowMaidListViewHolder,maidList:Maid) {
        // Set the visibility of the views that make up the item to GONE.
        holder.maidCollapseConstraintLayout.visibility = View.VISIBLE
        holder.maidExpendConstraintLayout.visibility = View.GONE

        // Animate the collapse of the item.
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                // Update the size of the item as it is being animated.
                val size = (1 - interpolatedTime) * 100
                holder.maidExpendConstraintLayout.layoutParams.height = size.toInt()
                holder.maidExpendConstraintLayout.layoutParams.height = size.toInt()
            }
        }
        animation.duration = 200
        animation.start()

        // Update the data model to reflect the collapsed state of the item.
        maidList.isExpanded = false
    }

    private var onItemClickListener: ((Maid) -> Unit)? = null
    fun setOnItemClickListener(listener: (Maid) -> Unit) {
        onItemClickListener = listener
    }

}