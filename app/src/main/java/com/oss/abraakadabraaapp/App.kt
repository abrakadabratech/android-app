package com.oss.abraakadabraaapp

import android.app.Application
import android.app.Notification
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.FirebaseApp
import com.oss.abraakadabraaapp.di.module.appModule
import com.oss.abraakadabraaapp.di.module.repoModule
import com.oss.abraakadabraaapp.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    companion object {
        const val CHANNEL_ID: String = "general_channel"
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this@App)
        createNotificationChannels()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, repoModule, viewModelModule))
        }
    }

    private fun createNotificationChannels() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channelName = "General Notifications"
        val channelDescription = "Channel For General Notifications"
        val playSound = Settings.System.DEFAULT_NOTIFICATION_URI
        val importance = NotificationManagerCompat.IMPORTANCE_HIGH
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, importance).apply {
            setName(channelName)
            setDescription(channelDescription)
            setLightColor(Color.RED)
            setLightsEnabled(true)
            setVibrationEnabled(true)
            setShowBadge(true)
            setSound(
                playSound,
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            )
        }

        NotificationManagerCompat.from(this).createNotificationChannel(channel.build())

    }


}