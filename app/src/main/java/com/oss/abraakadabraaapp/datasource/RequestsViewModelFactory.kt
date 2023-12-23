package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIService

class RequestsViewModelFactory(private val apiService: APIService,
                               private val headers:Map<String,String>,private val productId:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestViewModel::class.java)) {
            return RequestViewModel(apiService,headers,productId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}