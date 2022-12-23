package com.oss.abraakadabraaapp.retrofit.api

import com.oss.abraakadabraaapp.response.authResponse.*
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.ContentManagementResponse
import com.oss.abraakadabraaapp.response.locationResponse.LocationAddressResponse
import com.oss.abraakadabraaapp.response.mainResponse.*
import com.oss.abraakadabraaapp.response.notificationResponse.NotificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


@JvmSuppressWildcards
interface APIs {

    @GET
    suspend fun getAddress(@Url url: String): Response<LocationAddressResponse>

    @GET("user_login")
    suspend fun loginWithPhoneNumber(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<SignInResponse>

    @GET("user_login")
    suspend fun signIn(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<SignInResponse>

    @GET("change_password")
    suspend fun resetPassword(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("new_register")
    suspend fun signUp(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<SignUpResponse>

    @GET("otp_verification")
    suspend fun otpVerification(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("send_otp")
    suspend fun sendOtp(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<SendOtpResponse>

    @GET("profile_change_password")
    suspend fun changePassword(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("logout")
    suspend fun logout(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("update_profile")
    suspend fun updateUserProfile(
        @HeaderMap header: Map<String, String>,
        @QueryMap partMap: Map<String, String>
    ): Response<CommonResponse>

    @Multipart
    @POST("update_profile_image")
    suspend fun updateProfileImage(
        @HeaderMap header: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part file: MultipartBody.Part
    ): Response<CommonResponse>

    @GET("profile")
    suspend fun getUserProfile(
        @HeaderMap header: Map<String, String>,
        @QueryMap partMap: Map<String, String>,
    ): Response<GetUserProfile>

    @GET("content_management_system")
    suspend fun contentManagementSystem(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<ContentManagementResponse>

    @GET("get_all_category")
    suspend fun getCategory(
        @HeaderMap header: Map<String, String>
    ): Response<GetCategoryResponse>

    @GET("user_dashboard")
    suspend fun getHomeData(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetHomeDataResponse>

    @GET("get_all_notifications")
    suspend fun getAllNotifications(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<NotificationResponse>

    @GET("read_notification")
    suspend fun readNotification(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @Multipart
    @POST("product_manage")
    suspend fun manageProduct(
        @HeaderMap header: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part productImages: Array<MultipartBody.Part>
    ): Response<CommonResponse>

    @GET("product_list")
    suspend fun getMyProductList(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetProductListResponse>

    @GET("delete_product")
    suspend fun deleteProduct(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("cancel_product_request")
    suspend fun cancelProduct(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("product_detail")
    suspend fun getProductDetail(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetProductDetailResponse>

    @GET("make_an_request")
    suspend fun makeARequest(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<MakeRequestResponse>

    @GET("requests_tab")
    suspend fun getGiverList(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetGiverListResponse>

    @GET("requests_tab")
    suspend fun getReceiverList(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetReceiverListResponse>

    @GET("search")
    suspend fun getSearchList(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetSearchListResponse>

    @GET("product_request_data")
    suspend fun getRequestProductDetail(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<GetRequestProductDetailResponse>

    @GET("request_action")
    suspend fun requestAction(
        @HeaderMap header: Map<String, String>,
        @QueryMap map: HashMap<String, String>
    ): Response<RequestActionResponse>

}