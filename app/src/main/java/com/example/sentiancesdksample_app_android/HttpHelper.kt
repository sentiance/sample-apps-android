package com.example.sentiancesdksample_app_android

import android.os.Build
import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.lang.Error
import java.net.URL
import java.util.*

enum class EndPoint(val rawValue: String) {
    config("config"), healthChecks("healthchecks"), userLink("users/:id/link");
}

class HttpHelper {
    val TAG = "HttpHelper"

    //    private val baseURLString = "http://localhost:8000/"
    private val baseURLString = "http://6ac7-2a02-1811-382a-8500-b0b8-5c92-bd75-2a2e.ngrok.io/"
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

    private fun config(json: String): Config? {
        var config: Config? = null
        try {
            config = Gson().fromJson(json, Config::class.java)
        } catch (err: Error) {
            Log.i(TAG, err.message.toString())
        }
        return config
    }

    fun processConfigRequest(data: String?): Config? {
        data?.let {
            return config(it)
        }
        return null
    }

    fun userLink(json: String): UserLink? {
        var userLink: UserLink? = null
        try {
            userLink = Gson().fromJson(json, UserLink::class.java)
        } catch (err: Error) {
            Log.i(TAG, err.message.toString())
        }
        return userLink
    }

    fun processUserLinkRequest(data: String?): UserLink? {
        data?.let {
            return userLink(data)
        }
        return null
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
                    Log.i(TAG, "Body => " + Gson().toJson(it.body()))
//                    var config = processConfigRequest(it.body().toString())
//                    Log.i(TAG, "Process config => " + config)
//                    Log.i(TAG, Gson().toJson(it.body()))
                }
            }
        })
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