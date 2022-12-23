package com.oss.abraakadabraaapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oss.abraakadabraaapp.response.commonResponse.ContentManagementResponse
import com.oss.abraakadabraaapp.response.commonResponse.HttpErrorResponse
import com.oss.abraakadabraaapp.retrofit.api.CallHelper
import com.oss.abraakadabraaapp.retrofit.api.callApi
import com.oss.abraakadabraaapp.retrofit.repository.MainRepository
import kotlinx.coroutines.launch
import java.util.*

class ContentManagementViewModel(
    private val repository: MainRepository
) : ViewModel() {

    var errorMessage = MutableLiveData("")
    var isLoading = MutableLiveData(false)

    var contentManagementSuccess = MutableLiveData<ContentManagementResponse>()

    fun contentManagementSystem(headerMap: HashMap<String, String>, map: HashMap<String, String>) {

        viewModelScope.launch {
            isLoading.value = true

            suspend fun call() = repository.contentManagementSystem(headerMap, map)

            callApi(::call, object : CallHelper<ContentManagementResponse> {
                override fun onSuccessful(data: ContentManagementResponse) {
                    contentManagementSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false
        }
    }

}