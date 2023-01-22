package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.anilhappy.paginationsample.APIService

class MainViewModel(private val apiService: APIService,
                    private val headers:Map<String,String>,
                    private val maxDistance:Int,
                    private val lat:Double,
                    private val long:Double) : ViewModel() {
    val listData = Pager(PagingConfig(pageSize = 6)) {
        ProductDataSource(apiService,headers,maxDistance,lat,long)
    }.flow.cachedIn(viewModelScope)
}