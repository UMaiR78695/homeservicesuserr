package com.homeservices.user.ui.fragments.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.homeservices.user.databinding.FragmentAboutUsBinding


class AboutUsFragment : Fragment() {
    private var _binding: FragmentAboutUsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return binding.root
    }

}