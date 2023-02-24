package com.oss.abraakadabraaapp.service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.oss.abraakadabraaapp.notifications.Notifications
import com.oss.abraakadabraaapp.utils.Constants


class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("Notification - AKD", "onMessageReceived: ${Gson().toJson(remoteMessage)}")
        Log.d("Notification - AKD", "onMessageReceived: ${Gson().toJson(remoteMessage.data)}")

        /*val title = remoteMessage.notification?.title
        val text = remoteMessage.notification?.body

        val CHANNEL_ID = "HEADS_UP_NOTIFICATION"

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                "Heads Up Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val notification: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_media_previous)
                .setAutoCancel(true)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        NotificationManagerCompat.from(this).notify(1, notification.build())

*/

        val map = remoteMessage.data

        val map2 = remoteMessage.notification

        map2?.clickAction

        if (map != null) {

            Log.d("FCM", map.toString())

            val title = map["title"]
            val body = map["body"]
            val notificationId = System.currentTimeMillis().toInt()

            val intent = Intent(Constants.notificationReceived)
            intent.putExtra(Constants.notificationReceived, Constants.notificationReceived)

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

            Notifications.notifyMessage(
                this,
                title!!,
                body!!,
                notificationId,
                map
            )

        }

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("onNewToken", "newToken $newToken")
    }

}