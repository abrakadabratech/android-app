package com.oss.abraakadabraaapp.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.App
import com.oss.abraakadabraaapp.activities.ProductDetailActivity
import com.oss.abraakadabraaapp.activities.RequestProductDetailActivity
import com.oss.abraakadabraaapp.utils.Constants
import android.os.Build




object Notifications {

    fun notifyMessage(
        context: Context,
        notificationTitle: String,
        notificationSubtitle: String,
        notificationId: Int,
        map: MutableMap<String, String>
    ) {

        val color = ContextCompat.getColor(context, R.color.theme_color)

        val notificationBuilder = NotificationCompat.Builder(
            context,
            App.CHANNEL_ID
        )
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(notificationTitle)
            .setContentText(notificationSubtitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setColor(color)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))

        when (map["module"]) {
            Constants.pendingIntentRequest -> {
                var intent = Intent(context, ProductDetailActivity::class.java)

                if(map["role"] == Constants.giver){//role //1-> taker // 2->giver
                    intent = Intent(context, RequestProductDetailActivity::class.java)
                    intent.putExtra(Constants.productId, map["module_id"])
                }else{
                    intent.putExtra(Constants.productId, map["module_data_2"])//product id
                    intent.putExtra(Constants.titleStatus, map["module_data"])
                }
                intent.putExtra(Constants.hasNotificationData, map["module_data"])
                intent.putExtra(Constants.productStatus, map["module_data"])

                val pendingIntent =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                notificationBuilder.setContentIntent(pendingIntent)
            }
            Constants.productDetail -> {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra(Constants.productId, map["module_id"])
                intent.putExtra(Constants.hasNotificationData, Constants.hasNotificationData)

                val pendingIntent =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                notificationBuilder.setContentIntent(pendingIntent)
            }
            else -> {
                /*val intent = Intent(context, HomeActivity::class.java)

                val pendingIntent =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        context,
                        0, intent, PendingIntent.FLAG_ONE_SHOT
                    )
                }*/

//                notificationBuilder.setContentIntent(pendingIntent)
            }
        }

        val notification = notificationBuilder.build()
        val manager = NotificationManagerCompat.from(context)
        manager.notify(notificationId, notification)
    }

}