package com.oss.abraakadabraaapp.service

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.newflow.model.BroadCastNotificationModel
import com.oss.abraakadabraaapp.activities.newflow.model.NotificationDataModel
import com.oss.abraakadabraaapp.notifications.Notifications
import com.oss.abraakadabraaapp.utils.Constants


class MessagingService : FirebaseMessagingService() {

    override fun handleIntent(data: Intent) {
        val bundle = data.extras
        Log.d("Notification TAG", "handleIntent: $bundle")
        if (bundle != null) {
            val title = bundle.getString("gcm.notification.title")
            val body = bundle.getString("body")

            val broadcastTitle = bundle.getString("title")

            Log.d("Notification TAG", "handleIntent: ${bundle.getString("data").toString()}")


            val dataModel = Gson().fromJson(bundle.getString("data").toString(),
                BroadCastNotificationModel::class.java)

            val map = HashMap<String,String>()
            map["title"] = title.toString()
            map["body"] = body.toString()
            map["module"] = bundle.getString("module").toString()
            map["data"] = bundle.getString("data").toString()

            val notificationId = System.currentTimeMillis().toInt()

            val intent = Intent(Constants.notificationReceived)
            intent.putExtra(Constants.notificationReceived, Constants.notificationReceived)

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if(title != null && body != null ){
                Notifications.notifyMessage(
                    this,
                    title.toString(),
                    body.toString(),
                    notificationId,
                    map
                )
            }else if(broadcastTitle != null && dataModel.user_id != currentUserId){
                val map2 = HashMap<String,String>()
                map2["title"] = broadcastTitle
                map2["body"] = bundle.getString("body").toString()
                map2["module"] = bundle.getString("module").toString()
                val d = NotificationDataModel(chatNode = "",
                    user_id = dataModel.user_id, requestId = "",product_id = dataModel.product_id,notificationDoc = "")

                map2["data"] = Gson().toJson(d)

                Notifications.notifyMessage(
                    this,
                    broadcastTitle,
                    bundle.getString("body").toString(),
                    notificationId,
                    map2
                )
            }
        }

    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d("onNewToken", "newToken $newToken")
    }

}
