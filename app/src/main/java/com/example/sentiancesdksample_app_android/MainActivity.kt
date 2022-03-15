package com.example.sentiancesdksample_app_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Nullable
import com.example.sentiancesdksample_app_android.helpers.HttpHelper
import com.example.sentiancesdksample_app_android.helpers.SDKParams
import com.example.sentiancesdksample_app_android.helpers.SentianceHelper
import com.sentiance.sdk.*
import com.sentiance.sdk.OnInitCallback.InitIssue


class MainActivity : AppCompatActivity() {

    val TAG = "SENTIANCEHELPER"
    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var initWithoutUserLinkingView: RelativeLayout
    private lateinit var myApplication: MyApplication

    private lateinit var sentianceHelper: SentianceHelper
    private lateinit var httpHelper: HttpHelper

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

        val linker = Linker()

        httpHelper.fetchConfig { result ->

            val sdkParams =
                SDKParams(
                    result.id,
                    result.secret,
                    baseUrl,
                    linker,
                    onInitCallBack()
                )

            sentianceHelper.createUser(applicationContext, sdkParams)
        }

    }

    private fun onInitCallBack(): OnInitCallback {
        return object : OnInitCallback {
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

        initWithUserLinkingView.setOnClickListener {
            handleInitWithUserLinkClick()
        }
    }
}

class Linker : MetaUserLinkerAsync {

    val TAG = "SENTIANCEHELPER"

    override fun link(installId: String?, callback: MetaUserLinkerCallback?) {
        Log.i(TAG, "Link $installId")
    }
}