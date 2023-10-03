package com.oss.abraakadabraaapp.service

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.oss.abraakadabraaapp.notifications.Notifications
import com.oss.abraakadabraaapp.utils.Constants


class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)


    }
    override fun handleIntent(data: Intent) {
        val bundle = data.extras
        Log.d("Notification TAG", "handleIntent: $bundle")
        if (bundle != null) {
            val title = bundle.getString("gcm.notification.title")
            val body = bundle.getString("body")

            val map = HashMap<String,String>()
            map["title"] = title.toString()
            map["body"] = body.toString()
            map["module"] = bundle.getString("module").toString()
            map["data"] = bundle.getString("data").toString()
            map["notificationDoc"] = bundle.getString("notificationDoc").toString()
            map["userId"] = bundle.getString("userId").toString()

            val notificationId = System.currentTimeMillis().toInt()

            val intent = Intent(Constants.notificationReceived)
            intent.putExtra(Constants.notificationReceived, Constants.notificationReceived)

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
//&& currentUserId != map["userId"]
            if(title != null && body != null ){
                Notifications.notifyMessage(
                    this,
                    title.toString(),
                    body.toString(),
                    notificationId,
                    map
                )
            }
        }

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("onNewToken", "newToken $newToken")
    }

}
