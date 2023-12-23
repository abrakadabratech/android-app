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
import com.oss.abraakadabraaapp.response.authResponse.*
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.ContentManagementResponse
import com.oss.abraakadabraaapp.response.locationResponse.LocationAddressResponse
import com.oss.abraakadabraaapp.response.mainResponse.*
import com.oss.abraakadabraaapp.response.notificationResponse.NotificationResponse
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
    suspend fun getUser(@HeaderMap header: Map<String, String>) : Response<GetUserResponse>
    @POST("v2/onboarding/user")
    suspend fun onBoardUser(@HeaderMap header: Map<String, String>,@Body body: HashMap<String, String>)
    : Response<OnBoardingResponse>

    //Logout user
    @POST("user/logout")
    suspend fun logoutUser(@HeaderMap header: Map<String, String>) : Response<LogoutResponse>

    //post User data
    @POST("user")
    suspend fun postUser(@HeaderMap header: Map<String, String>,
                         @Body map: HashMap<String, String>) : Response<CreatedUserResponse>

    //Update user data
    @PUT("user")
    suspend fun updateUser(@HeaderMap header: Map<String, String>,
                         @Body map: DataClass
    ) : Response<GetUserResponse>
    @POST("v2/update/user")
    suspend fun updateUserV2(@HeaderMap header: Map<String, String>,
                         @Body map: Map<String, String>
    ) : Response<GetUserResponse>


    //Get user social link
    @GET("user/socialprofilelink")
    suspend fun getSocialLink(@HeaderMap header: Map<String, String>) :Response<SocialProfileResponse>

    //Update user social link
    @PUT("user/socialprofilelink")
    suspend fun postSocialLink(@HeaderMap header: Map<String, String>,
                               @Body map: HashMap<String, String>) :Response<SocialProfileResponse>

    //Update user profile image
    @Multipart
    @PUT("user/profileimage")
    suspend fun updateProfilePic(
        @HeaderMap header: Map<String, String>,
        @Part filePart: MultipartBody.Part
    ): Response<UploadProfileResponse>


    //Get app support data
    @GET("app/support")
    suspend fun getSupportData(
        @HeaderMap header: Map<String, String>)
    : Response<SupportResponse>

    //Product Related end points
    //Get all products
    @GET("products")
    suspend fun getProductsData(
        @HeaderMap header: Map<String, String>,
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
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<ProductDetailsData>

    @GET("product/v2/requests/product/{id}/detail")
    suspend fun getListingDetailsV2(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<ListingResponse>

    @GET("product/requests/{id}")
    suspend fun getListingDetails(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<ListingResponse>

    //Delete product
    @DELETE("product/{id}")
    suspend fun deleteProduct(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<ProductDeleteResponse>

    //Post new product
    @Multipart
    @POST("product/new")
    suspend fun postProduct(
        @HeaderMap header: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part filePart: Array<MultipartBody.Part>,
//        @Part displayImage: MultipartBody.Part
    ): Response<PostProductResponse>

    //update product if image-url sent
    @Multipart
    @PUT("product/{id}")
    suspend fun updateProduct(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String,
//        @Query("images") body: ArrayList<String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part filePart: Array<MultipartBody.Part>

    ): Response<UpdatedProductData>

    //update product if image sent
    @Multipart
    @PUT("product/{id}")
    suspend fun updateProductIfImage(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String,
//        @Query("images") body: ArrayList<String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part filePart: Array<MultipartBody.Part>,
        @Part displayImage : MultipartBody.Part
        ): Response<UpdatedProductData>

    //Get all product categories
    @GET("product/categories")
    suspend fun getAllCategories(
        @HeaderMap header: Map<String, String>
    ): Response<AllCategoryResponse>

    //Report product
    @POST("product/report")
    suspend fun reportProduct(
        @HeaderMap header: Map<String, String>,
        @Body body: HashMap<String, String>
    ): Response<ReportProductResponse>

    //My listings
    @GET("product/mylistings")
    suspend fun getProductListings(
        @HeaderMap header: Map<String, String>
    ): Response<MyListingResponse>

    //Get my requests product
    @GET("product/myrequests")
    suspend fun getMyRequests(
        @HeaderMap header: Map<String, String>
    ): Response<MyRequestResponse>

    //Request a product
    @POST("product/request/{id}")
    suspend fun postProductRequest(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String,
        @Body body : HashMap<String, String>
    ): Response<ProductDeleteResponse>

    //Request a product request
    @PUT("product/request/{id}")
    suspend fun updateProductRequest(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String,
        @Query("status") status : String
    ): Response<ProductDeleteResponse>

    //Get product request
    @GET("product/requests/{id}")
    suspend fun getProductRequest(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<ProductDetailsData>

    //Update Product Request
    @PUT("product/requests/{id}")
    suspend fun updateProductRequest(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<ProductDetailsData>

    //Cancel product request
    @DELETE("product/request/{id}")
    suspend fun cancelProductRequest(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<CancelRequestReponse>

    //Cancel product request
    @GET("product/request/{id}")
    suspend fun getRequestor(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<RequestorResponse>

    //Request details
    @GET("product/myrequest/{id}")
    suspend fun getRequestDetails(
        @HeaderMap header: Map<String, String>,
        @Path("id") id: String
    ): Response<RequestDetails>

    //=============Payment related
    @GET("razorpay-key")
    suspend fun getRazorPay(
        @HeaderMap header: Map<String, String>
    ): Response<RazorPayModel>


    @POST("init_payment")
    suspend fun initPayment(
        @HeaderMap header: Map<String, String>,
        @Body body : HashMap<String, String>
    ): Response<InitPaymentModel>

    @POST("update_payment")
    suspend fun updatePayment(
        @HeaderMap header: Map<String, String>,
        @Body body : HashMap<String, String>
    ): Response<UpdatePaymentModel>

    @POST("product/{id}/feedback")
    suspend fun sendFeedback(
        @HeaderMap header: Map<String, String>,
        @Path("id") id:String,
        @Body body : HashMap<String, String>
    ): Response<FeedbackModel>

  @GET("products/search")
    suspend fun searchQuery(
        @HeaderMap header: Map<String, String>,
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
        @HeaderMap header: Map<String, String>,
        @Body body: Map<String, String>
    ): Response<ChatResponse>


    //For pagination
    companion object {

        private const val BASE_URL = "https://api.instantwebtools.net/v1/"

        operator fun invoke(): APIs = Retrofit.Builder()
            .baseUrl(if(BuildConfig.DEBUG) BuildConfig.BASE_URL else BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIs::class.java)
    }
    //==================================================================================//
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

    @POST("app/payment/new")
    suspend fun postUPIPayment(
        @HeaderMap header: Map<String, String>,
        @Body map: HashMap<String, String>
    ): Response<UPIModel>

    @POST("v2/create/user")
    suspend fun postUserV2(@Body body: CreateUserRequest) : Response<CreatedUserResponse>

    @POST("v2/update/fcm-token")
    suspend fun postFCMToken(@HeaderMap header: Map<String, String>,
                             @Body map: FcmRequest
    ) : Response<FcmResponse>

    @GET("app/banners")
    suspend fun getBanners(@HeaderMap header: Map<String, String>) : Response<BannerResponce>

    @POST("app/reports")
    suspend fun reportApi(@HeaderMap header: Map<String, String>,@Body body:ReportRequest) : Response<ReportResponce>



}