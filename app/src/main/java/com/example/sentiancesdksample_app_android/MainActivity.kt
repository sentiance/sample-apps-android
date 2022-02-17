package com.example.sentiancesdksample_app_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView

class MainActivity : AppCompatActivity() {

    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var initWithoutUserLinkingView: RelativeLayout
    private lateinit var myApplication: MyApplication;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication = (applicationContext as MyApplication)!!
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun handleInitWithUserLinkClick() {
        // todo
    }

    private fun handleInitWithouUserLinkingClick() {
        myApplication.initializeSentianceSdk()
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