package com.homeservices.user.model

data class Maid(
    val maidId: String = "",
    val maidName: String = "",
    val cnic: String = "",
    val phone: String = "",
    val age: String = "",
    val priceRange: String = "",
    val experience: String = "",
    val area: String = "",
    val availability: String = "",
    val services: List<String> = emptyList(),
    val active: Boolean = false // Indicates whether maid is assigned to a customer or not
){
    var isExpanded = false
}