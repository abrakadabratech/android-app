package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oss.abraakadabraaapp.retrofit.api.APIService
private const val PAZE_SIZE = 10

class MainViewModel(
    private val apiService: APIService,
    private val maxDistance: Int,
    private val lat: Double,
    private val long: Double,
    private val sortBy: String
) : ViewModel() {
    val listData =
        Pager(PagingConfig(pageSize = PAZE_SIZE ), pagingSourceFactory = {
            ProductDataSource(apiService,  maxDistance, lat, long, sortBy)
        }).flow
}