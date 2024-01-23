package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.retrofit.api.APIs

class NotificationViewModelFactory(private val api:APIService):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}