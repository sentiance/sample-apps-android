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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class Dashboard : AppCompatActivity() {

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

        userStatusView.findViewById<TextView>(id.user_id).setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                "User id",
                userStatusView.findViewById<TextView>(id.user_id).text
            )
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "User ID copied !", Toast.LENGTH_SHORT).show()
        }

        userStatusView.findViewById<TextView>(id.user_id).text = sentiance.userId
        userStatusView.findViewById<TextView>(id.install_id).text = sentiance.userId
        userStatusView.findViewById<TextView>(id.external_user_id).text =
            sentiance.userId

        /* Setup PermissionStatus */
        permissionsStatusView = findViewById(id.card_view_permissions_status)

        permissionsStatusView.findViewById<TextView>(id.location_state).text =
            getLocationStatus().key
        permissionsStatusView.findViewById<TextView>(id.motion_state).text =
            getMotionStatus().key

        if (checkAllPermission()) {
            permissionsStatusView.findViewById<TextView>(id.permission_health).text =
                PermissionsStatus.ALL_PERMISSIONS_PROVIDED.key
        } else {
            permissionsStatusView.findViewById<TextView>(id.permission_health).text =
                PermissionsStatus.APP_WILL_NOT_WORK_OPTIMALLY.key

            permissionsStatusView.findViewById<TextView>(id.permission_health).setTextColor(
                resources.getColor(red, applicationContext.theme)
            )
        }
    }

    private fun formatString(str: String): String {
        return str.substring(0, 1) + str.substring(1).lowercase();
    }

    private fun getLocationStatus(): Permissions {
        if (Sentiance.getInstance(this).sdkStatus.isPreciseLocationPermGranted) {
            return Permissions.ALWAYS
        }
        if (Sentiance.getInstance(this).sdkStatus.isLocationPermGranted) {
            return Permissions.WHILE_IN_USE
        }
        return Permissions.NEVER
    }

    private fun getMotionStatus(): Permissions {
        if (Sentiance.getInstance(this).sdkStatus.isActivityRecognitionPermGranted) {
            return Permissions.ALWAYS
        }
        return Permissions.NEVER
    }

    private fun checkAllPermission(): Boolean {
        return getMotionStatus() == getLocationStatus() && getMotionStatus() == Permissions.ALWAYS
    }

    fun stopSdk(view: View) {
        setupView()
    }

}