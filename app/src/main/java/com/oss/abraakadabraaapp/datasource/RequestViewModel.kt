package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oss.abraakadabraaapp.retrofit.api.APIService

class RequestViewModel(private val apiService: APIService,
                       private val headers:Map<String,String>,private val productId:String) : ViewModel() {
    val listRequests = Pager(PagingConfig(pageSize = 10, prefetchDistance = 1)) {
        RequestsDataSource(apiService,headers,productId)
    }.flow.cachedIn(viewModelScope)
}