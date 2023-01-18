package com.oss.abraakadabraaapp.retrofit.repository

import com.oss.abraakadabraaapp.activities.newflow.apimodels.DataClass
import com.oss.abraakadabraaapp.activities.newflow.apimodels.ProductRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.utils.Utility
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class AuthRepository(private val apiHelper: APIs) {

    //API MVP2.0
    //=================================================================================//

    suspend fun getUser(
        map: HashMap<String, String>
    ) = apiHelper.getUser(map)

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