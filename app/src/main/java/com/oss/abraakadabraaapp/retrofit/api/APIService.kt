package com.oss.abraakadabraaapp.retrofit.api

import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.datasource.products.GetProducts
import com.oss.abraakadabraaapp.model.BlockedUsersListResponse
import com.oss.abraakadabraaapp.model.BuyerListResponse
import com.oss.abraakadabraaapp.model.NotificationResponse
import com.oss.abraakadabraaapp.model.SellerChatListResponse
import com.oss.abraakadabraaapp.model.SellersListReponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @GET("products")
    suspend fun getProductsData(
        @Query("page") page:Int,
//        @Query("pageSize") pageSize:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("sortBy") sortBy:String,
        @Query("type") type:String

    ): Response<GetProducts>

    @GET("products/search")
    suspend fun searchQuery(
//        @HeaderMap header: Map<String, String>,
        @Query("page") pageNumber:Int,
//        @Query("pageSize") pageSize:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("query") page:String,
        @Query("sortBy") sortBy:String
    ): Response<GetProducts>

    @GET("product/v2/requests/product/{id}")
    suspend fun getProductRequests(
        @Path("id") id: String,
        @Query("pageNumber") page:Int
    ): Response<RequestsResponse>

    @GET("app/user/notifications")
    suspend fun getAllNotifications(@Query("page") page: Int) : Response<NotificationResponse>


    @GET("users/giver/chats/products")
    suspend fun getSellerProductsList(@Query("pageNumber") page: Int,@Query("pageSize") pageSize: Int)
    : Response<SellersListReponse>

    @GET("users/receiver/chats/list")
    suspend fun getBuyerProductsList(@Query("pageNumber") page: Int,@Query("pageSize") pageSize: Int)
            : Response<BuyerListResponse>

    @GET("users/giver/chats/product/{id}")
    suspend fun getSellerChatList(@Path("id") id:String,@Query("pageNumber") page: Int,@Query("pageSize") pageSize: Int)
            : Response<SellerChatListResponse>

    @GET("user/list/blocked-users")
    suspend fun getBlockerUsers(@Query("pageNumber") page: Int,@Query("pageSize") pageSize: Int)
            : Response<BlockedUsersListResponse>

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

        fun github() = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIService::class.java)
    }
}