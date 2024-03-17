package com.oss.abraakadabraaapp.retrofit.repository

import DataClass
import com.oss.abraakadabraaapp.activities.newflow.apimodels.CreateUserRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.FcmRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.FcmResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.ProductRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.ReportRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.requests.ReportProductRequest
import com.oss.abraakadabraaapp.model.DeleteAll
import com.oss.abraakadabraaapp.model.DeleteMultiple
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.utils.Utility
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import kotlin.math.max

class AuthRepository(private val apiHelper: APIs) {

    //API MVP2.0
    //=================================================================================//

    suspend fun getUser(
    ) = apiHelper.getUser()

    suspend fun onBoardUser(
        map: HashMap<String, String>,body: HashMap<String, String>
    ) = apiHelper.onBoardUser(body)

    suspend fun logoutUser(
        map: HashMap<String, String>
    ) = apiHelper.logoutUser()

    suspend fun postUser(
        map: HashMap<String, String>,
        params: HashMap<String, String>
    ) = apiHelper.postUser(params)

    suspend fun postUserV2(
        body: CreateUserRequest
    ) = apiHelper.postUserV2(body)

    suspend fun updateUser(
        map: HashMap<String, String>,
        params: DataClass
    ) = apiHelper.updateUser(params)

    suspend fun updateUserV2(
        params: Map<String, String>
    ) = apiHelper.updateUserV2(params)

    suspend fun postFCMToken(
        params: FcmRequest
    ) = apiHelper.postFCMToken(params)

    suspend fun getSocialLink(
        map: HashMap<String, String>
    ) = apiHelper.getSocialLink()

    suspend fun postSocialLink(
        map: HashMap<String, String>,
        params: HashMap<String, String>
    ) = apiHelper.postSocialLink(params)

    suspend fun updateProfilePic(
        map: HashMap<String, String>,
        file: MultipartBody.Part
    ) = apiHelper.updateProfilePic(file)

    suspend fun postProduct(
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>
    ) = apiHelper.postProduct(body, file)

    suspend fun ProductDetails(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getProductDetails(id)

    suspend fun getListingDetails(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getListingDetails(id)

    suspend fun getListingDetailsV2(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getListingDetailsV2(id)

    suspend fun deleteProduct(
        id: String
    ) = apiHelper.deleteProduct(id)

    suspend fun updateProduct(
        map: HashMap<String, String>,
        id: String,
        images: ArrayList<String>,
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>
    ) = apiHelper.updateProduct(id, body, file)

    suspend fun updateProductIfImage(
        map: HashMap<String, String>,
        id: String,
        images: ArrayList<String>,
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>,
        displayImage: MultipartBody.Part
    ) = apiHelper.updateProductIfImage(id, body, file, displayImage)

    suspend fun getSupportData(
        map: HashMap<String, String>
    ) = apiHelper.getSupportData()

    suspend fun getProductsData(
        map: HashMap<String, String>,
        page: Int, maxDistance: Int, lat: Double, lang: Double, category: String, sortBy: String
    ) = apiHelper.getProductsData(page, maxDistance, lat, lang, category, "30", sortBy)


    suspend fun getAllCategories(
    ) = apiHelper.getAllCategories()

    suspend fun reportProduct(
        map: HashMap<String, String>,
        body: HashMap<String, String>
    ) = apiHelper.reportProduct(body)

    suspend fun postRequest(
        version:Int,
        id: String,
        body: HashMap<String, String>
    ) = apiHelper.postProductRequest(version,id, body)

    suspend fun updateProductRequest(
        id: String,
        body: String
    ) = apiHelper.updateProductRequest(id, body)

    suspend fun cancelProductRequest(
        id: String
    ) = apiHelper.cancelProductRequest(id)

    suspend fun getRequestor(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getRequestor(id)


    suspend fun getRequestDetails(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getRequestDetails(id)

    suspend fun getProductListing(
        map: HashMap<String, String>,
    ) = apiHelper.getProductListings()

    suspend fun getMyRequests(
    ) = apiHelper.getMyRequests()

    suspend fun getRazorPay(
        map: HashMap<String, String>
    ) = apiHelper.getRazorPay()

    suspend fun initPayment(
        map: HashMap<String, String>,
        id: HashMap<String, String>
    ) = apiHelper.initPayment(id)

    suspend fun updatePayment(
        id: HashMap<String, String>
    ) = apiHelper.updatePayment(id)

    suspend fun sendFeedback(
        id: String,
        body: HashMap<String, String>
    ) = apiHelper.sendFeedback(id, body)

    suspend fun searchQuery(
        page: String,
        lat: Double, long: Double, maxDistance: Int, pageNumber: Int, sortBy: String
    ) = apiHelper.searchQuery(page, lat, long, maxDistance, pageNumber, "30", sortBy)

    suspend fun sendNotification(
        body: HashMap<String, String>

    ) = apiHelper.sendNotification(body)

    //=================================================================================//
    suspend fun loginWithPhoneNumber(
        map: HashMap<String, String>
    ) = apiHelper.loginWithPhoneNumber(Utility.getAuthHeaders())

    suspend fun signIn(
        map: HashMap<String, String>
    ) = apiHelper.signIn(Utility.getAuthHeaders())

    suspend fun resetPassword(
        map: HashMap<String, String>
    ) = apiHelper.resetPassword(Utility.getAuthHeaders())

    suspend fun signUp(
        map: HashMap<String, String>
    ) = apiHelper.signUp(Utility.getAuthHeaders())

    suspend fun otpVerification(
        map: HashMap<String, String>
    ) = apiHelper.otpVerification(Utility.getAuthHeaders())

    suspend fun sendOtp(
        map: HashMap<String, String>
    ) = apiHelper.sendOtp(Utility.getAuthHeaders())

    suspend fun logout(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.logout(headerMap)

    suspend fun updateUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.updateUserProfile(headerMap)

    suspend fun updateProfileImage(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        file: MultipartBody.Part
    ) = apiHelper.updateProfileImage( map, file)

    suspend fun getUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getUserProfile( map)

    suspend fun changePassword(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.changePassword(map)

    suspend fun postUPIPayment(
        map: HashMap<String, String>
    ) = apiHelper.postUPIPayment(map)

    suspend fun getBanners(
        headerMap: HashMap<String, String>
    ) = apiHelper.getBanners()

    suspend fun reportApi(
        map: ReportRequest
    ) = apiHelper.reportApi(map)

    suspend fun requestsRemain(version: Int) = apiHelper.requestRemains(version)

    suspend fun userAccountDelete() = apiHelper.deleteUserAccount()
    suspend fun viewProfile(id:String) = apiHelper.viewProfile(id)

    suspend fun readAllNotification(
        map: DeleteAll
    ) = apiHelper.readAllNotification(map)

    suspend fun readMultipleNotification(
        map: DeleteMultiple
    ) = apiHelper.readMultipleNotification(map)

    suspend fun deleteAllNotification(map: DeleteAll) = apiHelper.deleteAllNotification(map)
    suspend fun deleteMultipleNotification(map: DeleteMultiple) = apiHelper.deleteMultipleNotification(map)
}