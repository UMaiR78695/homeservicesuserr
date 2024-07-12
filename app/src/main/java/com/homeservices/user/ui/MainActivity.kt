package com.homeservices.user.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.homeservices.user.R
import com.homeservices.user.databinding.ActivityMainBinding
import com.homeservices.user.extensions.navigateToNextActivity
import com.homeservices.user.util.UserPreferences


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var toggle: ActionBarDrawerToggle
    lateinit var navController: NavController
    lateinit var navHostFragment: NavHostFragment
    private lateinit var userPreferences: UserPreferences
    companion object {
        const val TAG = "MainActivityInfoLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userPreferences = UserPreferences(this)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        initializeDrawer()
        clickListenerButton()
        if (navController.currentDestination?.label == Fragments.HOME.label) {
            binding.logoutButtonImageview.setBackgroundResource(R.drawable.baseline_logout_24)
            binding.logoutButtonImageview.scaleX = (-1).toFloat()
        }
        onBackPress()
    }

    private fun clickListenerButton() {
        binding.drawerButtonImageview.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.logoutButtonImageview.setOnClickListener {
            when(navController.previousBackStackEntry?.destination?.label){
                Fragments.HOME.label -> {
                    handleLogoutClick(resources.getString(R.string.home))
                    binding.logoutButtonImageview.setBackgroundResource(R.drawable.baseline_logout_24)
                }
                Fragments.NOTIFICATION.label -> handleLogoutClick(resources.getString(R.string.notifications))
                Fragments.WORKING.label -> handleLogoutClick(resources.getString(R.string.working))
                Fragments.CURRENT_MAID.label -> handleLogoutClick(resources.getString(R.string.currentMaid))
                Fragments.ABOUT_US.label -> handleLogoutClick(resources.getString(R.string.aboutUs))
                else -> {
                    showLogoutDialog()
                }
            }
        }
    }

    private fun initializeDrawer() {
        binding.apply {
            toggle = ActionBarDrawerToggle(
                this@MainActivity,
                drawerLayout,
                R.string.open,
                R.string.close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.item_notifications -> {
                        when (navController.currentDestination?.label) {
                            Fragments.HOME.label -> changeHeaders(resources.getString(R.string.notifications), R.id.action_homeScreenFragment_to_notificationFragment)
                            Fragments.NOTIFICATION.label -> binding.drawerLayout.closeDrawer(GravityCompat.START)
                            Fragments.WORKING.label -> changeHeaders(resources.getString(R.string.notifications), R.id.action_workingFragment_to_notificationFragment)
                            Fragments.CURRENT_MAID.label -> changeHeaders(resources.getString(R.string.notifications), R.id.action_currentMaidFragment_to_notificationFragment)
                            Fragments.ABOUT_US.label -> changeHeaders(resources.getString(R.string.notifications), R.id.action_aboutUsFragment_to_notificationFragment)
                        }
                    }
                    R.id.item_how_works -> {
                        when (navController.currentDestination?.label) {
                            Fragments.HOME.label -> changeHeaders(resources.getString(R.string.working), R.id.action_homeScreenFragment_to_workingFragment)
                            Fragments.NOTIFICATION.label -> changeHeaders(resources.getString(R.string.working), R.id.action_notificationFragment_to_workingFragment)
                            Fragments.WORKING.label -> binding.drawerLayout.closeDrawer(GravityCompat.START)
                            Fragments.CURRENT_MAID.label -> changeHeaders(resources.getString(R.string.working), R.id.action_currentMaidFragment_to_workingFragment)
                            Fragments.ABOUT_US.label -> changeHeaders(resources.getString(R.string.working), R.id.action_aboutUsFragment_to_workingFragment) }
                    }
                    R.id.item_current_maid -> {
                        when (navController.currentDestination?.label) {
                            Fragments.HOME.label ->  changeHeaders(resources.getString(R.string.currentMaid), R.id.action_homeScreenFragment_to_currentMaidFragment)
                            Fragments.NOTIFICATION.label -> changeHeaders(resources.getString(R.string.currentMaid), R.id.action_notificationFragment_to_currentMaidFragment)
                            Fragments.WORKING.label -> changeHeaders(resources.getString(R.string.currentMaid), R.id.action_workingFragment_to_currentMaidFragment)
                            Fragments.CURRENT_MAID.label -> binding.drawerLayout.closeDrawer(GravityCompat.START)
                            Fragments.ABOUT_US.label -> changeHeaders(resources.getString(R.string.currentMaid), R.id.action_aboutUsFragment_to_currentMaidFragment)
                        }
                    }
                    R.id.item_about -> {
                        when (navController.currentDestination?.label) {
                            Fragments.HOME.label -> changeHeaders(resources.getString(R.string.aboutUs), R.id.action_homeScreenFragment_to_aboutUsFragment)
                            Fragments.NOTIFICATION.label -> changeHeaders(resources.getString(R.string.aboutUs), R.id.action_notificationFragment_to_aboutUsFragment)
                            Fragments.WORKING.label -> changeHeaders(resources.getString(R.string.aboutUs), R.id.action_workingFragment_to_aboutUsFragment)
                            Fragments.CURRENT_MAID.label -> changeHeaders(resources.getString(R.string.aboutUs), R.id.action_currentMaidFragment_to_aboutUsFragment)
                            Fragments.ABOUT_US.label -> binding.drawerLayout.closeDrawer(GravityCompat.START)

                        }
                    }
                    R.id.item_cancel_booking -> {
                        when (navController.currentDestination?.label) {
                            Fragments.HOME.label ->  changeHeaders(resources.getString(R.string.currentMaid), R.id.action_homeScreenFragment_to_currentMaidFragment)
                            Fragments.NOTIFICATION.label -> changeHeaders(resources.getString(R.string.currentMaid), R.id.action_notificationFragment_to_currentMaidFragment)
                            Fragments.WORKING.label -> changeHeaders(resources.getString(R.string.currentMaid), R.id.action_workingFragment_to_currentMaidFragment)
                            Fragments.CURRENT_MAID.label -> binding.drawerLayout.closeDrawer(GravityCompat.START)
                            Fragments.ABOUT_US.label -> changeHeaders(resources.getString(R.string.currentMaid), R.id.action_aboutUsFragment_to_currentMaidFragment)
                        }
                    }
                    R.id.item_invite_friend -> {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                            val shareMessage = "\n${userPreferences.getUser().name} recommend you this application\n https://play.google.com/store/apps/details?id=$packageName"
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "choose one"))
                        } catch (e: Exception) {
                            Log.i(TAG, "initializeDrawer: ")
                        }
                    }
                    R.id.item_help -> {
                        openDialer("1122")
                    }
                }
                true
            }

            val headerView: View = binding.navView.getHeaderView(0)
            val icCrossDrawer: ImageView = headerView.findViewById(R.id.iv_nav_header_cross)
            icCrossDrawer.setOnClickListener{
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun handleLogoutClick(text:String){
        navController.popBackStack()
        binding.homeTextView.text = text
    }

    private fun changeHeaders(headingText: String, navigationAction: Int){
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        binding.logoutButtonImageview.setBackgroundResource(R.drawable.baseline_arrow_forward_ios_24)
        binding.homeTextView.text = headingText
        navController.navigate(navigationAction)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    override fun onSupportNavigateUp(): Boolean {
        binding.drawerLayout.openDrawer(binding.navView)
        return true
    }

    private fun onBackPress(){
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    when(navController.previousBackStackEntry?.destination?.label){
                        Fragments.HOME.label -> {
                            handleLogoutClick(resources.getString(R.string.home))
                            binding.logoutButtonImageview.setBackgroundResource(R.drawable.baseline_logout_24)
                        }
                        Fragments.NOTIFICATION.label -> handleLogoutClick(resources.getString(R.string.notifications))
                        Fragments.WORKING.label -> handleLogoutClick(resources.getString(R.string.working))
                        Fragments.CURRENT_MAID.label -> handleLogoutClick(resources.getString(R.string.currentMaid))
                        Fragments.ABOUT_US.label -> handleLogoutClick(resources.getString(R.string.aboutUs))
                        else -> finish()
                    }
                }
            }
        })
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(this,R.style.RoundedCornersDialog)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Logout") { _, _ ->
            userPreferences.logout()
            Firebase.auth.signOut()
            navigateToNextActivity(UserActivity::class.java)
            finish()
        }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        dialog.show()
    }

    private fun openDialer(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

}
private enum class Fragments(val label: String) {
    HOME("fragment_home_screen"),NOTIFICATION("fragment_notification"),WORKING("fragment_working"),CURRENT_MAID("fragment_current_maid"),ABOUT_US("fragment_about_us")
}