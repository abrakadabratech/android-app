package com.oss.abraakadabraaapp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.viewModel.SellerChatListViewModel

class SellerChatViewModelFactory(private val api: APIService): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellerChatListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SellerChatListViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}