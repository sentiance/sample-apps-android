package com.sentiance.sdksampleapp

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.ArrayList

class PermissionManager(private val mActivity: Activity) {

    val notGrantedPermissions = allPermissions.filter { !it.isGranted(mActivity) }

    private fun updateCanShowAgainPermissions() {
        for (p in allPermissions) {
            if (p.isGranted(mActivity)) {
                // Permission is granted. Reset the show rationale and can show again prefs.
                p.setCanShowAgain(mActivity, true)
                p.clearShowRationale(mActivity)
                continue
            }
            if (!p.shouldShowRationale(mActivity)) {
                if (p.isShowRationaleSet(mActivity)) {
                    // We were allowed to show a rationale before, but not anymore.
                    // This is a result of the "don't ask again" option selected by the user.
                    p.setShowRationale(mActivity, false)
                    p.setCanShowAgain(mActivity, false)
                }
            } else {
                // We can show a rational. This is when our permission request
                // was shot down by the user the first time we asked.
                p.setShowRationale(mActivity, true)
            }
        }
    }

    private val isQPlus: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    private val isRPlus: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    private val allPermissions: List<Permission>
        get() {
            val list: MutableList<Permission> = ArrayList()
            if (isRPlus) {
                list.addAll(rPlusLocationPermissions)
            } else {
                list.addAll(preRLocationPermissions)
            }
            if (isQPlus) {
                list.add(
                    Permission(
                        "Activity Recognition", arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                        ACTIVITY_PERMISSION_REQUEST_CODE, TITLE_ACTIVITY, MESSAGE_ACTIVITY
                    )
                )
            }
            return list
        }
    private val preRLocationPermissions: List<Permission>
        get() {
            val permissions: MutableList<Permission> = ArrayList()
            val permissionStrings = if (isQPlus) arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            permissions.add(
                Permission(
                    "Location", permissionStrings, LOCATION_PERMISSION_REQUEST_CODE,
                    TITLE_LOCATION, MESSAGE_LOCATION
                )
            )
            return permissions
        }

    // The background location permission has a dependency on the foreground location permission.
    @get:RequiresApi(api = Build.VERSION_CODES.R)
    private val rPlusLocationPermissions: List<Permission>
        get() {
            val permissions: MutableList<Permission> = ArrayList()
            val fgLocation = Permission(
                "Foreground Location", arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FG_LOCATION_PERMISSION_REQUEST_CODE, TITLE_LOCATION, MESSAGE_LOCATION
            )

            // The background location permission has a dependency on the foreground location permission.
            val bgLocation = Permission(
                "Background Location",
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BG_LOCATION_PERMISSION_REQUEST_CODE,
                TITLE_LOCATION,
                MESSAGE_LOCATION,
                listOf(fgLocation)
            )
            permissions.add(fgLocation)
            permissions.add(bgLocation)
            return permissions
        }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 15440
        private const val FG_LOCATION_PERMISSION_REQUEST_CODE = 15441
        private const val BG_LOCATION_PERMISSION_REQUEST_CODE = 15442
        private const val ACTIVITY_PERMISSION_REQUEST_CODE = 15443
        private const val TITLE_LOCATION = "Location permission"
        private const val MESSAGE_LOCATION =
            "The Sentiance SDK needs access to your location all the time in order to build your profile."
        private const val TITLE_ACTIVITY = "Motion activity permission"
        private const val MESSAGE_ACTIVITY =
            "The Sentiance SDK needs access to your activity data in order to build your profile."
    }

    init {
        updateCanShowAgainPermissions()
    }
}