package com.example.sentiancesdksample_app_android

import SdkStatusUpdateHandler
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationCompat
import com.sentiance.sdk.*
import com.sentiance.sdk.OnInitCallback.InitIssue

class MainActivity : AppCompatActivity(), OnInitCallback, OnStartFinishedHandler {

    private val SENTIANCE_APP_ID = "61def70da962ed090000000f"
    private val SENTIANCE_SECRET =
        "896b844f9ea995181033aaa4fd13d44fdf828d262066ef96cb5d1b05756121325d718c6896a3fe2a9754006dd4c9f233997a7f976a211d1307fe16cd70ced92b"

    private val TAG = "SDKStarter"

    private val channelId = "trips"
    private val notificationName = "Trips"

    private val PREPROD_URL = "https://preprod-api.sentiance.com"

    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var initWithoutUserLinkingView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    private fun handleInitWithUserLinkClick() {
        startNextActivity()
    }

    private fun handleInitWithouUserLinkingClick() {
        initializeSentianceSdk()
        startNextActivity()
    }

    private fun initializeSentianceSdk() {
        // Create the config.
        val config = SdkConfig.Builder(
            SENTIANCE_APP_ID, SENTIANCE_SECRET, createNotification(channelId, notificationName)
        ).baseURL(PREPROD_URL)
            .setOnSdkStatusUpdateHandler(SdkStatusUpdateHandler(applicationContext))
            .build()

        // Initialize the Sentiance SDK.
        Sentiance.getInstance(this).init(config, this)
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

    private fun setupView() {
        initWithUserLinkingView = findViewById(R.id.cta_with_user_linking)
        initWithUserLinkingView.findViewById<TextView>(R.id.cta_textview).text =
            getString(R.string.initialise_SDK_with_user_linking)

        initWithoutUserLinkingView = findViewById(R.id.cta_without_user_linking)
        initWithoutUserLinkingView.findViewById<TextView>(R.id.cta_textview).text =
            getString(R.string.initialise_SDK_without_user_linking)

        initWithoutUserLinkingView.findViewById<AppCompatTextView>(R.id.cta_button)
            .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_not_linking, 0, 0)

        initWithUserLinkingView.setOnClickListener {
            handleInitWithUserLinkClick()
        }

        initWithoutUserLinkingView.setOnClickListener {
            handleInitWithouUserLinkingClick()
        }

    }

    private fun startNextActivity() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }

    override fun onInitSuccess() {
        printInitSuccessLogStatements()

        // Sentiance SDK was successfully initialized, we can now start it.
        Sentiance.getInstance(this).start(this)
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


    override fun onInitFailure(initIssue: InitIssue, throwable: Throwable?) {
        Log.e(TAG, "Could not initialize SDK: $initIssue")
        when (initIssue) {
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

}