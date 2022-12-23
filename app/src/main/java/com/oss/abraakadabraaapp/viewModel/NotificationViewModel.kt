package com.oss.abraakadabraaapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.HttpErrorResponse
import com.oss.abraakadabraaapp.response.notificationResponse.NotificationResponse
import com.oss.abraakadabraaapp.retrofit.api.CallHelper
import com.oss.abraakadabraaapp.retrofit.api.callApi
import com.oss.abraakadabraaapp.retrofit.repository.MainRepository
import kotlinx.coroutines.launch
import java.util.HashMap

class NotificationViewModel (
    private val repository: MainRepository
) : ViewModel() {

    var errorMessage = MutableLiveData("")
    var isLoading = MutableLiveData(false)
    var unAuthorization = MutableLiveData(false)

    var readNotificationSuccess = MutableLiveData<CommonResponse>()

    fun readNotification(headerMap: HashMap<String, String>, map: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.readNotification(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    readNotificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

    var notificationSuccess = MutableLiveData<NotificationResponse>()

    fun getAllNotification(headerMap: HashMap<String, String>, map: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.getAllNotifications(headerMap, map)

            callApi(::call, object : CallHelper<NotificationResponse> {
                override fun onSuccessful(data: NotificationResponse) {
                    notificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }
}