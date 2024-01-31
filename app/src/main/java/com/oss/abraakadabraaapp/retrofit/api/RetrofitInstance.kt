package com.oss.abraakadabraaapp.retrofit.api

import android.content.Context
import com.google.android.datatransport.runtime.dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

class RetrofitInstance {

    companion object {

        private const val BASE_URL = "https://api.instantwebtools.net/v1/"

        private val retrofit by lazy{
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val getInstance by lazy {
            retrofit.create(APIs::class.java)
        }
    }


}