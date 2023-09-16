package com.oss.abraakadabraaapp.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.App
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.ProductDetailActivity
import com.oss.abraakadabraaapp.activities.RequestProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.RequesterActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.MyRequestDetailsActivity
import com.oss.abraakadabraaapp.localdb.*
import com.oss.abraakadabraaapp.utils.Constants
import org.greenrobot.eventbus.EventBus


object Notifications {

    fun notifyMessage(
        context: Context,
        notificationTitle: String,
        notificationSubtitle: String,
        notificationId: Int,
        map: HashMap<String, String>
    ) {

      /*  val noteDatabase = NotificationDatabase(context)
        val repository = NotificationRepository(noteDatabase)
        val factory = NoteViewModelFactory(repository)
        val viewModel = ViewModelProvider(context).get(NotificationViewModel::class.java);*/

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
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_))

        when (map["module"]) {
            Constants.pendingIntentRequest -> {
                var intent = Intent(context, ProductDetailActivity::class.java)

                if (map["role"] == Constants.giver) {//role //1-> taker // 2->giver
                    intent = Intent(context, RequestProductDetailActivity::class.java)
                    intent.putExtra(Constants.productId, map["module_id"])
                } else {
                    intent.putExtra(Constants.productId, map["module_data_2"])//product id
                    intent.putExtra(Constants.titleStatus, map["module_data"])
                }
                intent.putExtra(Constants.hasNotificationData, map["module_data"])
                intent.putExtra(Constants.productStatus, map["module_data"])

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                val intent = Intent(context, NewProductDetailActivity::class.java)
                intent.putExtra(Constants.productId, map["data"])
                intent.putExtra(Constants.hasNotificationData, Constants.hasNotificationData)

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            Constants.productRequestDetails -> {
                Log.d("Notification -", "notifyMessage: requesting activity")
                val intent = Intent(context, MyRequestDetailsActivity::class.java)
                intent.putExtra(Constants.productId, map["data"])
                intent.putExtra(Constants.hasNotificationData, Constants.hasNotificationData)
                val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
                stackBuilder.addNextIntentWithParentStack(intent)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            Constants.chatDetails -> {
                Log.d("Notification -", "notifyMessage: requesting activity ${map["data"]}")
                val intent = Intent(context, ChatDetailActivity::class.java)
                intent.putExtra(Constants.productId, map["data"])
                intent.putExtra(Constants.hasNotificationData, Constants.hasNotificationData)
                val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
                stackBuilder.addNextIntentWithParentStack(intent)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            Constants.productListing -> {
                Log.d("Notification -", "notifyMessage: requesting activity ${map["productId"]}")
                val intent = Intent(context, RequesterActivity::class.java)
                intent.putExtra(Constants.productId, map["data"])
                intent.putExtra(Constants.hasNotificationData, Constants.hasNotificationData)
                val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
                stackBuilder.addNextIntentWithParentStack(intent)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                val intent = Intent(context, NewHomeActivity::class.java)

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                }

                notificationBuilder.setContentIntent(pendingIntent)
            }
        }

        val notification = notificationBuilder.build()
        val manager = NotificationManagerCompat.from(context)
        manager.notify(notificationId, notification)
    }

}