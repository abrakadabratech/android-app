package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.retrofit.api.APIService

class MainViewModelFactory(private val apiService: APIService,
                           private val maxDistance:Int,
                           private val lat:Double,
                           private val long:Double,
                           private val sortBy:String,private val type:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(apiService,maxDistance,lat,long,sortBy,type) as T
        }

        if (modelClass.isAssignableFrom(MainFilterViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MainFilterViewModel(apiService,maxDistance,lat,long,sortBy,type) as T
        }
        /*else{
            return SearchViewModel(apiService,headers,maxDistance,lat,long,categories,sortBy) as T
        }*/
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
