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
    ) = apiHelper.logout(headerMap, map)

    suspend fun contentManagementSystem(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.contentManagementSystem(headerMap, map)

    suspend fun getCategory(
        headerMap: HashMap<String, String>
    ) = apiHelper.getCategory(headerMap)

    suspend fun getHomeData(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getHomeData(headerMap,map)

    suspend fun getAllNotifications(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getAllNotifications(headerMap, map)

    suspend fun readNotification(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.readNotification(headerMap, map)

    suspend fun manageProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        productImages: Array<MultipartBody.Part>
    ) = apiHelper.manageProduct(
        headerMap,
        map,
        productImages
    )

    suspend fun getMyProductList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getMyProductList(headerMap, map)

    suspend fun deleteProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.deleteProduct(headerMap, map)

    suspend fun cancelProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.cancelProduct(headerMap, map)

    suspend fun getProductDetail(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getProductDetail(headerMap, map)

    suspend fun makeARequest(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.makeARequest(headerMap, map)

    suspend fun getReceiverList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getReceiverList(headerMap, map)

    suspend fun getGiverList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getGiverList(headerMap, map)

    suspend fun getSearchList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getSearchList(headerMap, map)

    suspend fun getRequestProductDetail(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.getRequestProductDetail(headerMap, map)

    suspend fun requestAction(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) = apiHelper.requestAction(headerMap, map)

}