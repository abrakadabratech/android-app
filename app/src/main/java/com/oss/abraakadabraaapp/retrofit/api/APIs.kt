package com.oss.abraakadabraaapp.retrofit.api

import DataClass
import RequestDetails
import SearchModel
import UpdatedProductData
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.activities.newflow.apimodels.*
import com.oss.abraakadabraaapp.activities.newflow.menu.LogoutResponse
import com.oss.abraakadabraaapp.activities.newflow.menu.SupportResponse
import com.oss.abraakadabraaapp.activities.newflow.model.*
import com.oss.abraakadabraaapp.activities.newflow.requests.CancelRequestReponse
import com.oss.abraakadabraaapp.datasource.products.GetProducts
import com.oss.abraakadabraaapp.model.AccountDeleteResponse
import com.oss.abraakadabraaapp.model.DeleteAll
import com.oss.abraakadabraaapp.model.DeleteMultiple
import com.oss.abraakadabraaapp.model.NotificationResponse
import com.oss.abraakadabraaapp.model.ReadNotificationResponse
import com.oss.abraakadabraaapp.response.authResponse.*
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.ContentManagementResponse
import com.oss.abraakadabraaapp.response.locationResponse.LocationAddressResponse
import com.oss.abraakadabraaapp.response.mainResponse.*
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.MyListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.MyRequestResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestorResponse
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


@JvmSuppressWildcards
interface APIs {

    //API MVP2.0
    //=================================================================================//

    //Deprecated
    @GET("user")
    suspend fun getUser() : Response<GetUserResponse>
    @POST("v2/onboarding/user")
    suspend fun onBoardUser(@Body body: HashMap<String, String>)
    : Response<OnBoardingResponse>

    //Logout user
    @POST("user/logout")
    suspend fun logoutUser() : Response<LogoutResponse>

    //post User data
    @POST("user")
    suspend fun postUser(@Body map: HashMap<String, String>) : Response<CreatedUserResponse>

    //Update user data
    @PUT("user")
    suspend fun updateUser(@Body map: DataClass
    ) : Response<GetUserResponse>
    @POST("v2/update/user")
    suspend fun updateUserV2(@Body map: Map<String, String>
    ) : Response<GetUserResponse>


    //Get user social link
    @GET("user/socialprofilelink")
    suspend fun getSocialLink() :Response<SocialProfileResponse>

    //Update user social link
    @PUT("user/socialprofilelink")
    suspend fun postSocialLink(@Body map: HashMap<String, String>) :Response<SocialProfileResponse>

    //Update user profile image
    @Multipart
    @PUT("user/profileimage")
    suspend fun updateProfilePic(

        @Part filePart: MultipartBody.Part
    ): Response<UploadProfileResponse>


    //Get app support data
    @GET("app/support")
    suspend fun getSupportData()
    : Response<SupportResponse>

    //Product Related end points
    //Get all products
    @GET("products")
    suspend fun getProductsData(

        @Query("page") page:Int,
        @Query("maxDistance") maxDistance:Int,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("category") category:String,
        @Query("pageSize") size:String,
        @Query("sortBy") sortBy:String): Response<GetProducts>

    //Get product when click on card (single product)
    @GET("product/{id}")
    suspend fun getProductDetails(

        @Path("id") id: String
    ): Response<ProductDetailsData>

    @GET("product/v2/requests/product/{id}/detail")
    suspend fun getListingDetailsV2(

        @Path("id") id: String
    ): Response<ListingResponse>

    @GET("product/requests/{id}")
    suspend fun getListingDetails(

        @Path("id") id: String
    ): Response<ListingResponse>

