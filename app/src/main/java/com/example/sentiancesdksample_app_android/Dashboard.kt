package com.example.sentiancesdksample_app_android

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.sentiancesdksample_app_android.R.*
import com.example.sentiancesdksample_app_android.R.color.red
import com.sentiance.sdk.Sentiance

import android.widget.Button
import android.widget.Toast
import com.example.sentiancesdksample_app_android.helpers.Permissions
import com.example.sentiancesdksample_app_android.helpers.PermissionsStatus
import com.example.sentiancesdksample_app_android.helpers.SentianceHelper
import com.sentiance.sdk.InitState
import com.sentiance.sdk.SdkStatus

class Dashboard : AppCompatActivity() {

    private lateinit var collectingDataStatusView: TextView
    private lateinit var initStatusView: RelativeLayout
    private lateinit var userStatusView: RelativeLayout
    private lateinit var permissionsStatusView: RelativeLayout
    private lateinit var buttonSdkStatus: Button

    private val SHARED_PREFS = "sentiancesdksample_app_android"
    private val SENTIANCE_INSTALL_ID = "SentianceInstallId"

    var sentiance: Sentiance = Sentiance.getInstance(this)
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PermissionManager(this).notGrantedPermissions.isNotEmpty()) {
            startActivity(Intent(this, PermissionCheckActivity::class.java))
        }

        setContentView(layout.activity_dashboard)
        sharedPreferences = applicationContext.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    private fun setupView() {
        setupCollectingDataStatusView()
        setupIniStatusView()
        setupSDKStatusView()
        setupUserIdView()
        setupPermissionStatusView()
        setupButtonStatusView()
    }

    private fun setupCollectingDataStatusView() {
        collectingDataStatusView = findViewById(id.collecting_data_text_view)
        if (sentiance.initState == InitState.INITIALIZED && sentiance.sdkStatus.startStatus == SdkStatus.StartStatus.STARTED
        ) {
            collectingDataStatusView.text = resources.getString(string.collecting_data)
        } else {
            collectingDataStatusView.text = resources.getString(R.string.not_collecting_data)
            collectingDataStatusView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_pulse_dot_red,
                0,
                0,
                0
            )
            collectingDataStatusView.setTextColor(
                resources.getColor(
                    red, applicationContext.theme
                )
            )
        }
    }

    private fun setupIniStatusView() {
        initStatusView = findViewById(id.card_view_status)

        if (sentiance.initState == InitState.INITIALIZED) {
            /* Setup InitStatus */
            initStatusView.findViewById<TextView>(id.state_init_status).text =
                InitState.INITIALIZED.name
        } else {
            /* Setup InitStatus */
            initStatusView.findViewById<TextView>(id.state_init_status).text =
                InitState.NOT_INITIALIZED.name
            initStatusView.findViewById<TextView>(id.state_init_status)
                .setBackgroundResource(drawable.styled_text_view_background_red)
            initStatusView.findViewById<TextView>(id.state_init_status).setTextColor(
                resources.getColor(
                    red, applicationContext.theme
                )
            )
            initStatusView.findViewById<TextView>(id.state_init_status)
                .setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_dot_red, 0, 0, 0)
        }
    }

    private fun setupSDKStatusView() {
        initStatusView = findViewById(id.card_view_status)

        if (sentiance.sdkStatus.startStatus == SdkStatus.StartStatus.STARTED) {
            initStatusView.findViewById<TextView>(id.state_sdk_status).text =
                sentiance.sdkStatus.startStatus.name
        } else {
            initStatusView.findViewById<TextView>(id.state_sdk_status).text =
                SdkStatus.StartStatus.NOT_STARTED.name
            initStatusView.findViewById<TextView>(id.state_sdk_status)
                .setBackgroundResource(drawable.styled_text_view_background_red)
            initStatusView.findViewById<TextView>(id.state_sdk_status).setTextColor(
                resources.getColor(
                    red, applicationContext.theme
                )
            )
            initStatusView.findViewById<TextView>(id.state_sdk_status)
                .setCompoundDrawablesRelativeWithIntrinsicBounds(drawable.ic_dot_red, 0, 0, 0)
        }
    }

    private fun setupUserIdView() {
        if (sentiance.initState == InitState.INITIALIZED) {
            userStatusView = findViewById(id.card_view_user_status)

            /* Setup userStatus */
            userStatusView.findViewById<TextView>(id.user_id).text =
                sentiance.userId
            userStatusView.findViewById<TextView>(id.install_id).text = sharedPreferences?.getString(SENTIANCE_INSTALL_ID, "")
            userStatusView.findViewById<TextView>(id.external_user_id).text = sharedPreferences?.getString(SENTIANCE_INSTALL_ID, "")

            /* CopyToClipBoard */
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
        }
    }

    private fun setupPermissionStatusView() {
        permissionsStatusView = findViewById(id.card_view_permissions_status)

        /* Setup PermissionStatus */
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

    private fun setupButtonStatusView() {
        buttonSdkStatus = findViewById(id.button_dashboard)
        if (sentiance.sdkStatus.startStatus == SdkStatus.StartStatus.STARTED) {
            buttonSdkStatus.text =
                applicationContext.resources.getText(string.dashboard_button_stop)
            buttonSdkStatus.setOnClickListener {
                SentianceHelper().reset(this)
                setupView()
            }
        } else {
            buttonSdkStatus.text =
                applicationContext.resources.getText(string.dashboard_button_start)
            buttonSdkStatus.setOnClickListener {
                sentiance.start { setupView() }
            }
        }
    }

    private fun getLocationStatus(): Permissions {
        if (sentiance.sdkStatus.isPreciseLocationPermGranted) {
            return Permissions.ALWAYS
        }
        if (sentiance.sdkStatus.isLocationPermGranted) {
            return Permissions.WHILE_IN_USE
        }
        return Permissions.NEVER
    }

    private fun getMotionStatus(): Permissions {
        if (sentiance.sdkStatus.isActivityRecognitionPermGranted) {
            return Permissions.ALWAYS
        }
        return Permissions.NEVER
    }

    private fun checkAllPermission(): Boolean {
        return getMotionStatus() == getLocationStatus() && getMotionStatus() == Permissions.ALWAYS
    }

}