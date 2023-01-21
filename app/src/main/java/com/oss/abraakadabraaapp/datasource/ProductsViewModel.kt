package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.oss.abraakadabraaapp.retrofit.api.APIs
import kotlinx.coroutines.flow.Flow

class ProductsViewModel(val page:Int,private val api: APIs, val headers:Map<String,String>,
                        val maxDistange:Int,
                        val lat: Double,
                        val long:Double
) : ViewModel() {

    val listData = Pager(PagingConfig(pageSize = 6)) {
        ProductDataSource(page,api,headers,maxDistange, lat,long)
    }.flow.cachedIn(viewModelScope)
}