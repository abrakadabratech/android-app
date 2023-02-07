package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val apiService: APIService,
                           private val headers:Map<String,String>,
                           private val maxDistance:Int,
                           private val lat:Double,
                           private val long:Double,
                           private val categories:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiService,headers,maxDistance,lat,long,categories) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}