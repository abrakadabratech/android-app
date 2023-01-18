package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductsViewModelFactory(
    private val api: APIs
) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsViewModel(api) as T
    }
}