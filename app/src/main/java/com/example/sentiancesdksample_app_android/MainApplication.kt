package com.example.sentiancesdksample_app_android

import android.app.*
import android.content.Intent
import android.util.Log
import com.example.sentiancesdksample_app_android.helpers.SentianceHelper
import com.sentiance.sdk.*
import com.sentiance.sdk.OnInitCallback.InitIssue

class MainApplication : Application(), OnInitCallback, OnStartFinishedHandler {

    private val TAG = "SDKStarter"

    override fun onCreate() {
        super.onCreate()

        /* Init from SentianceHelper */
        var sentianceHelper = SentianceHelper()
        sentianceHelper.initSdk(applicationContext)
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
        val intent = Intent(applicationContext, Dashboard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }
}