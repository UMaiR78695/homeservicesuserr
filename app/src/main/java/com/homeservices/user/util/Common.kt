package com.homeservices.user.util

import android.content.Context
import io.github.rupinderjeet.kprogresshud.KProgressHUD

class Common {
    companion object{
        fun getProgressBar(context: Context, message: String = ""): KProgressHUD = KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(1)
            .setDimAmount(0.5f)
            .setDetailsLabel(message)
    }

}