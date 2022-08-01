package com.sentiance.sdksampleapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import com.sentiance.sdk.*
import com.sentiance.sdk.usercreation.UserCreationOptions
import com.sentiance.sdksampleapp.helpers.AuthCodeAPI
import com.sentiance.sdksampleapp.helpers.log
import com.sentiance.sdksampleapp.helpers.toast


class UserCreationActivity : AppCompatActivity() {

    private val sentiance = Sentiance.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (sentiance.userExists()) {
            openDashboard()
            return
        }
        setContentView(R.layout.activity_main)
        setupView()
    }

    private fun handleCreateUserClick() {
        // fetch auth code
        toast("creating user...")

        // Get authentication from your backend to create sentiance SDK user
        // See https://github.com/sentiance/sample-apps-api
        AuthCodeAPI().getAuthCodeFromServer { authCode, error ->
            if (authCode != null) {
                createUserAndEnableDetections(authCode)
            } else {
                toast(error!!)
            }
        }
    }


    /**
     * - Creates a Sentiance user and links it with your application's user.
     * - Enable detections.
     *
     * @param authCode authentication code obtained from Sentiance backend to create a user.
     */
    private fun createUserAndEnableDetections(authCode: String) {
        val userCreationOptions = UserCreationOptions.Builder(authCode)
            .build()

        sentiance
            .createUser(userCreationOptions)
            .addOnSuccessListener { userCreationResult ->
                val successMessage = "User created successfully, userId: ${userCreationResult.userInfo.userId}"
                enableDetections()
                log(successMessage)
                toast(successMessage)
            }
            .addOnFailureListener { userCreationError ->
                val failureMessage =
                    "Failed to create a user, reason: ${userCreationError.reason} details: ${userCreationError.details}"
                log(failureMessage)
                toast(failureMessage)
            }
    }

    private fun enableDetections() {
        sentiance
            .enableDetections()
            .addOnSuccessListener { enableDetectionResult ->
                val successMessage =
                    "Detections enabled successfully, detection status: ${enableDetectionResult.detectionStatus}"
                log(successMessage)
                toast(successMessage)
                openDashboard()
            }
            .addOnFailureListener { enableDetectionsError ->
                val failureMessage =
                    "Failed to enable detections, reason: ${enableDetectionsError.reason} sdkStatus: ${enableDetectionsError.sdkStatus}"
                log(failureMessage)
                toast(failureMessage)
            }
    }

    private fun setupView() {
        val createUserView = findViewById<RelativeLayout>(R.id.create_user_layout)
        createUserView.setOnClickListener {
            handleCreateUserClick()
        }
    }

    private fun openDashboard() {
        startActivity(Intent(this, Dashboard::class.java))
        finish()
    }
}

