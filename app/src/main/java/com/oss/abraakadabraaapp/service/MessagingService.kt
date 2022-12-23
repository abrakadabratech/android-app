package com.oss.abraakadabraaapp.service

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oss.abraakadabraaapp.notifications.Notifications
import com.oss.abraakadabraaapp.utils.Constants

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val map = remoteMessage.data

        if (map.isNotEmpty()) {

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