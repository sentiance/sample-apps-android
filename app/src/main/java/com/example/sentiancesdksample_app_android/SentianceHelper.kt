package com.example.sentiancesdksample_app_android

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sentiance.sdk.*

class SentianceHelper : Activity() {
    private val SENTIANCE_APP_ID = "SentianceAppId"
    private val SENTIANCE_APP_SECRET = "SentianceAppSecret"
    private val SENTIANCE_BASE_URL = "SentianceBaseUrl"

    private val SHARED_PREFS = "sentiancesdksample_app_android"
    private val channelId = "SentianceChannel"
    private val notificationName = "SentianceNotification"

//    var context = this.applicationContext
//    private var context: Context? = null
//    private lateinit var sharedPreferences: SharedPreferences

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
            sharedPreferences.edit().putString(SENTIANCE_BASE_URL, params.baseUrl).apply()
        }

        configureSdk(context, params)
    }

    private fun configureSdk(context: Context, params: SDKParams) {

        val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

        if (params.appId == "" || params.appSecret == "") {
            return
        }

        if (Sentiance.getInstance(context).initState == InitState.INITIALIZED) {
            params.initCb.let {
                it
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
                    "https://preprod-api.sentiance.com/"
                )
            )
        }

        Sentiance.getInstance(context).init(config.build(), params.initCb)
    }


    private fun createNotification(context: Context): Notification? {
        // PendingIntent that will start your application's MainActivity
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        // On Oreo and above, you must create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, notificationName, NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.app_name) + " is running")
            .setContentText("Touch to open.")
            .setContentIntent(pendingIntent)
            .setShowWhen(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }
}