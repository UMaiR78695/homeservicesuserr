package com.homeservices.user.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.homeservices.user.adapter.PastMaidListAdapter
import com.homeservices.user.databinding.FragmentCurrentMaidBinding
import com.homeservices.user.extensions.showToast
import com.homeservices.user.model.Maid
import com.homeservices.user.model.MaidRequest
import com.homeservices.user.model.Notification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


class CurrentMaidFragment : Fragment() {
    private var _binding: FragmentCurrentMaidBinding? = null
    private val binding get() = _binding!!
    private var database = Firebase.database.reference
    private lateinit var pastMaidsAdapter: PastMaidListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCurrentMaidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pastMaidsAdapter = PastMaidListAdapter()
        binding.pastMaidListRecycleView.layoutManager = LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL, false)
        binding.pastMaidListRecycleView.adapter = pastMaidsAdapter

        readCurrentMaidFromDatabase()
        fetchPastMaids()

        binding.cancelEmploymentButton.setOnClickListener {
            database.child("requests").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var terminateLoop = false
                    for (postSnapshot in dataSnapshot.children) {
                        var request = postSnapshot.getValue(MaidRequest::class.java)
                        request?.let {
                            if (it.user?.userId == Firebase.auth.currentUser?.uid && it.jobActive && it.actionTaken) {
                                cancelMaidEmployment(request)
                                terminateLoop = true
                            }
                        }
                        if (terminateLoop) break
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    private fun fetchPastMaids() {
        database.child("requests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var maidList : ArrayList<Maid> = ArrayList()
                for (postSnapshot in dataSnapshot.children) {
                    var request = postSnapshot.getValue(MaidRequest::class.java)
                    request?.let {
                        if (it.user?.userId == Firebase.auth.currentUser?.uid && !it.jobActive && it.actionTaken) {
                            it.maid?.let { maid -> maidList.add(maid) }
                        }
                    }
                }
                pastMaidsAdapter.differ.submitList(maidList)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun cancelMaidEmployment(request: MaidRequest) {
        database.child("requests").child(request.requestId).child("jobActive").setValue(false)
            .addOnSuccessListener {
                database.child("maids").child(request.maid!!.maidId).child("active")
                    .setValue(false).addOnSuccessListener {
                        generateNotification(request)
                    }
            }
    }

    private fun readCurrentMaidFromDatabase() {
        database.child("requests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var terminateLoop = false
                for (postSnapshot in dataSnapshot.children) {
                    var request = postSnapshot.getValue(MaidRequest::class.java)
                    request?.let {
                        if (it.user?.userId == Firebase.auth.currentUser?.uid && it.jobActive) {
                            it.maid?.let { maid -> setCurrentMaidData(maid) }
                            terminateLoop = true
                        }
                    }
                    if (terminateLoop) break
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setCurrentMaidData(currentMaidData: Maid){
        binding.currentMaidLayout.isVisible = true
        binding.currentMaidNameTextView.text = currentMaidData.maidName + " is your current maid."
        binding.maidNameTextView.text = currentMaidData.maidName
        binding.maidAgeTextView.text = "Age: ${currentMaidData.age}"
        binding.maidSalaryPackageTextView.text = "RS ${currentMaidData.priceRange}"
        binding.locationNameTextView.text = currentMaidData.area
        binding.availabilityTimeTextView.text = currentMaidData.availability
        binding.experienceYearTextView.text = currentMaidData.experience

        //iterate list and separate with comma
        val builder = StringBuilder()
        for (services in currentMaidData.services) {
            builder.append(services)
            builder.append(",")
        }

        //remove last coma from list of string
        var finalServiceList = builder.substring(0,builder.length-1)
        binding.serviceNameTextView.text = finalServiceList
    }

    private fun generateNotification(request: MaidRequest) {
        val notification = Notification(
            id = System.currentTimeMillis().toString(),
            title = "Maid Cancelled",
            message = "${request.maid!!.maidName}'s employment is cancelled",
            appointed = false,
            toUser = request.user!!.userId,
            maid = request.maid,
            date = getTimeStamp()
        )
        Firebase.database.reference.child("notifications")
            .child(notification.id)
            .setValue(notification).addOnSuccessListener {
                requireActivity().showToast("Employment Cancel Successfully!")
                binding.currentMaidLayout.visibility = View.INVISIBLE
            }
    }

    private fun getTimeStamp(): String {
        val sfd = SimpleDateFormat("dd MMM, yyyy hh:mm a")
        val pakistan = TimeZone.getTimeZone("Asia/Karachi")
        sfd.timeZone = pakistan
        return sfd.format(Date()).orEmpty()
    }
}