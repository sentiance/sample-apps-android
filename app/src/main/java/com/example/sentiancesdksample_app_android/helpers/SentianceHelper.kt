package com.example.sentiancesdksample_app_android.helpers

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.sentiancesdksample_app_android.MainActivity
import com.example.sentiancesdksample_app_android.R
import com.sentiance.sdk.*

import com.sentiance.sdk.OnInitCallback

class SDKParams {
    var appId: String
    var appSecret: String
    var baseUrl: String?
    var link: MetaUserLinkerAsync?
    var initCb: OnInitCallback?
    var TAG = "SENTIANCEHELPER"

    constructor(
        appId: String,
        appSecret: String,
        baseUrl: String?,
        link: MetaUserLinkerAsync?,
        initCb: OnInitCallback?
    ) {
        this.appId = appId
        this.appSecret = appSecret
        this.baseUrl = baseUrl
        this.link = link
        this.initCb = initCb
    }

    override fun toString(): String {
        return "SDKParams(appId='$appId', appSecret='$appSecret', baseUrl=$baseUrl, link=$link, initCb=$initCb)"
    }
}

class SentianceHelper : Activity() {
    private val SENTIANCE_APP_ID = "SentianceAppId"
    private val SENTIANCE_APP_SECRET = "SentianceAppSecret"
    private val SENTIANCE_BASE_URL = "SentianceBaseUrl"

    private val SHARED_PREFS = "sentiancesdksample_app_android"
    private val CHANNEL_ID = "SentianceChannel"
    private val NOTIFICATION_NAME = "SentianceNotification"

    var TAG = "SENTIANCEHELPER"

    /**
     * Initialises the Sentiance SDK. This method should be called only once in the entire codebase,
     * specifically in the application( onCreate ) method.
     * This invocation ensures that the Sentiance SDK can continue detecting while the
     * application in the background.
     *
     * - Parameter initCallback: An optional callback
     */
    fun initSdk(context: Context, initCallback: OnInitCallback? = null) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val appId = sharedPreferences.getString(SENTIANCE_APP_ID, "").toString()
        val appSecret = sharedPreferences.getString(SENTIANCE_APP_SECRET, "").toString()
        val baseUrl =
            sharedPreferences.getString(SENTIANCE_BASE_URL, "https://preprod-api.sentiance.com/")
                .toString()
        val setupSdkConfig = SDKParams(appId, appSecret, baseUrl, null, initCallback)

        configureSdk(context, setupSdkConfig)
    }

    /**
     * Creates a sentiance user for the application.
     *
     * his method should, ideally, be called when the SDK needs to come into action.
     * e.g on user registration, on login or on accessing a particular feature.
     *
     * Note: Calling this method multiple times will not cause multiple sentiance users to be created
     *
     * - Parameter SDKParams: The SDK Params
     */
    fun createUser(context: Context, params: SDKParams) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SENTIANCE_APP_ID, params.appId).apply()
        sharedPreferences.edit().putString(SENTIANCE_APP_SECRET, params.appSecret).apply()

        params.baseUrl.let {
            sharedPreferences.edit().putString(it, params.baseUrl).apply()
        }

        configureSdk(context, params)
    }

    private fun configureSdk(context: Context, params: SDKParams) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        if (params.appId == "" || params.appSecret == "") {
            return
        }

        if (Sentiance.getInstance(context).initState == InitState.INITIALIZED) {

            params.initCb?.let {
                it.onInitSuccess()
            }
            return
        }

        val config = SdkConfig.Builder(
            params.appId,
            params.appSecret,
            createNotification(context)
        )

        if (params.baseUrl != null) {
            config.baseURL(params.baseUrl)
        }

        if (params.link != null) {
            config.setMetaUserLinker(params.link)
        }

        if (sharedPreferences?.getString(SENTIANCE_BASE_URL, "") != "") {
            config.baseURL(
                sharedPreferences?.getString(
                    SENTIANCE_BASE_URL,
                    "https://api.sentiance.com/"
                )
            )
        }

        Sentiance.getInstance(context).init(config.build(), params.initCb)
    }

    /**
     * Resets the SDK
     *
     * This method should, ideally, be called to logout the user
     */
    fun reset(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        for(key in listOf(SENTIANCE_APP_ID, SENTIANCE_APP_SECRET, SENTIANCE_BASE_URL)){
            sharedPreferences.edit().remove(key).commit()
        }
        Sentiance.getInstance(context).reset(null)
    }

    private fun createNotification(context: Context): Notification? {
        // PendingIntent that will start your application's MainActivity
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // On Oreo and above, you must create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name) + " is running")
            .setContentText("Touch to open.")
            .setContentIntent(pendingIntent)
            .setShowWhen(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }
}