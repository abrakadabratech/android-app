package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.oss.abraakadabraaapp.retrofit.api.APIService

private const val PAZE_SIZE = 10
class MainFilterViewModel(private val apiService: APIService,
                          private val maxDistance:Int,
                          private val lat:Double,
                          private val long:Double,
                          private val sortBy:String) : ViewModel() {
    val listData2 = Pager(
        PagingConfig(pageSize = PAZE_SIZE, initialLoadSize = 20)) {
        ProductDataSource(apiService,maxDistance,lat,long,sortBy)
    }.flow
}
