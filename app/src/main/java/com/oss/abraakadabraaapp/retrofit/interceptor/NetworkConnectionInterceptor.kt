package com.oss.abraakadabraaapp.retrofit.interceptor

import android.content.Context
import com.oss.abraakadabraaapp.retrofit.exception.NoConnectivityException
import com.oss.abraakadabraaapp.retrofit.utils.NetworkHelper
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor constructor(private val context: Context) : Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isNetworkConnected()){
            throw NoConnectivityException()
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

   private fun isNetworkConnected(): Boolean {
       val networkHelper = NetworkHelper(context)
       return networkHelper.isNetworkConnected()
    }
}