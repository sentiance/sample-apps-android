package com.example.sentiancesdksample_app_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.sentiance.sdk.OnInitCallback

class MainActivity : AppCompatActivity() {

    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var initWithoutUserLinkingView: RelativeLayout
    private lateinit var myApplication: MyApplication
    private lateinit var sentianceHelper: SentianceHelper;
    private val BASE_URL = "https://preprod-api.sentiance.com/"
//    private val SHARED_PREFS = "sentiancesdksample_app_android"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myApplication = (applicationContext as MyApplication)!!
        sentianceHelper = SentianceHelper()
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun handleInitWithUserLinkClick() {
        // todo
    }

    private fun handleInitWithouUserLinkingClick() {
        val sdkParams =
            SDKParams(BuildConfig.SENTIANCE_APP_ID, BuildConfig.SENTIANCE_SECRET, BASE_URL, null, OnInitCallback())
//        Log.i("TAG", "MainActivity() -> " + sdkParams.toString())

        // create user from the helper file
        sentianceHelper.createUser(applicationContext, sdkParams)
    }

    private fun startNewActivity() {
        val intent = Intent(applicationContext, Dashboard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            handleInitWithouUserLinkingClick()
        }

    }
}