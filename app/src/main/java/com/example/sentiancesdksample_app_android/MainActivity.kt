package com.example.sentiancesdksample_app_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatTextView
import com.example.sentiancesdksample_app_android.helpers.HttpHelper
import com.example.sentiancesdksample_app_android.helpers.SDKParams
import com.example.sentiancesdksample_app_android.helpers.SentianceHelper
import com.sentiance.sdk.OnInitCallback
import com.sentiance.sdk.OnInitCallback.InitIssue
import com.sentiance.sdk.Sentiance

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var initWithoutUserLinkingView: RelativeLayout
    private lateinit var myApplication: MyApplication

    private lateinit var sentianceHelper: SentianceHelper
    private lateinit var httpHelper: HttpHelper

    private lateinit var config: HttpHelper.Config

    private val baseUrl = "https://preprod-api.sentiance.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication = (applicationContext as MyApplication)
        sentianceHelper = SentianceHelper()
        httpHelper = HttpHelper()
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun handleInitWithUserLinkClick() {
        Log.i(TAG, "handleInitWithUserLinkClick()")
        httpHelper.fetchConfig()
    }

    private fun handleInitWithoutUserLinkingClick() {
        Log.i(TAG, "handleInitWithoutUserLinkingClick()")

        val initCallback: OnInitCallback = object : OnInitCallback {
            override fun onInitSuccess() {
                Log.i("MainActivity/onInitSuccess", "Good Job")
                Sentiance.getInstance(applicationContext).start {
                    //  You can include any app specific code you would like
                    //  e.g. log the "start status", etc
                    startNewActivity()
                }
            }

            override fun onInitFailure(issue: InitIssue, @Nullable th: Throwable?) {
                Log.i("MainActivity/onInitFailure", "issue: $issue")
                startNewActivity()
            }
        }

        val sdkParams =
            SDKParams(
                BuildConfig.SENTIANCE_APP_ID,
                BuildConfig.SENTIANCE_SECRET,
                baseUrl,
                null,
                initCallback
            )

        // create user from the helper file
        sentianceHelper.createUser(applicationContext, sdkParams)

//        var config = httpHelper.fetchConfig()
//
//        config.let {
//            sentianceHelper.createUser(
//                applicationContext,
//                SDKParams(
//                    config.id,
//                    config.secret,
//                    baseUrl,
//                    null,
//                    initCallback
//                )
//            )
//        }
    }

    private fun startNewActivity() {
        Log.i(TAG, "startNewActivity")
        val intent = Intent(applicationContext, Dashboard::class.java)
        startActivity(intent)
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
            handleInitWithoutUserLinkingClick()
        }

    }
}