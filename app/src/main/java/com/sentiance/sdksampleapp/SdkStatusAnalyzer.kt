package com.sentiance.sdksampleapp

import com.sentiance.sdk.SdkStatus

class SdkStatusAnalyzer {

    fun checkDetectionIssues(status: SdkStatus): List<String> {
        val detectionIssues = mutableListOf<String>()
        if (status.locationPermission != SdkStatus.LocationPermission.ALWAYS) {
            detectionIssues.add("Location permission is not granted.")
            // Ask the user to grant the location permission.
        }
        if (status.locationSetting == SdkStatus.LocationSetting.DISABLED) {
            detectionIssues.add("Location is disabled.")

            // Ask the user to set the location mode to high accuracy (enabling
            // both GPS and network location providers).
            // See: https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        } else if (!status.isLocationAvailable) {
            detectionIssues.add("Location is not available.")

            // The device's location has been unavailable for some time. The
            // SDK will automatically recover once locations become available.
        }
        if (status.isAirplaneModeEnabled) {
            detectionIssues.add("Airplane mode is enabled.")

            // Ask the user to disable airplane mode if possible.
        }
        if (status.isBackgroundProcessingRestricted) {
            detectionIssues.add("Background processing is restricted.")

            // On Android 9 and above, this restriction prevents an app from running
            // in the background, disabling SDK detections.

            // Ask the user to remove this restriction.
            // See: https://docs.sentiance.com/sdk/api-reference/android/sdkstatus
        }
        if (status.diskQuotaStatus == SdkStatus.QuotaStatus.EXCEEDED) {
            detectionIssues.add("Disk quota exceeded.")

            // The disk quota has been completely consumed.
            // Detections will stop until the pending SDK data is submitted
            // to clear up some quota.
            //
            // You may call Sentiance.submitDetections() to force the submission
            // and clear up some disk space. Note that calling this method will
            // bypass SDK mobile data and wifi quota limits.
        }
        // The following issues are only logged as they cannot be resolved by
        // the app or the user.
        if (!status.isRemoteEnabled) {
            detectionIssues.add("The user is disabled by the platform.")
        }
        if (!status.isGpsPresent) {
            detectionIssues.add("The device does not have a GPS.")
        }
        if (status.isGooglePlayServicesMissing) {
            detectionIssues.add("Google Play Services is missing.")

            // The device likely does not have Google Play Services. A play
            // services free version of the Sentiance SDK may be used instead.
            //
            // This may also be caused by a Google Play Services update,
            // in which case the issue will be automatically resolved.
        }

        return detectionIssues
    }

    fun checkDetectionWarnings(status: SdkStatus): ArrayList<String> {
        val detectionWarnings = ArrayList<String>()
        if (!status.isActivityRecognitionPermGranted) {
            detectionWarnings.add("Activity recognition permission has not been granted.")

            // Ask the user to grant the activity recognition permission.
            // See: https://docs.sentiance.com/sdk/getting-started/android-sdk/permissions#activity-recognition-android-10+
        }
        if (status.isBatterySavingEnabled) {
            detectionWarnings.add("Battery saving is enabled.")

            // Depending on the device, this may limit background and location tracking.
            // If possible, ask the user to disable battery saving.
        }
        if (status.isBatteryOptimizationEnabled) {
            detectionWarnings.add("OS battery optimization is enabled.")

            // This may cause detection issues on some devices.
            // See: https://docs.sentiance.com/sdk/appendix/android/android-battery-optimization
        }
        if (!status.isAccelPresent) {
            detectionWarnings.add("The device reports a lack of accelerometer.")
        }
        if (!status.isGyroPresent) {
            detectionWarnings.add("The device reports a lack of gyroscope.")
        }

        if (status.wifiQuotaStatus == SdkStatus.QuotaStatus.WARNING) {
            detectionWarnings.add("Wifi quota warning.")

            // The Wifi quota is almost consumed.
        }
        if (status.wifiQuotaStatus == SdkStatus.QuotaStatus.EXCEEDED) {
            detectionWarnings.add("Wifi quota exceeded.")

            // The Wifi quota has been completely consumed.
            // Data submission over wifi will stop until the quota clears up
            // again the next day.
        }
        if (status.mobileQuotaStatus == SdkStatus.QuotaStatus.WARNING) {
            detectionWarnings.add("Mobile data quota warning.")

            // The mobile data quota is almost consumed.
        }
        if (status.mobileQuotaStatus == SdkStatus.QuotaStatus.EXCEEDED) {
            detectionWarnings.add("Mobile data quota exceeded.")

            // The mobile data quota has been completely consumed.
            // Data submission over mobile data will stop until the quota
            // clears up again the next day.
        }
        if (status.diskQuotaStatus == SdkStatus.QuotaStatus.WARNING) {
            detectionWarnings.add("Disk quota warning.")

            // The disk quota is almost consumed.
            //
            // You may call Sentiance.submitDetections() to force the submission
            // and clear up some disk space. Note that calling this method will
            // bypass SDK mobile data and wifi quota limits.
        }

        return detectionWarnings
    }
}