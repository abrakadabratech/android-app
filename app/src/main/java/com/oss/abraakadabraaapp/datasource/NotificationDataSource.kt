package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.model.Notifications
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.retrofit.api.Movie

class NotificationDataSource(private val api:APIService) : PagingSource<Int,Notifications>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
    override fun getRefreshKey(state: PagingState<Int, Notifications>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notifications> {
        try {
            Log.d("TAG - ", "load: Starting to load page ${params.key ?: 1}")

            val page = params.key ?: STARTING_PAGE_INDEX
            val response = api.getAllNotifications(page = page)


            Log.d("TAG - ", "load:response data ${response.body()}")

            return LoadResult.Page(
                data = response.body()?.notifications!!,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page -1,
                nextKey = if (response.body()!!.notifications.isEmpty()) null else page+1
            )
        }catch (e: Exception) {
            Log.e("TAG - ", "load: ${e.message}", )
            return LoadResult.Error(e)
        }
    }
}