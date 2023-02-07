package com.oss.abraakadabraaapp.datasource

import com.oss.abraakadabraaapp.datasource.products.GetProducts
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface APIService {
    @GET("products")
    suspend fun getProductsData(
        @HeaderMap header: Map<String, String>,
        @Query("page") page:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("category") categories:String,

        ): GetProducts

    companion object {

        fun getApiService() = Retrofit.Builder()
            .baseUrl("https://asia-south1-abrakadabra-dev.cloudfunctions.net/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}