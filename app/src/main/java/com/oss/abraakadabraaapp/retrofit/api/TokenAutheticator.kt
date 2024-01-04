package com.oss.abraakadabraaapp.retrofit.api

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAutheticator @Inject constructor(
    private val context: Context
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        if (!response.isSuccessful) {
            /*val localBroadcastManager = LocalBroadcastManager.getInstance(context)
            val intent = Intent(Keys.ACTION_LOGOUT)
            localBroadcastManager.sendBroadcast(intent)*/
            Log.w("OkHttp", "authenticate: error:", )
            return null
        }
        Log.w("OkHttp", "authenticate: success", )
        return response.request.newBuilder().build()
    }
}