package com.sentiance.sdksampleapp.helpers

import android.app.Activity
import android.util.Log
import android.widget.Toast


fun Activity.toast(message: String) {
    runOnUiThread {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Any.log(message: String) {
    Log.d(javaClass.simpleName, message)
}
