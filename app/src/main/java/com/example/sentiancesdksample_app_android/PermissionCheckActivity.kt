package com.example.sentiancesdksample_app_android

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

class PermissionCheckActivity : AppCompatActivity() {

    var mPermissionManager: PermissionManager? = null
    var grantPermissions: Button? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission_check)
        grantPermissions = findViewById(R.id.grant_permissions)
        grantPermissions?.setOnClickListener(View.OnClickListener { startPermissionsSettingActivity() })
        mPermissionManager = PermissionManager(this)
        val permissions: List<Permission> = mPermissionManager!!.notGrantedPermissions
        if (permissions.isNotEmpty() && !isHandlingPermissionResult(savedInstanceState)) {
            requestPermissions(permissions)
        } else {
            startMainActivity()
        }
    }

    private fun isHandlingPermissionResult(savedInstanceState: Bundle?): Boolean {
        return savedInstanceState != null &&
                savedInstanceState.getBoolean("android:hasCurrentPermissionsRequest", false)
    }

    override fun onResume() {
        super.onResume()
        if (mPermissionManager?.notGrantedPermissions?.isEmpty() == true) {
            startMainActivity()
        }
    }

    private fun startPermissionsSettingActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun requestPermissions(permissions: List<Permission>) {
        for (p in permissions) {
            if (requestPermission(p, false)) {
                return  // one at a time
            }
        }
    }

    private fun requestPermission(p: Permission, bypassRationale: Boolean): Boolean {
        for (dep in p.dependencies) {
            if (!dep.isGranted(this)) {
                // This permission depends on another one that's not yet been granted.
                return false
            }
        }
        if (!p.isGranted(this) && !p.getCanShowAgain(this)) {
            // This permission was denied more than once.
            return false
        }
        if (!bypassRationale && p.shouldShowRationale(this)) {
            showExplanation(p)
        } else if (!p.isGranted(this)) {
            ActivityCompat.requestPermissions(this, p.manifestPermissions, p.askCode)
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (alertDialog != null && alertDialog!!.isShowing) {
            // Avoid leaking the activity
            alertDialog!!.dismiss()
        }
        if (mPermissionManager?.notGrantedPermissions?.isEmpty() == true) {
            startMainActivity()
        } else {
            // Requesting a new permission doesn't work in the same activity that handles the result of a previous
            // request. We therefore restart this activity to automatically triggers additional permission requests.
            startPermissionCheckActivity()
        }
    }

    private fun showExplanation(p: Permission) {
        alertDialog = AlertDialog.Builder(this)
            .setTitle(p.dialogTitle)
            .setMessage(p.dialogMessage)
            .setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which -> requestPermission(p, true) })
            .show()
    }

    private fun startPermissionCheckActivity() {
        startActivity(Intent(this, PermissionCheckActivity::class.java))
        finish()
    }

    private fun startMainActivity() {
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}