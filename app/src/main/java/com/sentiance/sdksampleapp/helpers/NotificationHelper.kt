package com.sentiance.sdksampleapp.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sentiance.sdksampleapp.UserCreationActivity
import com.sentiance.sdksampleapp.R

class NotificationHelper {

    companion object {
        private const val NOTIFICATION_NAME = "SentianceNotification"
        private const val NOTIFICATION_CHANNEL = "SentianceChannel"

        fun createNotification(context: Context): Notification {
            // PendingIntent that will start your application's MainActivity
            val intent = Intent(context, UserCreationActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_IMMUTABLE)

            // On Oreo and above, you must create a notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        NOTIFICATION_CHANNEL,
                        NOTIFICATION_NAME,
                        NotificationManager.IMPORTANCE_LOW
                    )
                channel.setShowBadge(false)
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle(context.getString(R.string.app_name) + " is running")
                .setContentText("Touch to open.")
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .build()
        }
    }

}