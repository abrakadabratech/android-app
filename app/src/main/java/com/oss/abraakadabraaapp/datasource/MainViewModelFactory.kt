package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val apiService: APIService,
                           private val headers:Map<String,String>,
                           private val maxDistance:Int,
                           private val lat:Double,
                           private val long:Double,
                           private val categories:String,
private val sortBy:String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(apiService,headers,maxDistance,lat,long,categories,sortBy) as T
        }

        if (modelClass.isAssignableFrom(MainFilterViewModel::class.java)){
            return MainFilterViewModel(apiService,headers,maxDistance,lat,long,categories,sortBy) as T
        }
        /*else{
            return SearchViewModel(apiService,headers,maxDistance,lat,long,categories,sortBy) as T
        }*/
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}