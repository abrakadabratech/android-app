package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.retrofit.api.APIs

class NotificationViewModel(private val api:APIService) : ViewModel() {

    val notificationList = Pager(
        PagingConfig(
            pageSize = 10,
        ), pagingSourceFactory = { NotificationDataSource(api)}
    ).flow

}