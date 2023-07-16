package com.oss.abraakadabraaapp.datasource

import SearchModel
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.datasource.products.GetProducts
import retrofit2.Response
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
        //@Query("category") categories:String,
        @Query("sortBy") sortBy:String

        ): GetProducts

    @GET("products/search")
    suspend fun searchQuery(
        @HeaderMap header: Map<String, String>,
        @Query("page") pageNumber:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("query") page:String,
        @Query("sortBy") sortBy:String
    ): GetProducts

    companion object {

        fun getApiService() = Retrofit.Builder()
            .baseUrl(if (BuildConfig.DEBUG) BuildConfig.BASE_URL else BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}