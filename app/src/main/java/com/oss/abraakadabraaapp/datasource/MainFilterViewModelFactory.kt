package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIService

class MainFilterViewModelFactory(
    private val apiService: APIService,
    private val maxDistance: Int,
    private val lat: Double,
    private val long: Double,
    private val sortBy: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainFilterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainFilterViewModel(apiService, maxDistance, lat, long, sortBy) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}