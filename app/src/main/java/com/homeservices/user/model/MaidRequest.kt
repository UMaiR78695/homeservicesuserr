package com.homeservices.user.model

data class MaidRequest(
    val user: User? = null,
    val maid: Maid? = null,
    val requestId: String = "",
    val actionTaken: Boolean = false,
    val requestStatus: String = Request.PENDING.value,
    val jobActive: Boolean = false
)

enum class Request(val value: String){
    PENDING("pending"), APPROVED("approved"), DECLINED("declined")
}