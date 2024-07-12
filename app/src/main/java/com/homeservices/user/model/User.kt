package com.homeservices.user.model

data class User(
    val userId: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val cnic: String = "",
)