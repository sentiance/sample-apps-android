package com.example.sentiancesdksample_app_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView

class MainActivity : AppCompatActivity() {

    private lateinit var initWithUserLinkingView: RelativeLayout
    private lateinit var initWithoutUserLinkingView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    private fun handleInitWithUserLinkClick() {
        Toast.makeText(this, "Init with userlinking", Toast.LENGTH_SHORT).show()
        startNextActivity()
    }

    private fun handleInitWithouUserLinkingClick() {
        Toast.makeText(this, "Init without userlinking", Toast.LENGTH_SHORT).show()
        startNextActivity()
    }

    private fun setupView() {
        initWithUserLinkingView = findViewById(R.id.cta_with_user_linking)
        initWithUserLinkingView.findViewById<TextView>(R.id.cta_textview).text = getString(R.string.initialise_SDK_with_user_linking)

        initWithoutUserLinkingView = findViewById(R.id.cta_without_user_linking)
        initWithoutUserLinkingView.findViewById<TextView>(R.id.cta_textview).text = getString(R.string.initialise_SDK_without_user_linking)
        initWithoutUserLinkingView.findViewById<AppCompatTextView>(R.id.cta_button).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_not_linking, 0,0)

        initWithUserLinkingView.setOnClickListener {
            handleInitWithUserLinkClick()
        }

        initWithoutUserLinkingView.setOnClickListener {
            handleInitWithouUserLinkingClick()
        }

    }

    private fun startNextActivity(){
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }
}