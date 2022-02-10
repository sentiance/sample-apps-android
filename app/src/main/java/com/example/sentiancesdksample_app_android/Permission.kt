package com.example.sentiancesdksample_app_android

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import java.util.ArrayList

class Permission @JvmOverloads internal constructor(
    private val name: String,
    val manifestPermissions: Array<String>,
    val askCode: Int,
    val dialogTitle: String,
    val dialogMessage: String,
    dependantPermissions: List<Permission>? = emptyList()
) {
    val dependencies: List<Permission>

    fun isGranted(activity: Activity?): Boolean {
        for (permission in manifestPermissions) {
            if (ActivityCompat.checkSelfPermission(activity!!, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun shouldShowRationale(activity: Activity?): Boolean {
        for (permission in manifestPermissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                return true
            }
        }
        return false
    }

    override fun toString(): String {
        return "Permission{" +
                "name='" + name + '\'' +
                '}'
    }

    fun getCanShowAgain(activity: Activity): Boolean {
        val key = KEY_CAN_SHOW_AGAIN + "_" + askCode
        return getPrefs(activity).getBoolean(key, true)
    }

    fun setCanShowAgain(activity: Activity, value: Boolean) {
        val key = KEY_CAN_SHOW_AGAIN + "_" + askCode
        getPrefs(activity).edit().putBoolean(key, value).apply()
    }

    fun isShowRationaleSet(activity: Activity): Boolean {
        val key = KEY_SHOW_RATIONALE + "_" + askCode
        return getPrefs(activity).contains(key)
    }

    fun setShowRationale(activity: Activity, value: Boolean) {
        val key = KEY_SHOW_RATIONALE + "_" + askCode
        getPrefs(activity).edit().putBoolean(key, value).apply()
    }

    fun clearShowRationale(activity: Activity) {
        val key = KEY_SHOW_RATIONALE + "_" + askCode
        getPrefs(activity).edit().remove(key).apply()
    }

    private fun getPrefs(activity: Activity): SharedPreferences {
        return activity.getSharedPreferences("permission", Context.MODE_PRIVATE)
    }

    companion object {
        private const val KEY_CAN_SHOW_AGAIN = "can_show_again"
        private const val KEY_SHOW_RATIONALE = "show_rationale"
    }

    init {
        dependencies = ArrayList(dependantPermissions)
    }
}