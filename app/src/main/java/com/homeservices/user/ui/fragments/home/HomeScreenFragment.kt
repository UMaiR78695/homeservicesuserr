package com.homeservices.user.ui.fragments.home

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.homeservices.user.adapter.MaidListAdapter
import com.homeservices.user.databinding.FragmentHomeScreenBinding
import com.homeservices.user.extensions.showToast
import com.homeservices.user.model.Maid
import com.homeservices.user.model.MaidRequest
import com.homeservices.user.util.UserPreferences


class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!
    private lateinit var maidListAdapter: MaidListAdapter
    private lateinit var database: DatabaseReference
    private lateinit var userPreferences: UserPreferences
    var maidList : ArrayList<Maid> = ArrayList()
    private var priceRange: String = ""
    private var area: String = ""
    var availability = ""
    private val services = mutableListOf<String>()
    var progressDialog: ProgressDialog? = null
    companion object{
        const val TAG = "HomeScreenFragmentsInfo"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences(requireContext())

        //read data from database
        binding.heyNameUserTextView.text = "Hey! ${userPreferences.getUser().name}"
        readMaidListFromDatabase()
        maidListAdapter = MaidListAdapter()
        binding.maidListRecycleView.layoutManager = LinearLayoutManager(requireActivity())
        binding.maidListRecycleView.adapter = maidListAdapter

        //listener on Button
        clickListenerButton()
    }

    private fun clickListenerButton() {
        maidListAdapter.setOnItemClickListener { maid ->
            val request = MaidRequest(userPreferences.getUser(), maid, System.currentTimeMillis().toString())
            Firebase.database.reference.child("requests").child(request.requestId).setValue(request)
                .addOnSuccessListener {
                    requireActivity().showToast("Maid request created")
                }
        }

        binding.filterButtonImageView.setOnClickListener{
            binding.filterDialogConstraintLayout.isVisible =  !binding.filterDialogConstraintLayout.isVisible
        }

        binding.availabilityChipGroup.setOnCheckedChangeListener { chipGroup, i ->
            val chip: Chip = chipGroup.findViewById(i)
            availability = chip.text.toString()
        }

        binding.applyFiltersButton.setOnClickListener{
            binding.serviceChipGroup.checkedChipIds.forEach { id ->
                val chip: Chip = binding.serviceChipGroup.findViewById(id)
                services.add(chip.text.toString())
            }
            priceRange = binding.enterPriceRangeEditText.text.toString().trim()
            area = binding.enterAreaEditText.text.toString().trim()
            applyFilter()
            Log.i(TAG, "clickListenerButton: $priceRange   $area    $availability")
        }

        binding.maidSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(query: String): Boolean {
                val filteredList =  maidList.filter { it.maidName.contains(query,ignoreCase = true) }
                maidListAdapter.differ.submitList(filteredList)
                Log.i(TAG, "onQueryTextChange: $filteredList")
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val filteredList =  maidList.filter { it.maidName.contains(query,ignoreCase = true) }
                maidListAdapter.differ.submitList(filteredList)
                Log.i(TAG, "onQueryTextChange: $filteredList")
                return false
            }
        })
    }

    private fun readMaidListFromDatabase() {
        loadingProgressDialogue()
        database = FirebaseDatabase.getInstance().getReference("maids")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                maidList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    var data = postSnapshot.getValue(Maid::class.java)
                    maidList.add(data!!)
                    Log.i(TAG, "onDataChange: $postSnapshot    ${postSnapshot.value}   //   ${data.maidName}")
                }
                maidListAdapter.differ.submitList(maidList)
                progressDialog?.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun applyFilter(){
        var filterList= maidList.filter {
            var includePerson = false

            if(!area.isNullOrEmpty()){
                if (it.area.contains(area,ignoreCase = true)) {
                    includePerson = true
                }
            }
            if(!priceRange.isNullOrEmpty()){
                if(it.priceRange.contains(priceRange)){
                    includePerson = true
                }
            }
            if(it.availability == availability){
                includePerson = true
            }
            for(service in services){
                if(it.services.contains(service)){
                    includePerson=true
                }
            }
            includePerson
        }
        Log.i(TAG, "applyFilter: ${filterList.size} //   $filterList")
        maidListAdapter.differ.submitList(filterList)
        binding.filterDialogConstraintLayout.visibility = View.GONE
    }

    private fun loadingProgressDialogue(){
        progressDialog = ProgressDialog(requireActivity())
        progressDialog?.setTitle("Please Wait")
        progressDialog?.setMessage("Loading...")
        progressDialog?.show()
    }
}