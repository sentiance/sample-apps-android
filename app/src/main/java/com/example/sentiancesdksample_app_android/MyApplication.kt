package com.example.sentiancesdksample_app_android

import SdkStatusUpdateHandler
import android.app.*
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sentiance.sdk.*
import com.sentiance.sdk.OnInitCallback.InitIssue

class MyApplication : Application(), OnInitCallback, OnStartFinishedHandler {

    private val TAG = "SDKStarter"
    private val PREPROD_URL = "https://preprod-api.sentiance.com/"

    private val channelId = "trips"
    private val notificationName = "Trips"

    fun initializeSentianceSdk() {
        if (Sentiance.getInstance(this).initState !== InitState.INITIALIZED) {
            // Create the config.
            val config = SdkConfig.Builder(
                BuildConfig.SENTIANCE_APP_ID,
                BuildConfig.SENTIANCE_SECRET,
                createNotification(channelId, notificationName)
            ).baseURL(PREPROD_URL)
                .setOnSdkStatusUpdateHandler(SdkStatusUpdateHandler(applicationContext))
                .build()

            // Initialize the Sentiance SDK.
            Sentiance.getInstance(this).init(config, this)
        }
    }

    private fun createNotification(channelId: String, notificationName: String): Notification? {
        // PendingIntent that will start your application's MainActivity
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // On Oreo and above, you must create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, notificationName, NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name) + " is running")
            .setContentText("Touch to open.")
            .setContentIntent(pendingIntent)
            .setShowWhen(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    private fun printInitSuccessLogStatements() {
        Log.i(TAG, "Sentiance SDK initialized, version: " + Sentiance.getInstance(this).version)
        Log.i(
            TAG,
            "Sentiance platform user id for this install: " + Sentiance.getInstance(this).userId
        )
        Sentiance.getInstance(this).getUserAccessToken(object : TokenResultCallback {
            override fun onSuccess(token: Token) {
                Log.i(TAG, "Access token to query the HTTP API: Bearer " + token.tokenId)
                // Using this token, you can query the Sentiance API.
            }

            override fun onFailure() {
                Log.e(TAG, "Couldn't get access token")
            }
        })
    }

    override fun onInitSuccess() {
        printInitSuccessLogStatements()
        //  Sentiance SDK was successfully initialized, we can now start it.

        Sentiance.getInstance(this).start {
            startNewActivity()
        }
    }

    override fun onInitFailure(issue: InitIssue, throwable: Throwable?) {
        Log.e(TAG, "Could not initialize SDK: $issue")

        when (issue) {
            InitIssue.INVALID_CREDENTIALS -> Log.e(
                TAG,
                "Make sure SENTIANCE_APP_ID and SENTIANCE_SECRET are set correctly."
            )
            InitIssue.CHANGED_CREDENTIALS -> Log.e(
                TAG,
                "The app ID and secret have changed; this is not supported. If you meant to change the app credentials, please uninstall the app and try again."
            )
            InitIssue.SERVICE_UNREACHABLE -> Log.e(
                TAG,
                "The Sentiance API could not be reached. Double-check your internet connection and try again."
            )
            InitIssue.LINK_FAILED -> Log.e(
                TAG,
                "An issue was encountered trying to link the installation ID to the metauser."
            )
            InitIssue.INITIALIZATION_ERROR -> Log.e(
                TAG,
                "An unexpected exception or an error occurred during initialization.",
                throwable
            )
            InitIssue.SDK_RESET_IN_PROGRESS -> Log.e(
                TAG,
                "SDK reset operation is in progress. Wait until it's complete.",
                throwable
            )
        }
    }

    override fun onStartFinished(sdkStatus: SdkStatus?) {
        Log.i(
            TAG, "SDK start finished with status: " + sdkStatus!!.startStatus
        )
    }

    private fun startNewActivity() {
        val intent = Intent(this, Dashboard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }
}