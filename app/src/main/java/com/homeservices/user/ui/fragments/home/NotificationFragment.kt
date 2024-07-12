package com.homeservices.user.ui.fragments.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.homeservices.user.adapter.NotificationAdapter
import com.homeservices.user.databinding.FragmentNotificationBinding
import com.homeservices.user.model.MaidRequest
import com.homeservices.user.model.Notification
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationAdapter = NotificationAdapter()
        binding.notificationListRecycleView.layoutManager = LinearLayoutManager(requireActivity())
        binding.notificationListRecycleView.adapter = notificationAdapter
        fetchNotifications()
    }

    private fun fetchNotifications() {
        Firebase.database.reference.child("notifications")
            .orderByChild("toUser").equalTo(Firebase.auth.currentUser?.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val notificationsList = ArrayList<Notification>()
                    for (notificationSnapshot in snapshot.children) {
                        val notification = notificationSnapshot.getValue(Notification::class.java)
                        if (notification != null) notificationsList.add(notification)
                    }
                    notificationAdapter.differ.submitList(notificationsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}