package com.example.sentiancesdksample_app_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.sentiancesdksample_app_android.R.*
import com.example.sentiancesdksample_app_android.R.color.red
import com.sentiance.sdk.Sentiance

class Dashboard : AppCompatActivity() {

    private val TAG = "SDKStarter"
    private val ALL_PERMISSIONS_PROVIDED = "All permissions provided"
    private val APP_WILL_NOT_WORK_OPTIMALY = "App will not work optimaly"
    private val listener = null

    enum class PERMISSIONS {
        ALWAYS,
        NEVER,
        WHILE_IN_USE,
    }

    private lateinit var initStatusView: RelativeLayout
    private lateinit var userStatusView: RelativeLayout
    private lateinit var permissionsStatusView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PermissionManager(this).notGrantedPermissions.isNotEmpty()) {
            startActivity(Intent(this, PermissionCheckActivity::class.java))
        }

        setContentView(layout.activity_dashboard)
        setupView()

    }

    private fun setupView() {
        var sentiance: Sentiance = Sentiance.getInstance(this)

        /* Setup InitStatus */
        initStatusView = findViewById(id.card_view_status)
        initStatusView.findViewById<TextView>(id.state_init_status).text =
            formatString(sentiance.initState.name)
        initStatusView.findViewById<TextView>(id.state_sdk_status).text =
            formatString(sentiance.sdkStatus.startStatus.name)

        /* Setup userStatus */
        userStatusView = findViewById(id.card_view_user_status)
        userStatusView.findViewById<TextView>(id.user_id).text = sentiance.userId
        userStatusView.findViewById<TextView>(id.install_id).text = sentiance.userId
        userStatusView.findViewById<TextView>(id.external_user_id).text =
            sentiance.userId

        /* Setup PermissionStatus */
        permissionsStatusView = findViewById(id.card_view_permissions_status)

        permissionsStatusView.findViewById<TextView>(id.location_state).text = getLocationStatus().toString();
        permissionsStatusView.findViewById<TextView>(id.motion_state).text =
            getMotionStatus().toString()

        if (checkAllPermission()) {
            permissionsStatusView.findViewById<TextView>(id.permission_health).text =
                ALL_PERMISSIONS_PROVIDED
        } else {
            permissionsStatusView.findViewById<TextView>(id.permission_health).text =
                APP_WILL_NOT_WORK_OPTIMALY
            permissionsStatusView.findViewById<TextView>(id.permission_health).setTextColor(
                resources.getColor(red, applicationContext.theme)
            )
        }
    }

    private fun formatString(str: String): String {
        return str.substring(0, 1) + str.substring(1).lowercase();
    }

    private fun getLocationStatus(): PERMISSIONS {
        if (Sentiance.getInstance(this).sdkStatus.isPreciseLocationPermGranted) {
            return PERMISSIONS.ALWAYS
        }
        if (Sentiance.getInstance(this).sdkStatus.isLocationPermGranted) {
            return PERMISSIONS.WHILE_IN_USE
        }
        return PERMISSIONS.NEVER
    }

    private fun getMotionStatus(): PERMISSIONS {
        if (Sentiance.getInstance(this).sdkStatus.isActivityRecognitionPermGranted) {
            return PERMISSIONS.ALWAYS
        }
        return PERMISSIONS.NEVER
    }

    private fun checkAllPermission(): Boolean {
        return getMotionStatus() == getLocationStatus() && getMotionStatus() == PERMISSIONS.ALWAYS
    }

    fun stopSdk(view: View) {

    }


}