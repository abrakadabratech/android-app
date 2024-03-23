package com.oss.abraakadabraaapp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.viewModel.BlockerUserViewModel

class BlockerUserViewModelFactory(private val api: APIService,private val id:String): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlockerUserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlockerUserViewModel(api,id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}