package com.example.sentiancesdksample_app_android

import com.sentiance.sdk.MetaUserLinker
import com.sentiance.sdk.OnInitCallback

enum class Permissions(val key: String) {
    ALWAYS("ALWAYS"), NEVER("NEVER"), WHILE_IN_USE("WHILE IN USE");
}

enum class PermissionsStatus(val key: String) {
    ALL_PERMISSIONS_PROVIDED("All permissions provided"), APP_WILL_NOT_WORK_OPTIMALLY("App will not work optimally");
}

class SDKParams {
    var appId: String
    var appSecret: String
    var baseUrl: String? = null
    var link: MetaUserLinker?
    var initCb: OnInitCallback?

    constructor(
        appId: String,
        appSecret: String,
        baseUrl: String? = null,
        link: MetaUserLinker?,
        initCb: OnInitCallback?
    ) {
        this.appId = appId
        this.appSecret = appSecret
        this.baseUrl = baseUrl
        this.link = link
        this.initCb = initCb
    }

    override fun toString(): String {
        return "SDKParams(appId='$appId', appSecret='$appSecret', baseUrl=$baseUrl, link=$link, initCb=$initCb)"
    }
}