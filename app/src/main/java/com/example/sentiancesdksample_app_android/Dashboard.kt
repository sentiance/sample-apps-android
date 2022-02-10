package com.example.sentiancesdksample_app_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class Dashboard : AppCompatActivity() {

    private val TAG = "SDKStarter"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        if (PermissionManager(this).notGrantedPermissions.isNotEmpty()) {
            startActivity(Intent(this, PermissionCheckActivity::class.java))
        }
    }


    fun stopSdk(view: View) {

    }

    private fun startNextActivity() {
        val intent = Intent(this, Dashboard::class.java)
    }

}