    //Delete product
    @DELETE("product/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): Response<ProductDeleteResponse>

    //Post new product
    @Multipart
    @POST("product/new")
    suspend fun postProduct(

        @PartMap partMap: Map<String, RequestBody>,
        @Part filePart: Array<MultipartBody.Part>,
//        @Part displayImage: MultipartBody.Part
    ): Response<PostProductResponse>

    //update product if image-url sent
    @Multipart
    @PUT("product/{id}")
    suspend fun updateProduct(

        @Path("id") id: String,
//        @Query("images") body: ArrayList<String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part filePart: Array<MultipartBody.Part>

    ): Response<UpdatedProductData>

    //update product if image sent
    @Multipart
    @PUT("product/{id}")
    suspend fun updateProductIfImage(

        @Path("id") id: String,
//        @Query("images") body: ArrayList<String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part filePart: Array<MultipartBody.Part>,
        @Part displayImage : MultipartBody.Part
        ): Response<UpdatedProductData>

    //Get all product categories
    @GET("product/categories")
    suspend fun getAllCategories(
//        @HeaderMap header: Map<String, String>
    ): Response<AllCategoryResponse>

    //Report product
    @POST("product/report")
    suspend fun reportProduct(

        @Body body: HashMap<String, String>
    ): Response<ReportProductResponse>

    //My listings
    @GET("product/mylistings")
    suspend fun getProductListings(): Response<MyListingResponse>

    //Get my requests product
    @GET("product/myrequests")
    suspend fun getMyRequests(): Response<MyRequestResponse>

    //Request a product
    @POST("product/request/{id}")
    suspend fun postProductRequest(
        @Header("app_version") app_version:Int,
        @Path("id") id: String,
        @Body body : HashMap<String, String>
    ): Response<ProductDeleteResponse>

    //Request a product request
    @PUT("product/request/{id}")
    suspend fun updateProductRequest(

        @Path("id") id: String,
        @Query("status") status : String
    ): Response<ProductDeleteResponse>

    //Get product request
    @GET("product/requests/{id}")
    suspend fun getProductRequest(

        @Path("id") id: String
    ): Response<ProductDetailsData>

    //Update Product Request
    @PUT("product/requests/{id}")
    suspend fun updateProductRequest(

        @Path("id") id: String
    ): Response<ProductDetailsData>

    //Cancel product request
    @DELETE("product/request/{id}")
    suspend fun cancelProductRequest(

        @Path("id") id: String
    ): Response<CancelRequestReponse>

    //Cancel product request
    @GET("product/request/{id}")
    suspend fun getRequestor(

        @Path("id") id: String
    ): Response<RequestorResponse>

    //Request details
    @GET("product/myrequest/{id}")
    suspend fun getRequestDetails(

        @Path("id") id: String
    ): Response<RequestDetails>

    //=============Payment related
    @GET("razorpay-key")
    suspend fun getRazorPay(): Response<RazorPayModel>


    @POST("init_payment")
    suspend fun initPayment(
        @Body body : HashMap<String, String>
    ): Response<InitPaymentModel>

    @POST("update_payment")
    suspend fun updatePayment(
        @Body body : HashMap<String, String>
    ): Response<UpdatePaymentModel>

    @POST("product/{id}/feedback")
    suspend fun sendFeedback(
        @Path("id") id:String,
        @Body body : HashMap<String, String>
    ): Response<FeedbackModel>

  @GET("products/search")
    suspend fun searchQuery(
        @Query("query") page:String,
        @Query("lat") lat:Double,
        @Query("long") long:Double,
        @Query("maxDistance") maxDistance:Int,
        @Query("page") pageNumber:Int,
        @Query("pageSize") size:String,
        @Query("sortBy") sortBy:String
    ): Response<SearchModel>

    @POST("product/chats/send-notification")
    suspend fun sendNotification(
        @Body body: Map<String, String>
    ): Response<ChatResponse>


    //For pagination
    companion object {

        private const val BASE_URL = "https://api.instantwebtools.net/v1/"
        var okkHttp = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(APIService.loggingInterceptor)
            .addInterceptor(TokenInterceptor())
            .build()
        operator fun invoke(): APIs = Retrofit.Builder()
            .baseUrl(if(BuildConfig.DEBUG) BuildConfig.BASE_URL else BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okkHttp)
            .build()
            .create(APIs::class.java)
    }
    //==================================================================================//
    @GET
    suspend fun getAddress(@Url url: String): Response<LocationAddressResponse>

    @GET("user_login")
    suspend fun loginWithPhoneNumber(
        @QueryMap map: HashMap<String, String>
    ): Response<SignInResponse>

    @GET("user_login")
    suspend fun signIn(
        @QueryMap map: HashMap<String, String>
    ): Response<SignInResponse>

    @GET("change_password")
    suspend fun resetPassword(
        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("new_register")
    suspend fun signUp(

        @QueryMap map: HashMap<String, String>
    ): Response<SignUpResponse>

    @GET("otp_verification")
    suspend fun otpVerification(

        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("send_otp")
    suspend fun sendOtp(

        @QueryMap map: HashMap<String, String>
    ): Response<SendOtpResponse>

    @GET("profile_change_password")
    suspend fun changePassword(

        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("logout")
    suspend fun logout(

        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("update_profile")
    suspend fun updateUserProfile(

        @QueryMap partMap: Map<String, String>
    ): Response<CommonResponse>

    @Multipart
    @POST("update_profile_image")
    suspend fun updateProfileImage(

        @PartMap partMap: Map<String, RequestBody>,
        @Part file: MultipartBody.Part
    ): Response<CommonResponse>

    @GET("profile")
    suspend fun getUserProfile(

        @QueryMap partMap: Map<String, String>,
    ): Response<GetUserProfile>

    @GET("content_management_system")
    suspend fun contentManagementSystem(

        @QueryMap map: HashMap<String, String>
    ): Response<ContentManagementResponse>

    @GET("get_all_category")
    suspend fun getCategory(
        @HeaderMap header: Map<String, String>
    ): Response<GetCategoryResponse>

    @GET("user_dashboard")
    suspend fun getHomeData(

        @QueryMap map: HashMap<String, String>
    ): Response<GetHomeDataResponse>


    @Multipart
    @POST("product_manage")
    suspend fun manageProduct(

        @PartMap partMap: Map<String, RequestBody>,
        @Part productImages: Array<MultipartBody.Part>
    ): Response<CommonResponse>

    @GET("product_list")
    suspend fun getMyProductList(

        @QueryMap map: HashMap<String, String>
    ): Response<GetProductListResponse>

    @GET("delete_product")
    suspend fun deleteProduct(

        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("cancel_product_request")
    suspend fun cancelProduct(

        @QueryMap map: HashMap<String, String>
    ): Response<CommonResponse>

    @GET("product_detail")
    suspend fun getProductDetail(

        @QueryMap map: HashMap<String, String>
    ): Response<GetProductDetailResponse>

    @GET("make_an_request")
    suspend fun makeARequest(

        @QueryMap map: HashMap<String, String>
    ): Response<MakeRequestResponse>

    @GET("requests_tab")
    suspend fun getGiverList(

        @QueryMap map: HashMap<String, String>
    ): Response<GetGiverListResponse>

    @GET("requests_tab")
    suspend fun getReceiverList(

        @QueryMap map: HashMap<String, String>
    ): Response<GetReceiverListResponse>

    @GET("search")
    suspend fun getSearchList(

        @QueryMap map: HashMap<String, String>
    ): Response<GetSearchListResponse>

    @GET("product_request_data")
    suspend fun getRequestProductDetail(

        @QueryMap map: HashMap<String, String>
    ): Response<GetRequestProductDetailResponse>

    @GET("request_action")
    suspend fun requestAction(

        @QueryMap map: HashMap<String, String>
    ): Response<RequestActionResponse>

    @POST("app/payment/new")
    suspend fun postUPIPayment(

        @Body map: HashMap<String, String>
    ): Response<UPIModel>

    @POST("v2/create/user")
    suspend fun postUserV2(@Body body: CreateUserRequest) : Response<CreatedUserResponse>

    @POST("v2/update/fcm-token")
    suspend fun postFCMToken(
                             @Body map: FcmRequest
    ) : Response<FcmResponse>

    @GET("app/banners")
    suspend fun getBanners() : Response<BannerResponce>

    @POST("app/reports")
    suspend fun reportApi(@Body body:ReportRequest) : Response<ReportResponce>

    @GET("product/request/verify")
    suspend fun requestRemains(@Header("app_version") app_version:Int) : Response<RequestsRemain>

    @GET("app/user/notifications")
    suspend fun getAllNotifications(@Query("page") page: Int) : Response<NotificationResponse>

    @PUT("app/user/notifications/read")
    suspend fun readAllNotification(@Body body: DeleteAll) : Response<ReadNotificationResponse>


    @PUT("app/user/notifications/read")
    suspend fun readMultipleNotification(@Body body: DeleteMultiple) : Response<ReadNotificationResponse>

    @DELETE("app/user/notifications/delete")
    suspend fun deleteAllNotification(@Body all: DeleteAll) : Response<ReadNotificationResponse>

    @DELETE("app/user/notifications/delete")
    suspend fun deleteMultipleNotification(@Body multiple: DeleteMultiple) : Response<ReadNotificationResponse>

    @POST("user/account/delete")
    suspend fun deleteUserAccount() : Response<AccountDeleteResponse>

}