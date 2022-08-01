package com.sentiance.sdksampleapp

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.sentiance.sdksampleapp.R.*
import com.sentiance.sdksampleapp.R.color.red

import android.widget.Button
import com.sentiance.sdksampleapp.helpers.*
import com.sentiance.sdk.*
import com.sentiance.sdksampleapp.R.color.green


class Dashboard : AppCompatActivity() {

    private val sentiance: Sentiance = Sentiance.getInstance(this)
    private val sdkStatusAnalyzer = SdkStatusAnalyzer()

    private lateinit var collectingDataStatus: TextView
    private lateinit var initStatus: TextView
    private lateinit var userId: TextView
    private lateinit var locationPermission: TextView
    private lateinit var motionPermission: TextView
    private lateinit var permissionHealth: TextView
    private lateinit var resetSdk: Button
    private lateinit var detectionIssues: TextView
    private lateinit var detectionWarnings: TextView
    private lateinit var detectionStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PermissionManager(this).notGrantedPermissions.isNotEmpty()) {
            startActivity(Intent(this, PermissionCheckActivity::class.java))
        }

        setContentView(layout.activity_dashboard)
        setupView()
        listenToSdkStatus()
    }

    private fun setupView() {
        setupCollectingDataStatusView()
        setupIniStatusView()
        setupUserIdView()
        setupPermissionStatusView()
        setupResetButton()
        setupDetectionStatusView()
    }

    private fun setupDetectionStatusView() {
        val detectionView = findViewById<View>(id.card_view_detection_status)
        detectionIssues = detectionView.findViewById(id.detection_issues)
        detectionWarnings = detectionView.findViewById(id.detection_warnings)
        detectionStatus = detectionView.findViewById(id.detection_status)

        updateDetectionStatus(sentiance.sdkStatus)
    }

    private fun setupCollectingDataStatusView() {
        collectingDataStatus = findViewById(id.collecting_data_text_view)
        updateDataCollectionStatus()
    }

    private fun updateDataCollectionStatus() {
        val drawableId:Int
        val color: Int
        val text: String
        if (isDetecting()) {
            drawableId = drawable.ic_pulse_dot
            color = resources.getColor(green, applicationContext.theme)
            text = resources.getString(string.collecting_data)
        } else {
            drawableId = drawable.ic_pulse_dot_red
            color = resources.getColor(red, applicationContext.theme)
            text = resources.getString(string.not_collecting_data)
        }

        collectingDataStatus.text = text
        collectingDataStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableId, 0, 0, 0)
        collectingDataStatus.setTextColor(color)
    }


    private fun setupIniStatusView() {
        initStatus = findViewById<RelativeLayout>(id.card_view_status).findViewById(id.state_init_status)
        updateInitState()
    }

    private fun updateInitState() {
        val state = sentiance.initState
        when (sentiance.initState) {
            InitState.INITIALIZED ->
                initStatus.text = state.name
            InitState.NOT_INITIALIZED,
            InitState.INIT_IN_PROGRESS,
            InitState.RESETTING -> {
                initStatus.setBackgroundResource(drawable.styled_text_view_background_red)
                initStatus.setTextColor(resources.getColor(red, applicationContext.theme))
            }

        }
    }

    private fun setupUserIdView() {
        if (isInitialized()) {
            userId = findViewById<RelativeLayout>(id.card_view_user_status).findViewById(id.user_id)

            /* Setup userStatus */
            userId.text = sentiance.userId

            /* CopyToClipBoard */
            userId.setOnClickListener {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    "User id",
                    userId.text
                )
                clipboard.setPrimaryClip(clip)
                toast("User ID copied !")
            }
        }
    }

    private fun setupPermissionStatusView() {
        val permissionView = findViewById<RelativeLayout>(id.card_view_permissions_status)
        locationPermission = permissionView.findViewById(id.location_state)
        motionPermission = permissionView.findViewById(id.motion_state)
        permissionHealth = permissionView.findViewById(id.permission_health)

        updatePermissionStatus()
    }

    private fun updatePermissionStatus() {
        locationPermission.text = sdkLocationPermissionStatus().name
        motionPermission.text = isActivityRecognitionPermGranted().toString()

        if (allPermissionsGranted()) {
            permissionHealth.text = getString(string.all_permissions_provided)
            locationPermission.setTextColor(resources.getColor(green, applicationContext.theme))
            motionPermission.setTextColor(resources.getColor(green, applicationContext.theme))
            permissionHealth.setTextColor(resources.getColor(green, applicationContext.theme))
        } else {
            permissionHealth.text = getString(string.permission_warning)
            locationPermission.setTextColor(resources.getColor(red, applicationContext.theme))
            motionPermission.setTextColor(resources.getColor(red, applicationContext.theme))
            permissionHealth.setTextColor(resources.getColor(red, applicationContext.theme))
        }
    }

    private fun setupResetButton() {
        resetSdk = findViewById(id.button_dashboard)
        resetSdk.setOnClickListener {
            toast("Resetting SDK")
            sentiance.reset()
                .addOnFailureListener {
                    val failureMessage = "Failed to reset SDK, reason: ${it.reason} exception: ${it.exception}"
                    log(failureMessage)
                    toast(failureMessage)
                }
                .addOnSuccessListener {
                    finish()
                    startActivity(Intent(this, UserCreationActivity::class.java))
                }
        }
    }

    private fun listenToSdkStatus() {
        sentiance.setSdkStatusUpdateListener { sdkStatus ->
            updateDetectionStatus(sdkStatus)
            updateInitState()
            updatePermissionStatus()
            updateDataCollectionStatus()
        }
    }

    private fun updateDetectionStatus(sdkStatus: SdkStatus) {
        val detectionsIssues = sdkStatusAnalyzer.checkDetectionIssues(sdkStatus)
        val detectionsWarnings = sdkStatusAnalyzer.checkDetectionWarnings(sdkStatus)
        updateDetectionIssues(detectionsIssues)
        updateDetectionWarnings(detectionsWarnings)

        if (sdkStatus.detectionStatus != DetectionStatus.ENABLED_AND_DETECTING) {
            detectionStatus.setTextColor(resources.getColor(red, application.theme))
        } else {
            detectionStatus.setTextColor(resources.getColor(green, application.theme))
        }
        detectionStatus.text = sdkStatus.detectionStatus.name
    }

    private fun updateDetectionIssues(issues: List<String>) {
        detectionIssues.text = listToText(issues)
    }

    private fun updateDetectionWarnings(warnings: List<String>) {
        detectionWarnings.text = listToText(warnings)
    }

    private fun listToText(items: List<String>): String {
        var text = ""
        items.forEachIndexed { index, item ->
            if (index > 0) {
                text += "\n"
            }
            text += "${index + 1}: $item"
        }
        return text
    }

    private fun sdkLocationPermissionStatus() = sentiance.sdkStatus.locationPermission

    private fun isLocationPermissionGranted() = sdkLocationPermissionStatus() == SdkStatus.LocationPermission.ALWAYS

    private fun isActivityRecognitionPermGranted() = sentiance.sdkStatus.isActivityRecognitionPermGranted

    private fun allPermissionsGranted() = isActivityRecognitionPermGranted() && isLocationPermissionGranted()

    private fun isInitialized() = Sentiance.getInstance(this).initState == InitState.INITIALIZED

    private fun isDetecting() =
        Sentiance.getInstance(this).sdkStatus.detectionStatus == DetectionStatus.ENABLED_AND_DETECTING
}