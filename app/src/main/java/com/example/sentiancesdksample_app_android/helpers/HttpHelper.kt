package com.example.sentiancesdksample_app_android.helpers

import android.app.Activity
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.lang.Error
import java.net.URL
import java.util.*

enum class EndPoint(val rawValue: String) {
    config("config"), healthChecks("healthchecks"), userLink("users/:id/link");
}

class HttpHelper : Activity() {
    val TAG = "HttpHelper"
    private val SHARED_PREFS = "sentiancesdksample_app_android"
    private val baseURLString = "http://192.168.0.111:8000/"

    private val SENTIANCE_APP_ID = "SentianceAppId"
    private val SENTIANCE_APP_SECRET = "SentianceAppSecret"
    private val SENTIANCE_BASE_URL = "SentianceBaseUrl"

    private val username = "dev-1"
    private val password = "test"
    private val client = OkHttpClient()

    private fun getConfigUrl(): URL {
        return URL(baseURLString + EndPoint.config)
    }

    private fun getAuthHeader(): String {
        val rawAuthHeader = "$username:$password"
        val base64Auth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(rawAuthHeader.toByteArray())
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return "Basic $base64Auth"
    }

    private fun getUserLinkURL(installId: String): URL {
        val url = baseURLString + EndPoint.valueOf(EndPoint.userLink.rawValue)
        val newString = url.replace(":id", installId)
        return URL(newString)
    }

    data class Config(
        val id: String,
        val secret: String
    )

    data class UserLink(
        val id: String
    )

    data class LinkRequestBody(
        val external_id: String
    )

    private fun config(data: String?): Config? {
        val gson = GsonBuilder().create()
        return data?.let {
            gson.fromJson(data, Config::class.java)
        } ?: null
    }

    fun userLink(data: String?): UserLink? {
        val gson = GsonBuilder().create()
        return data?.let {
            gson.fromJson(data, UserLink::class.java)
        } ?: null
    }

    fun fetchConfig() {
        val url: URL = getConfigUrl()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", getAuthHeader())
            .addHeader("Accept", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $it")
                    val body = response?.body()?.string()
//                    processConfigRequest(body)
                    var config = config(body)
                    Log.i(TAG, "config => " + config.toString())

//                    val gson = GsonBuilder().create()
//                    val config = gson.fromJson(body, Config::class.java)
//                    val sharedPreferences =
//                        context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
//
//                    sharedPreferences.edit().putString(SENTIANCE_APP_ID, config.id).apply()
//                    sharedPreferences.edit().putString(SENTIANCE_APP_SECRET, config.secret).apply()
                }
            }
        })
    }

    private fun processConfigRequest(data: String?): Config? {
        return data?.let { config(data) } ?: null
    }

    fun linkUser(installId: String) {
        val url: URL = getUserLinkURL(installId)
        val userLinkBody = LinkRequestBody(installId)

        val body = RequestBody.create(
            MediaType.parse("application/json"), Gson().toJson(userLinkBody)
        )

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", getAuthHeader())
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()


        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            Log.i(TAG, "linkUser => " + Gson().toJson(response.body()))
            println(response.body().toString())
        }

    }

}