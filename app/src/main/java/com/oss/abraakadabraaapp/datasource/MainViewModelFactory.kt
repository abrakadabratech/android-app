package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.anilhappy.paginationsample.APIService

class MainViewModelFactory(private val apiService: APIService,
                           private val headers:Map<String,String>,
                           private val maxDistance:Int,
                           private val lat:Double,
                           private val long:Double) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiService,headers,maxDistance,lat,long) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}