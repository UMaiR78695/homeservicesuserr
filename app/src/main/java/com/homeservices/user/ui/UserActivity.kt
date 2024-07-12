package com.homeservices.user.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.homeservices.user.R
import com.homeservices.user.databinding.ActivityUserBinding
import com.homeservices.user.extensions.loadFragment
import com.homeservices.user.extensions.navigateToNextActivity
import com.homeservices.user.ui.fragments.user.LoginFragment

class UserActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserBinding
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Firebase.auth.currentUser != null) {
            navigateToNextActivity(MainActivity::class.java)
            finish()
        } else loadFragment(supportFragmentManager, LoginFragment.newInstance(), false)
    }

}