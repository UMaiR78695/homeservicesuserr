package com.homeservices.user.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.homeservices.user.model.User


class UserPreferences(private val context: Context) {
    companion object{
        const val UserID: String = "userid"
        const val UserEmail: String = "userEmail"
        const val UserName: String = "userName"
        const val UserPhone: String = "userPhone"
        const val UserCnic: String = "userCnic"
        const val UserPrefName: String = "Loginuser"
    }

    private val sharedPreferences: SharedPreferences  = context.getSharedPreferences(UserPrefName, Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        sharedPreferences.edit {
            putString(UserID, user.userId).apply()
            putString(UserEmail, user.email).apply()
            putString(UserName, user.name).apply()
            putString(UserPhone, user.phone).apply()
            putString(UserCnic, user.cnic).apply()
        }
    }

    fun getUser() = User(
        sharedPreferences.getString(UserID, "").orEmpty(),
        sharedPreferences.getString(UserEmail, "").orEmpty(),
        sharedPreferences.getString(UserName, "").orEmpty(),
        sharedPreferences.getString(UserPhone, "").orEmpty(),
        sharedPreferences.getString(UserCnic, "").orEmpty(),
    )

    fun logout() = sharedPreferences.edit().clear()
}