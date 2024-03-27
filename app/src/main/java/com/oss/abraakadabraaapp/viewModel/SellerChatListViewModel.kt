package com.oss.abraakadabraaapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.oss.abraakadabraaapp.datasource.SellerChatListDataSource
import com.oss.abraakadabraaapp.retrofit.api.APIService

class SellerChatListViewModel(private val api: APIService,private val id:String) : ViewModel() {

    val notificationList = Pager(
        PagingConfig(
            pageSize = 10,
        ), pagingSourceFactory = { SellerChatListDataSource(api,id) }
    ).flow

}