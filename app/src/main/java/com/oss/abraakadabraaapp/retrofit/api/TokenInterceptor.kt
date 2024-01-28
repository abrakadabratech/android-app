package com.oss.abraakadabraaapp.retrofit.api


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class TokenInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        try {
            if (request.url.encodedPath.contains("v2/create/user") && request.method == "POST") {
                return chain.proceed(request)
            }

            val user = FirebaseAuth.getInstance().currentUser ?: return chain.proceed(request)
            val task: Task<GetTokenResult> = user.getIdToken(false)
            val tokenResult = Tasks.await(task, 20, TimeUnit.SECONDS)
            val token = tokenResult.token

            Log.w("OkHttp", "intercept Token: $token", )
            request = request.newBuilder().addHeader("Authorization", "Bearer $token").build()

            return chain.proceed(request)
        } catch (e: Exception) {
            return chain.proceed(request)
        }
    }
}