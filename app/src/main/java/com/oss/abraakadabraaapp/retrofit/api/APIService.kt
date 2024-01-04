package com.oss.abraakadabraaapp.retrofit.api

import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.datasource.products.GetProducts
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestsResponse
import com.oss.abraakadabraaapp.retrofit.interceptor.NetworkConnectionInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("products")
    suspend fun getProductsData(
//        @HeaderMap header: Map<String, String>,
        @Query("page") page:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        //@Query("category") categories:String,
        @Query("sortBy") sortBy:String

        ): GetProducts

    @GET("products/search")
    suspend fun searchQuery(
//        @HeaderMap header: Map<String, String>,
        @Query("page") pageNumber:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("query") page:String,
        @Query("sortBy") sortBy:String
    ): GetProducts

    @GET("product/v2/requests/product/{id}")
    suspend fun getProductRequests(
        @Path("id") id: String,
        @Query("pageNumber") page:Int
    ): RequestsResponse

    companion object {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        var okkHttp = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(loggingInterceptor)
            .addInterceptor(TokenInterceptor())
            .build()

        fun getApiService() = Retrofit.Builder()
            .baseUrl(if (BuildConfig.DEBUG) BuildConfig.BASE_URL else BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okkHttp)
            .build()
            .create(APIService::class.java)
    }
}