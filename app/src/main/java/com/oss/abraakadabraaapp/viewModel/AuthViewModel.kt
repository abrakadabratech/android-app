package com.oss.abraakadabraaapp.viewModel

import DataClass
import RequestDetails
import SearchModel
import UpdatedProductData
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.newflow.apimodels.*
import com.oss.abraakadabraaapp.activities.newflow.menu.LogoutResponse
import com.oss.abraakadabraaapp.activities.newflow.menu.SupportResponse
import com.oss.abraakadabraaapp.activities.newflow.model.*
import com.oss.abraakadabraaapp.activities.newflow.requests.CancelRequestReponse
import com.oss.abraakadabraaapp.datasource.products.GetProducts
import com.oss.abraakadabraaapp.model.AccountDeleteResponse
import com.oss.abraakadabraaapp.model.BlockedResponse
import com.oss.abraakadabraaapp.model.DeleteAll
import com.oss.abraakadabraaapp.model.DeleteMultiple
import com.oss.abraakadabraaapp.model.InitChatResponce
import com.oss.abraakadabraaapp.model.ReadNotificationResponse
import com.oss.abraakadabraaapp.model.UserChatBlock
import com.oss.abraakadabraaapp.model.UserProfile
import com.oss.abraakadabraaapp.response.authResponse.*
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.HttpErrorResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.MyListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.MyRequestResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestorResponse
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.retrofit.api.CallHelper
import com.oss.abraakadabraaapp.retrofit.api.callApi
import com.oss.abraakadabraaapp.retrofit.repository.AuthRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var errorMessage = MutableLiveData("")
    var isLoading = MutableLiveData(false)
    var unAuthorization = MutableLiveData(false)

    var getUserSuccess = MutableLiveData<GetUserResponse>()
    var logoutNewSuccess = MutableLiveData<LogoutResponse>()
    var postUserSuccess = MutableLiveData<CreatedUserResponse>()
    var createUserSuccess = MutableLiveData<CreatedUserResponse>()
    var updateUserSuccess = MutableLiveData<GetUserResponse>()
    var fcmSuccess = MutableLiveData<FcmResponse>()
    var userProfilePicSuccess = MutableLiveData<UploadProfileResponse>()
    var postProductSuccess = MutableLiveData<PostProductResponse>()

    var getSocialProfileSuccess = MutableLiveData<SocialProfileResponse>()
    var postSocialProfileSuccess = MutableLiveData<SocialProfileResponse>()


    var loginWithPhoneNumberSuccess = MutableLiveData<SignInResponse>()
    var changePasswordSuccess = MutableLiveData<CommonResponse>()
    var postUPIPaymentSuccess = MutableLiveData<UPIModel>()

    var productDetailsData = MutableLiveData<ProductDetailsData>()
    var listingDetailsuccess = MutableLiveData<ListingResponse>()
    var requestDetailsuccess = MutableLiveData<RequestDetails>()
    var deleteProductSuccess = MutableLiveData<ProductDeleteResponse>()
    var updateProductSuccess = MutableLiveData<UpdatedProductData>()
    var requstProductSuccess = MutableLiveData<ProductDeleteResponse>()
    var updateProductRequest = MutableLiveData<ProductDeleteResponse>()
    var cancelRequestSuccess = MutableLiveData<CancelRequestReponse>()
    var getRequestorSuccess = MutableLiveData<RequestorResponse>()
    var getProductListingsSuccess = MutableLiveData<MyListingResponse>()
    var getMyRequestsSuccess = MutableLiveData<MyRequestResponse>()
    var initPaymentSuccess = MutableLiveData<InitPaymentModel>()
    var razorpaySuccess = MutableLiveData<RazorPayModel>()
    var updatePaymentSuccess = MutableLiveData<UpdatePaymentModel>()
    var sendFeedbackSuccess = MutableLiveData<FeedbackModel>()
    var searchSuccess = MutableLiveData<SearchModel>()
    var chatSuccess = MutableLiveData<ChatResponse>()
    var supportDataSuccess  = MutableLiveData<SupportResponse>()
    var allproductsSuccess  = MutableLiveData<GetProducts>()
    var getAllcategoriesSuccess  = MutableLiveData<AllCategoryResponse>()
    var reportProductSuccess  = MutableLiveData<ReportProductResponse>()


    fun getUser() {
        viewModelScope.launch {

            suspend fun call() = repository.getUser()

            callApi(::call, object : CallHelper<GetUserResponse> {
                override fun onSuccessful(data: GetUserResponse) {
                    getUserSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false

        }
    }

    var onBoardingResponse = MutableLiveData<OnBoardingResponse>()
    fun onBoardUser(
        headerMap: HashMap<String, String> , body: HashMap<String, String>) {
        viewModelScope.launch {

            suspend fun call() = repository.onBoardUser(headerMap,body)

            callApi(::call, object : CallHelper<OnBoardingResponse> {
                override fun onSuccessful(data: OnBoardingResponse) {
                    onBoardingResponse.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false

        }
    }

    //NEW
    fun logoutUser(headerMap: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.logoutUser(headerMap)

            callApi(::call, object : CallHelper<LogoutResponse> {
                override fun onSuccessful(data: LogoutResponse) {
                    logoutNewSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun postUser(
        headerMap: HashMap<String, String>,bodyMap: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.postUser(headerMap,bodyMap)

            callApi(::call, object : CallHelper<CreatedUserResponse> {
                override fun onSuccessful(data: CreatedUserResponse) {
                    postUserSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun postUserV2(
        body: CreateUserRequest) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.postUserV2(body)

            callApi(::call, object : CallHelper<CreatedUserResponse> {
                override fun onSuccessful(data: CreatedUserResponse) {
                    createUserSuccess.value = data
                    Log.d("TAG::", "onSuccess: firebase message ${data}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun updateUser(
        headerMap: HashMap<String, String>, bodyMap: DataClass
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateUser(headerMap,bodyMap)

            callApi(::call, object : CallHelper<GetUserResponse> {
                override fun onSuccessful(data: GetUserResponse) {
                    updateUserSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun updateUserV2( bodyMap: Map<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateUserV2(bodyMap)

            callApi(::call, object : CallHelper<GetUserResponse> {
                override fun onSuccessful(data: GetUserResponse) {
                    updateUserSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun postFCMToken(bodyMap: FcmRequest) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.postFCMToken(bodyMap)

            callApi(::call, object : CallHelper<FcmResponse> {
                override fun onSuccessful(data: FcmResponse) {
                    fcmSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun postFCMToken(
        headerMap: HashMap<String, String>, bodyMap: DataClass
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateUser(headerMap,bodyMap)

            callApi(::call, object : CallHelper<GetUserResponse> {
                override fun onSuccessful(data: GetUserResponse) {
                    updateUserSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }


    fun getUserSocialProfile(
        headerMap: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getSocialLink(headerMap)

            callApi(::call, object : CallHelper<SocialProfileResponse> {
                override fun onSuccessful(data: SocialProfileResponse) {
                    getSocialProfileSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun postUserSocialProfile(
        headerMap: HashMap<String, String>, bodyMap: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.postSocialLink(headerMap,bodyMap)

            callApi(::call, object : CallHelper<SocialProfileResponse> {
                override fun onSuccessful(data: SocialProfileResponse) {
                    postSocialProfileSuccess
                        .value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun updateProfilePic(
        headerMap: HashMap<String, String>,file: MultipartBody.Part) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateProfilePic(headerMap,file)

            callApi(::call, object : CallHelper<UploadProfileResponse> {
                override fun onSuccessful(data: UploadProfileResponse) {
                    userProfilePicSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun postProduct(
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.postProduct(body,file)

            callApi(::call, object : CallHelper<PostProductResponse> {
                override fun onSuccessful(data: PostProductResponse) {
                    postProductSuccess.value = data
//                    Log.d("TAG::", "onSuccess: firebase message ${data.message}")
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    fun getProductDetails(
        headerMap: HashMap<String, String>,
        id: String
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.ProductDetails(headerMap,id)
            callApi(::call, object : CallHelper<ProductDetailsData>{
                override fun onSuccessful(data: ProductDetailsData) {
                    productDetailsData.postValue(data)
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false

        }
    }

    fun getListingDetails(
        headerMap: HashMap<String, String>,
        id: String
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.getListingDetails(headerMap,id)
            callApi(::call, object : CallHelper<ListingResponse>{
                override fun onSuccessful(data: ListingResponse) {
                    listingDetailsuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }
    var listingDetailV2success = MutableLiveData<ListingResponse>()
    fun getListingDetailsV2(
        headerMap: HashMap<String, String>,
        id: String
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.getListingDetailsV2(headerMap,id)
            callApi(::call, object : CallHelper<ListingResponse>{
                override fun onSuccessful(data: ListingResponse) {
                    listingDetailV2success.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false

        }
    }
fun getRequestDetails(
        headerMap: HashMap<String, String>,
        id: String
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.getRequestDetails(headerMap,id)
            callApi(::call, object : CallHelper<RequestDetails>{
                override fun onSuccessful(data: RequestDetails) {
                    requestDetailsuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false

        }
    }

    //NEW - Delete product
    fun deleteProduct(
        id: String
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.deleteProduct(id)
            callApi(::call, object : CallHelper<ProductDeleteResponse>{
                override fun onSuccessful(data: ProductDeleteResponse) {
                    deleteProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }

    fun updateProduct(
        map: HashMap<String, String>,
        id: String,
        images:ArrayList<String>,
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.updateProduct(map,id,images,body,file)
            callApi(::call, object : CallHelper<UpdatedProductData>{
                override fun onSuccessful(data: UpdatedProductData) {
                    updateProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }

    fun updateProductIfImage(
        map: HashMap<String, String>,
        id: String,
        images:ArrayList<String>,
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>,
        displayImage : MultipartBody.Part
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.updateProductIfImage(map,id,images,body,file,displayImage)
            callApi(::call, object : CallHelper<UpdatedProductData>{
                override fun onSuccessful(data: UpdatedProductData) {
                    updateProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }

    fun postProductRequest(version : Int,
        id: String,
        body:HashMap<String, String>
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.postRequest(version,id,body)
            callApi(::call, object : CallHelper<ProductDeleteResponse>{
                override fun onSuccessful(data: ProductDeleteResponse) {
                    requstProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false
        }
    }

    fun updateProductRequest(
        id: String,
        body:String
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.updateProductRequest(id,body)
            callApi(::call, object : CallHelper<ProductDeleteResponse>{
                override fun onSuccessful(data: ProductDeleteResponse) {
                    updateProductRequest.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }

    fun cancelProductRequest(
        id: String
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.cancelProductRequest(id)
            callApi(::call, object : CallHelper<CancelRequestReponse>{
                override fun onSuccessful(data: CancelRequestReponse) {
                    cancelRequestSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }

    fun getRequestor(
        headerMap: HashMap<String, String>,
        id: String
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.getRequestor(headerMap,id)
            callApi(::call, object : CallHelper<RequestorResponse>{
                override fun onSuccessful(data: RequestorResponse) {
                    getRequestorSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }


    fun getProductListings(
        headerMap: HashMap<String, String>
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.getProductListing(headerMap)
            callApi(::call, object : CallHelper<MyListingResponse>{
                override fun onSuccessful(data: MyListingResponse) {
                    getProductListingsSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false
        }
    }

    fun getMyRequests(
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.getMyRequests()
            callApi(::call, object : CallHelper<MyRequestResponse>{
                override fun onSuccessful(data: MyRequestResponse) {
                    getMyRequestsSuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
//            isLoading.value = false
        }
    }

    fun getRazorPay(
        headerMap: HashMap<String, String>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.getRazorPay(headerMap)
            callApi(::call, object : CallHelper<RazorPayModel>{
                override fun onSuccessful(data: RazorPayModel) {
                    razorpaySuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
            isLoading.value = false
        }
    }

    fun initPayment(body:HashMap<String, String>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.initPayment(body)
            callApi(::call, object : CallHelper<InitPaymentModel>{
                override fun onSuccessful(data: InitPaymentModel) {
                    initPaymentSuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
            isLoading.value = false
        }
    }

    fun updatePayment(body:HashMap<String, String>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.updatePayment(body)
            callApi(::call, object : CallHelper<UpdatePaymentModel>{
                override fun onSuccessful(data: UpdatePaymentModel) {
                    updatePaymentSuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
            isLoading.value = false
        }
    }

    fun sendFeedback(
        id: String, body:HashMap<String, String>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.sendFeedback(id,body)
            callApi(::call, object : CallHelper<FeedbackModel>{
                override fun onSuccessful(data: FeedbackModel) {
                    sendFeedbackSuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
            isLoading.value = false
        }
    }

    fun searchQuery(
        page:String,
        lat:Double,long:Double,maxDistance:Int,pageNumber:Int,sortBy: String
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.searchQuery(page,lat,long,maxDistance,pageNumber,sortBy)
            callApi(::call, object : CallHelper<SearchModel>{
                override fun onSuccessful(data: SearchModel) {
                    Log.e("okk", "onSuccessful: $data", )
                    searchSuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
//            isLoading.value = false
        }
    }

    fun sendNotification(
        body: HashMap<String, String>) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.sendNotification(body)
            callApi(::call, object : CallHelper<ChatResponse>{
                override fun onSuccessful(data: ChatResponse) {
                    chatSuccess .value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })
//            isLoading.value = false
        }
    }

    fun getSupportData(
        headerMap: HashMap<String, String>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.getSupportData(headerMap)
            callApi(::call, object : CallHelper<SupportResponse>{
                override fun onSuccessful(data: SupportResponse) {
                    supportDataSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false
        }
    }

    fun getProductsData(
        headerMap: HashMap<String, String>,page:Int,maxDistance:Int,lat:Double,lang:Double,category:String,sortBy:String
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() =
                repository.getProductsData(headerMap, page, maxDistance, lat, lang, category.dropLast(1),sortBy)
            callApi(::call, object : CallHelper<GetProducts> {
                override fun onSuccessful(data: GetProducts) {
                    allproductsSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false

        }
    }
            //NEW
    fun getAllCategoriesData(
    ) {
        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.getAllCategories()
            callApi(::call, object : CallHelper<AllCategoryResponse>{
                override fun onSuccessful(data: AllCategoryResponse) {
                    getAllcategoriesSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
//            isLoading.value = false
        }
    }

    //NEW
    fun reportProduct(
        headerMap: HashMap<String, String>,
        body: HashMap<String, String>
    ) {
        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.reportProduct(headerMap,body)
            callApi(::call, object : CallHelper<ReportProductResponse>{
                override fun onSuccessful(data: ReportProductResponse) {
                    reportProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                    Log.d("TAG::", "onError: firebase error ${Gson().toJson(errorResponse)}")

                }

            })
            isLoading.value = false
        }
    }

    fun changePassword(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.changePassword(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    changePasswordSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }
    fun postUPIPayment(
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.postUPIPayment( map)

            callApi(::call, object : CallHelper<UPIModel> {
                override fun onSuccessful(data: UPIModel) {
                    postUPIPaymentSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var getUserProfileSuccess = MutableLiveData<GetUserProfile>()

    fun getUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getUserProfile(headerMap, map)

            callApi(::call, object : CallHelper<GetUserProfile> {
                override fun onSuccessful(data: GetUserProfile) {
                    getUserProfileSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                    if(errorResponse.code != null && errorResponse.code == 422){
                        unAuthorization.value = true
                    }
                }
            })

            isLoading.value = false

        }
    }

    fun loginWithPhoneNumber(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.loginWithPhoneNumber(map)

            callApi(::call, object : CallHelper<SignInResponse> {
                override fun onSuccessful(data: SignInResponse) {
                    loginWithPhoneNumberSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var updateProfileImageSuccess = MutableLiveData<CommonResponse>()

    fun updateProfileImage(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateProfileImage(headerMap, map,file)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    updateProfileImageSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var updateUserProfileSuccess = MutableLiveData<CommonResponse>()

    fun updateUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateUserProfile(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    updateUserProfileSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var logoutSuccess = MutableLiveData<CommonResponse>()

    fun logout(headerMap: HashMap<String, String>, map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.logout(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    logoutSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var resetPasswordSuccess = MutableLiveData<CommonResponse>()

    fun resetPassword(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.resetPassword(map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    resetPasswordSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var signInSuccess = MutableLiveData<SignInResponse>()

    fun signIn(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.signIn(map)

            callApi(::call, object : CallHelper<SignInResponse> {
                override fun onSuccessful(data: SignInResponse) {
                    signInSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = "Status = ${errorResponse.status} Message = ${errorResponse.responseMessage} Code = ${errorResponse.code}"

//                    FirebaseCrashlytics.getInstance().log("${errorMessage.value}")

                }
            })

            isLoading.value = false

        }
    }

    var signUpSuccess = MutableLiveData<SignUpResponse>()

    fun signUp(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.signUp(map)

            callApi(::call, object : CallHelper<SignUpResponse> {
                override fun onSuccessful(data: SignUpResponse) {
                    signUpSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var otpVerificationSuccess = MutableLiveData<CommonResponse>()

    fun otpVerification(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.otpVerification(map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    otpVerificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var sendOtpSuccess = MutableLiveData<SendOtpResponse>()

    fun sendOtp(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.sendOtp(map)

            callApi(::call, object : CallHelper<SendOtpResponse> {
                override fun onSuccessful(data: SendOtpResponse) {
                    sendOtpSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var bannerSuccess = MutableLiveData<BannerResponce>()
    fun getBanners(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getBanners(map)

            callApi(::call, object : CallHelper<BannerResponce> {
                override fun onSuccessful(data: BannerResponce) {
                    bannerSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }
    var reportSuccess = MutableLiveData<ReportResponce>()
    fun reportApi(body:ReportRequest) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.reportApi(body)

            callApi(::call, object : CallHelper<ReportResponce> {
                override fun onSuccessful(data: ReportResponce) {
                    reportSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }


    var requestsRemainSuccess = MutableLiveData<RequestsRemain>()
    fun requestRemains(version: Int) {
        viewModelScope.launch {

//            isLoading.value = true

            suspend fun call() = repository.requestsRemain(version)

            callApi(::call, object : CallHelper<RequestsRemain> {
                override fun onSuccessful(data: RequestsRemain) {
                    requestsRemainSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false

        }
    }


    var userAccountDeletionSuccess = MutableLiveData<AccountDeleteResponse>()
    fun userAccountDelete() {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.userAccountDelete()


            callApi(::call, object : CallHelper<AccountDeleteResponse> {
                override fun onSuccessful(data: AccountDeleteResponse) {
                    userAccountDeletionSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var userProfileSuccess = MutableLiveData<UserProfile>()
    fun viewProfile(id:String) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.viewProfile(id)


            callApi(::call, object : CallHelper<UserProfile> {
                override fun onSuccessful(data: UserProfile) {
                    userProfileSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var isBlockedResponse = MutableLiveData<BlockedResponse>()
    fun isBlockedUser(body: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.isBlockedUser(body)


            callApi(::call, object : CallHelper<BlockedResponse> {
                override fun onSuccessful(data: BlockedResponse) {
                    isBlockedResponse.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var iniChatSuccess = MutableLiveData<InitChatResponce>()
    fun iniChat(id:String,body: HashMap<String, String>) {

        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.iniChat(id,body)


            callApi(::call, object : CallHelper<InitChatResponce> {
                override fun onSuccessful(data: InitChatResponce) {
                    iniChatSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false
        }
    }

    var closeChatSessionSuccess = MutableLiveData<UserChatBlock>()
    fun closeChatSession(id:String) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.closeChatSession(id)


            callApi(::call, object : CallHelper<UserChatBlock> {
                override fun onSuccessful(data: UserChatBlock) {
                    closeChatSessionSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var reportChatSuccess = MutableLiveData<UserChatBlock>()
    fun reportChat(id:String,body: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.reportChat(id,body)


            callApi(::call, object : CallHelper<UserChatBlock> {
                override fun onSuccessful(data: UserChatBlock) {
                    reportChatSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var userChatBlockSuccess = MutableLiveData<UserChatBlock>()
    fun userChatBlock(body: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.userChatBlock(body)


            callApi(::call, object : CallHelper<UserChatBlock> {
                override fun onSuccessful(data: UserChatBlock) {
                    userChatBlockSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var userChatUnBlockSuccess = MutableLiveData<UserChatBlock>()
    fun userChatUnBlock(body: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.userChatUnBlock(body)


            callApi(::call, object : CallHelper<UserChatBlock> {
                override fun onSuccessful(data: UserChatBlock) {
                    userChatUnBlockSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }



    var readNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun readAllNotification(map: DeleteAll) {

        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.readAllNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    readNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false
        }
    }

    var readMultipleNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun readMultipleNotification(map: DeleteMultiple) {

        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.readMultipleNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    readMultipleNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false
        }
    }

    var deleteNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun deleteAllNotification(map: DeleteAll) {

        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.deleteAllNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    deleteNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false
        }
    }

    var deleteMultipleNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun deleteMultipleNotification(map: DeleteMultiple) {

        viewModelScope.launch {
//            isLoading.value = true

            suspend fun call() = repository.deleteMultipleNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    deleteMultipleNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

//            isLoading.value = false
        }
    }
}