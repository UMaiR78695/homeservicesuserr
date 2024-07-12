package com.homeservices.user.model

data class Notification(
    val id: String= "",
    val title: String = "",
    val message: String = "",
    val appointed: Boolean = false,
    val date: String = "",
    val toUser: String = "",
    val maid: Maid? = null
)