package com.homeservices.user.extensions

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.homeservices.user.R

fun Activity.loadFragment(fragmentManager: FragmentManager, fragment: Fragment, addToBackStack: Boolean) {
    val transaction = fragmentManager.beginTransaction()
        .replace(R.id.fragment_container, fragment)
    if (addToBackStack) transaction.addToBackStack(fragment.javaClass.simpleName)
    transaction.commit()
}
fun Activity.removeAllFragments(fragmentManager: FragmentManager) {
    fragmentManager.apply {
        for (fragment in fragments) {
            beginTransaction().remove(fragment).commit()
        }
        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
fun Activity.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
fun Activity.navigateToNextActivity(destination: Class<out Activity>) = startActivity(Intent(this, destination))
