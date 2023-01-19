package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductsViewModel(val page:Int,private val api: APIs, val headers:Map<String,String>,
                        val maxDistange:Int,
                        val lat: Double,
                        val long:Double
) : ViewModel() {
    val products =
        Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = {
            ProductDataSource(page,api,headers,maxDistange, lat,long)
        }).flow.cachedIn(viewModelScope)
}