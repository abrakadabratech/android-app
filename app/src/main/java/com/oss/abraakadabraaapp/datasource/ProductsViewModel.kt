package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductsViewModel(private val api: APIs
) : ViewModel() {
   /* val passengers =
        Pager(config = PagingConfig(pageSize = 10), pagingSourceFactory = {
            ProductDataSource(api)
        }).flow.cachedIn(viewModelScope)*/
}