package com.oss.abraakadabraaapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oss.abraakadabraaapp.model.DeleteAll
import com.oss.abraakadabraaapp.model.DeleteMultiple
import com.oss.abraakadabraaapp.model.ReadNotificationResponse
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.HttpErrorResponse
import com.oss.abraakadabraaapp.response.locationResponse.LocationAddressResponse
import com.oss.abraakadabraaapp.response.mainResponse.*
import com.oss.abraakadabraaapp.retrofit.api.CallHelper
import com.oss.abraakadabraaapp.retrofit.api.callApi
import com.oss.abraakadabraaapp.retrofit.repository.MainRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    var errorMessage = MutableLiveData("")
    var isLoading = MutableLiveData(false)
    var unAuthorization = MutableLiveData(false)

    var requestActionSuccess = MutableLiveData<RequestActionResponse>()

    fun requestAction(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.requestAction(headerMap,map)

            callApi(::call, object : CallHelper<RequestActionResponse> {
                override fun onSuccessful(data: RequestActionResponse) {
                    requestActionSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var requestProductDetailSuccess = MutableLiveData<GetRequestProductDetailResponse>()

    fun getRequestProductDetail(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getRequestProductDetail(headerMap,map)

            callApi(::call, object : CallHelper<GetRequestProductDetailResponse> {
                override fun onSuccessful(data: GetRequestProductDetailResponse) {
                    requestProductDetailSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                    if(errorResponse.code == 422){
                        unAuthorization.value = true
                    }
                }

            })
            isLoading.value = false

        }
    }

    var searchListSuccess = MutableLiveData<GetSearchListResponse>()

    fun getSearchList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getSearchList(headerMap,map)

            callApi(::call, object : CallHelper<GetSearchListResponse> {
                override fun onSuccessful(data: GetSearchListResponse) {
                    searchListSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var receiverListSuccess = MutableLiveData<GetReceiverListResponse>()

    fun getReceiverList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getReceiverList(headerMap,map)

            callApi(::call, object : CallHelper<GetReceiverListResponse> {
                override fun onSuccessful(data: GetReceiverListResponse) {
                    receiverListSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var giverListSuccess = MutableLiveData<GetGiverListResponse>()

    fun getGiverList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getGiverList(headerMap,map)

            callApi(::call, object : CallHelper<GetGiverListResponse> {
                override fun onSuccessful(data: GetGiverListResponse) {
                    giverListSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                    if(errorResponse.code == 422){
                        unAuthorization.value = true
                    }
                }

            })
            isLoading.value = false

        }
    }

    var makeARequestSuccess = MutableLiveData<MakeRequestResponse>()

    fun makeARequest(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.makeARequest(headerMap,map)

            callApi(::call, object : CallHelper<MakeRequestResponse> {
                override fun onSuccessful(data: MakeRequestResponse) {
                    makeARequestSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var getProductDetailSuccess = MutableLiveData<GetProductDetailResponse>()

    fun getProductDetail(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getProductDetail(headerMap,map)

            callApi(::call, object : CallHelper<GetProductDetailResponse> {
                override fun onSuccessful(data: GetProductDetailResponse) {
                    getProductDetailSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var cancelProductSuccess = MutableLiveData<CommonResponse>()

    fun cancelProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.cancelProduct(headerMap,map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    cancelProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var deleteProductSuccess = MutableLiveData<CommonResponse>()

    fun deleteProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.deleteProduct(headerMap,map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    deleteProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var getMyProductListSuccess = MutableLiveData<GetProductListResponse>()

    fun getMyProductList(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getMyProductList(headerMap,map)

            callApi(::call, object : CallHelper<GetProductListResponse> {
                override fun onSuccessful(data: GetProductListResponse) {
                    getMyProductListSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var manageProductSuccess = MutableLiveData<CommonResponse>()

    fun manageProduct(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        productImages: Array<MultipartBody.Part>
    ) {
        viewModelScope.launch {

            suspend fun call() = repository.manageProduct(
                headerMap,
                map,
                productImages
            )

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    manageProductSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var getHomeDataSuccess = MutableLiveData<GetHomeDataResponse>()

    fun getHomeData(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>,
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getHomeData(headerMap,map)

            callApi(::call, object : CallHelper<GetHomeDataResponse> {
                override fun onSuccessful(data: GetHomeDataResponse) {
                    getHomeDataSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }

            })
            isLoading.value = false

        }
    }

    var getCategorySuccess = MutableLiveData<GetCategoryResponse>()

    fun getCategory(
        headerMap: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getCategory(headerMap)

            callApi(::call, object : CallHelper<GetCategoryResponse> {
                override fun onSuccessful(data: GetCategoryResponse) {
                    getCategorySuccess.value = data
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

    var addressSuccess = MutableLiveData<LocationAddressResponse>()

    fun getAddress(url: String) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getAddress(url)

            callApi(::call, object : CallHelper<LocationAddressResponse> {
                override fun onSuccessful(data: LocationAddressResponse) {
                    addressSuccess.value = data
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
            isLoading.value = true

            suspend fun call() = repository.readAllNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    readNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var readMultipleNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun readMultipleNotification(map: DeleteMultiple) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.readMultipleNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    readMultipleNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var deleteNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun deleteAllNotification(map: DeleteAll) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.deleteAllNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    deleteNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var deleteMultipleNotificationSuccess = MutableLiveData<ReadNotificationResponse>()

    fun deleteMultipleNotification(map: DeleteMultiple) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.deleteMultipleNotification(map)

            callApi(::call, object : CallHelper<ReadNotificationResponse> {
                override fun onSuccessful(data: ReadNotificationResponse) {
                    deleteMultipleNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }



}