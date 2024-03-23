package com.oss.abraakadabraaapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.oss.abraakadabraaapp.datasource.BuyerChatListDataSource
import com.oss.abraakadabraaapp.retrofit.api.APIService

class BuyerProductViewModel(private val api: APIService) : ViewModel() {

    val notificationList = Pager(
        PagingConfig(
            pageSize = 10,
        ), pagingSourceFactory = { BuyerChatListDataSource(api) }
    ).flow

}