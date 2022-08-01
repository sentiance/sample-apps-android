package com.sentiance.sdksampleapp.helpers


import okhttp3.*
import okhttp3.Request.Builder
import org.json.JSONObject
import java.io.IOException

class AuthCodeAPI {

    fun getAuthCodeFromServer(callback: (authCode:String?, error:String?) -> Unit) {
        OkHttpClient().newCall(
            Builder()
                .url(BACKEND_BASE_URL + "auth/code")
                .header("Content-Type","application/json")
                .header("Accept","application/json")
                .get()
                .build()
        ).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonObject = JSONObject(response.body!!.string())
                val authCode = jsonObject.getString("auth_code")
                callback(authCode, null)
            }

        })
    }

    companion object {
        private const val BACKEND_BASE_URL = "http://localhost:8000/"
    }
}