package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn

class MainViewModel(private val apiService: APIService,
                    private val headers:Map<String,String>,
                    private val maxDistance:Int,
                    private val lat:Double,
                    private val long:Double,
                    private val categories:String) : ViewModel() {
    val listData = Pager(PagingConfig(pageSize = 3, prefetchDistance = 2)) {
        ProductDataSource(apiService,headers,maxDistance,lat,long,categories)
    }.flow.cachedIn(viewModelScope)
}