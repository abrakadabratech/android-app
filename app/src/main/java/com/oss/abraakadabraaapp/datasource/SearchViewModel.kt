package com.oss.abraakadabraaapp.datasource

import SearchDataSource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oss.abraakadabraaapp.retrofit.api.APIService

class SearchViewModel(private val apiService: APIService,
                      private val headers:Map<String,String>,
                      private val maxDistance:Int,
                      private val lat:Double,
                      private val long:Double,
                      private val query:String,
                      private val sortBy:String) : ViewModel() {
    val listData = Pager(PagingConfig(pageSize = 3, prefetchDistance = 2)) {
        SearchDataSource(apiService,headers,maxDistance,lat,long,query,sortBy)
    }.flow.cachedIn(viewModelScope)
}