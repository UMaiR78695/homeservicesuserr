package com.homeservices.user.ui.fragments.user

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.homeservices.user.R
import com.homeservices.user.databinding.FragmentSignUpBinding
import com.homeservices.user.extensions.navigateToNextActivity
import com.homeservices.user.extensions.showToast
import com.homeservices.user.model.User
import com.homeservices.user.ui.MainActivity
import com.homeservices.user.util.Common
import com.homeservices.user.util.UserPreferences
import io.github.rupinderjeet.kprogresshud.KProgressHUD


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: KProgressHUD
    private lateinit var userPreferences: UserPreferences


    private var email: String = ""
    private var name: String = ""
    private var phone: String = ""
    private var cnic: String = ""
    private var password: String = ""
    private var confirmPassword: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = Common.getProgressBar(requireContext(),"Registering user..")
        userPreferences = UserPreferences(requireContext())
        //button Click Listener
        buttonClickListener()
    }

    private fun validateInput(): Boolean {
        return if (email.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterUserNameEmail))
            false
        } else if (name.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterFullName))
            false
        } else if (phone.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterPhone))
            false
        } else if (phone.length > 11) {
            requireActivity().showToast(getString(R.string.phone_num_not_valid))
            false
        } else if (cnic.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterCNIC))
            false
        } else if (password.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterPassword))
            false
        } else if (confirmPassword.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterConfirmPassword))
            false
        } else if (password != confirmPassword) {
            requireActivity().showToast(getString(R.string.password_not_match))
            false
        } else {
            true
        }
    }

    private fun buttonClickListener() {
        binding.signInTextview.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.signUpButton.setOnClickListener {
            getInput()
            if (validateInput()) {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        /** Step 1: Authenticate **/
        progressBar.show()
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = User(Firebase.auth.currentUser?.uid ?: System.currentTimeMillis().toString(), email, name, phone, cnic)
                    /** Step 2: Save info to Realtime db and Shared Preference**/
                    Firebase.database.reference.child("Users").child(user.userId).setValue(user).addOnSuccessListener {
                        saveUserInfo(user)
                        requireActivity().navigateToNextActivity(MainActivity::class.java)
                        requireActivity().finish()
                    }.addOnFailureListener {
                        requireActivity().showToast(it.message.toString())
                        progressBar.dismiss()
                    }
                } else{
                    requireActivity().showToast(task.exception?.message.toString())
                    progressBar.dismiss()
                }
            }
    }

    private fun saveUserInfo(user:User) {
        userPreferences.saveUser(user)
        progressBar.dismiss()
    }

    private fun getInput() {
        email = binding.usernameEmailEditText.text.toString().trim()
        name = binding.fullNameEditText.text.toString().trim()
        phone = binding.phoneEditText.text.toString().trim()
        cnic = binding.cnicEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString()
        confirmPassword = binding.confirmPasswordEditText.text.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}