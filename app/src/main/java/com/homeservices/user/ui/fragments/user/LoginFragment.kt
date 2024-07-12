package com.homeservices.user.ui.fragments.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.homeservices.user.R
import com.homeservices.user.extensions.loadFragment
import com.homeservices.user.databinding.FragmentLoginBinding
import com.homeservices.user.extensions.navigateToNextActivity
import com.homeservices.user.extensions.showToast
import com.homeservices.user.model.User
import com.homeservices.user.ui.MainActivity
import com.homeservices.user.util.Common
import com.homeservices.user.util.UserPreferences
import io.github.rupinderjeet.kprogresshud.KProgressHUD

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: KProgressHUD
    private lateinit var userPreferences: UserPreferences
    private var email: String = ""
    private var password: String = ""
    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()

        const val TAG = "LoginFragmentInfo"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = Common.getProgressBar(requireContext(),"Logging in..")
        userPreferences = UserPreferences(requireContext())

        //button Click Listener
        clickListenerButton()
    }

    private fun clickListenerButton() {
        binding.signUpTextview.setOnClickListener {
            requireActivity().loadFragment(parentFragmentManager, SignUpFragment(), true)
        }
        binding.watchVideoTextview.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com/watch?v=Y85VFcozvDY")
            startActivity(intent)
        }
        binding.signInButton.setOnClickListener {
            getInput()
            if (validateInput()) {
                progressBar.show()
                Firebase.auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        saveUserInfo(Firebase.auth.currentUser?.uid!!)
                    }.addOnFailureListener { error ->
                        progressBar.dismiss()
                        requireActivity().showToast(error.message ?: "Error")
                    }
            }
        }

    }

    private fun saveUserInfo(userID: String) {
        Firebase.database.reference.child("Users").child(userID).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)
            userPreferences.saveUser(user!!)
            progressBar.dismiss()
            requireActivity().navigateToNextActivity(MainActivity::class.java)
            requireActivity().finish()
        }.addOnFailureListener {
            progressBar.dismiss()
        }
    }

    private fun validateInput(): Boolean {
        return if (email.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterUserNameEmail))
            false
        } else if (password.isEmpty()) {
            requireActivity().showToast(getString(R.string.enterPassword))
            false
        } else {
            true
        }
    }

    private fun getInput() {
        email = binding.usernameEmailEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}