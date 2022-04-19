package com.example.sentiancesdksample_app_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import com.example.sentiancesdksample_app_android.helpers.HttpHelper
import com.example.sentiancesdksample_app_android.helpers.SDKParams
import com.example.sentiancesdksample_app_android.helpers.SentianceHelper
import com.sentiance.sdk.*
import com.sentiance.sdk.OnInitCallback.InitIssue


class MainActivity : AppCompatActivity(), MetaUserLinkerAsync {

    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var mainApplication: MainApplication

    private lateinit var sentianceHelper: SentianceHelper
    private lateinit var httpHelper: HttpHelper

    private val SHARED_PREFS = "sentiancesdksample_app_android"
    private val SENTIANCE_INSTALL_ID = "SentianceInstallId"
    private val baseUrl = "https://prepod-api.sentiance.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainApplication = (applicationContext as MainApplication)
        sentianceHelper = SentianceHelper()
        httpHelper = HttpHelper()
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun handleInitWithUserLinkClick() {
        httpHelper.fetchConfig { result ->
            result?.let {
                sentianceHelper.createUser(
                    applicationContext, SDKParams(
                        result.id,
                        result.secret,
                        baseUrl,
                        this,
                        onInitCallBack()
                    )
                )
            } ?: run {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Error: it seems that the sample backend service is not running.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun link(installId: String?, callback: MetaUserLinkerCallback?) {
        httpHelper.requestLinking(installId!!) {
            val sharedPreferences =
                applicationContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            sharedPreferences.edit().putString(SENTIANCE_INSTALL_ID, installId).apply()
            callback?.onSuccess()
        }
    }

    private fun onInitCallBack(): OnInitCallback {
        return object : OnInitCallback {
            override fun onInitSuccess() {
                Sentiance.getInstance(applicationContext).start {
                    //  You can include any app specific code you would like
                    //  e.g. log the "start status", etc
                    startNewActivity()
                }
            }

            override fun onInitFailure(issue: InitIssue, @Nullable th: Throwable?) {
                Toast.makeText(this@MainActivity, "onInitFailure: $issue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun startNewActivity() {
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

