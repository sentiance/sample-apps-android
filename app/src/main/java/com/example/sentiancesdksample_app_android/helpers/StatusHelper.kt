package com.example.sentiancesdksample_app_android.helpers

enum class Permissions(val key: String) {
    ALWAYS("ALWAYS"), NEVER("NEVER"), WHILE_IN_USE("WHILE IN USE");
}

enum class PermissionsStatus(val key: String) {
    ALL_PERMISSIONS_PROVIDED("All permissions provided"), APP_WILL_NOT_WORK_OPTIMALLY("App will not work optimally");
}