package com.sentiance.sdksampleapp

import android.app.*
import android.util.Log
import com.sentiance.sdk.*
import com.sentiance.sdk.init.SentianceOptions
import com.sentiance.sdksampleapp.helpers.NotificationHelper

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeSentianceSdk()
    }

    private fun initializeSentianceSdk() {
        val sentiance = Sentiance.getInstance(this)
        val initOptions = SentianceOptions.Builder(this)
            .setNotification(NotificationHelper.createNotification(this), NOTIFICATION_ID)
            .build()

        val initResult = sentiance.initialize(initOptions)
        if (!initResult.isSuccessful) {
            Log.e(
                TAG,
                "Initialization failed, failure reason: ${initResult.failureReason} exception: ${initResult.throwable}"
            )
        }
    }

    companion object {
        private const val TAG = "MainApplication"
        private const val NOTIFICATION_ID = 100
    }
}