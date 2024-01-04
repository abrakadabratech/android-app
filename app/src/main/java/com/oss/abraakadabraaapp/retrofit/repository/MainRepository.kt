package com.oss.abraakadabraaapp.retrofit.repository

import com.oss.abraakadabraaapp.retrofit.api.APIs
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainRepository(private val apiHelper: APIs) {
























    suspend fun getAddress(
        url: String
    ) = apiHelper.getAddress(url)

    suspend fun logout(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.logout(headerMap)

    suspend fun contentManagementSystem(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.contentManagementSystem(headerMap)

    suspend fun getCategory(
        headerMap: HashMap<String, String>
    ) = apiHelper.getCategory(headerMap)

    suspend fun getHomeData(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getHomeData(headerMap)

    suspend fun getAllNotifications(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getAllNotifications(headerMap)

    suspend fun readNotification(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.readNotification(headerMap)

    suspend fun manageProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        productImages: Array<MultipartBody.Part>
    ) = apiHelper.manageProduct(
        map,
        productImages
    )

    suspend fun getMyProductList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getMyProductList( map)

    suspend fun deleteProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.deleteProduct(map)

    suspend fun cancelProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.cancelProduct(map)

    suspend fun getProductDetail(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getProductDetail(map)

    suspend fun makeARequest(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.makeARequest(map)

    suspend fun getReceiverList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getReceiverList(map)

    suspend fun getGiverList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getGiverList(map)

    suspend fun getSearchList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getSearchList(map)

    suspend fun getRequestProductDetail(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getRequestProductDetail(map)

    suspend fun requestAction(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.requestAction(map)

}