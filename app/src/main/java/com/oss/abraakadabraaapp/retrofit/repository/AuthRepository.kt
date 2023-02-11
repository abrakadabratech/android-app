package com.oss.abraakadabraaapp.retrofit.repository

import DataClass
import com.oss.abraakadabraaapp.activities.newflow.apimodels.ProductRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.requests.ReportProductRequest
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
        map: HashMap<String, String>
    ) = apiHelper.getUser(map)

    suspend fun logoutUser(
        map: HashMap<String, String>
    ) = apiHelper.logoutUser(map)

    suspend fun postUser(
        map: HashMap<String, String>,
        params: HashMap<String, String>
    ) = apiHelper.postUser(map,params)

    suspend fun updateUser(
        map: HashMap<String, String>,
        params: DataClass
    ) = apiHelper.updateUser(map,params)

    suspend fun getSocialLink(
        map: HashMap<String, String>
    ) = apiHelper.getSocialLink(map)

    suspend fun postSocialLink(
        map: HashMap<String, String>,
        params: HashMap<String, String>
    ) = apiHelper.postSocialLink(map,params)

    suspend fun updateProfilePic(
        map: HashMap<String, String>,
        file: MultipartBody.Part
    ) = apiHelper.updateProfilePic(map,file)

    suspend fun postProduct(
        map: HashMap<String, String>,
        body: Map<String, RequestBody>,
        file: Array<MultipartBody.Part>
    ) = apiHelper.postProduct(map,body,file)

    suspend fun ProductDetails(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getProductDetails(map,id)

    suspend fun getListingDetails(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.getListingDetails(map,id)

    suspend fun deleteProduct(
        map: HashMap<String, String>,
        id: String
    ) = apiHelper.deleteProduct(map,id)

    suspend fun updateProduct(
        map: HashMap<String, String>,
        id: String,
        body: Map<String,String>
    ) = apiHelper.updateProduct(map,id,body)

    suspend fun getSupportData(
        map: HashMap<String, String>)
      = apiHelper.getSupportData(map)

    suspend fun getProductsData(
        map: HashMap<String, String>,
        page:Int,maxDistance:Int,lat:Double,lang:Double,category:String)
            = apiHelper.getProductsData(map,page, maxDistance,lat,lang,category)


    suspend fun getAllCategories(
        map: HashMap<String, String>)
      = apiHelper.getAllCategories(map)

    suspend fun reportProduct(
        map: HashMap<String, String>,
        body: HashMap<String, String>
    )
      = apiHelper.reportProduct(map,body)

    suspend fun postRequest(
        map: HashMap<String, String>,
        id: String,
        body: HashMap<String, String>
    )
      = apiHelper.postProductRequest(map,id,body)

    suspend fun updateProductRequest(
        map: HashMap<String, String>,
        id: String,
        body: String
    )
            = apiHelper.updateProductRequest(map,id,body)

    suspend fun cancelProductRequest(
        map: HashMap<String, String>,
        id: String
    )
            = apiHelper.cancelProductRequest(map,id)

    suspend fun getRequestDetails(
        map: HashMap<String, String>,
        id: String)
      = apiHelper.getRequestDetails(map,id)

    suspend fun getProductListing(
        map: HashMap<String, String>,
    )
      = apiHelper.getProductListings(map)

    suspend fun getMyRequests(
        map: HashMap<String, String>,
    )
      = apiHelper.getMyRequests(map)

    suspend fun initPayment(
        map: HashMap<String, String>,
        id: HashMap<String, String>)
            = apiHelper.initPayment(map,id)

    suspend fun updatePayment(
        map: HashMap<String, String>,
        id: HashMap<String, String>)
            = apiHelper.updatePayment(map,id)

    suspend fun sendFeedback(
        map: HashMap<String, String>,
        id:String,
        body: HashMap<String, String>)
            = apiHelper.sendFeedback(map,id,body)

    suspend fun searchQuery(
        map: HashMap<String, String>,
        page:String,
        lat:Double,long:Double,maxDistance:Int,pageNumber:Int
    )
            = apiHelper.searchQuery(map,page,lat,long,maxDistance,pageNumber)

    suspend fun sendNotification(
        map: HashMap<String, String>,
        body: HashMap<String, String>

    )
            = apiHelper.sendNotification(map,body)

    //=================================================================================//
    suspend fun loginWithPhoneNumber(
        map: HashMap<String, String>
    ) = apiHelper.loginWithPhoneNumber(Utility.getAuthHeaders(), map)

    suspend fun signIn(
        map: HashMap<String, String>
    ) = apiHelper.signIn(Utility.getAuthHeaders(), map)

    suspend fun resetPassword(
        map: HashMap<String, String>
    ) = apiHelper.resetPassword(Utility.getAuthHeaders(), map)

    suspend fun signUp(
        map: HashMap<String, String>
    ) = apiHelper.signUp(Utility.getAuthHeaders(), map)

    suspend fun otpVerification(
        map: HashMap<String, String>
    ) = apiHelper.otpVerification(Utility.getAuthHeaders(), map)

    suspend fun sendOtp(
        map: HashMap<String, String>
    ) = apiHelper.sendOtp(Utility.getAuthHeaders(), map)

    suspend fun logout(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.logout(headerMap, map)

    suspend fun updateUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.updateUserProfile(headerMap, map)

    suspend fun updateProfileImage(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        file: MultipartBody.Part
    ) = apiHelper.updateProfileImage(headerMap, map,file)

    suspend fun getUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getUserProfile(headerMap, map)

    suspend fun changePassword(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.changePassword(headerMap, map)
}