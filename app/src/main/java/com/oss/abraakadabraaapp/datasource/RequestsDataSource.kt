package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.response.productRequestResponse.Requests
import com.oss.abraakadabraaapp.retrofit.api.APIService
import org.greenrobot.eventbus.EventBus

class RequestsDataSource(private val apiService: APIService,
                         private val headers:Map<String,String>,private val productId:String) : PagingSource<Int, Requests>() {

    override fun getRefreshKey(state: PagingState<Int, Requests>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Requests> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getProductRequests(headers, productId,currentLoadingPageKey)
            Log.d("TAG - ", "load:response data $response")
            val responseData = mutableListOf<Requests>()
            EventBus.getDefault().post(response.requests.size.toFloat())
            val data = response.requests ?: emptyList()
            responseData.addAll(data)
            Log.d("TAG - ", "load:response data ${responseData.size} ${Gson().toJson(responseData)}")
            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            return LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = if (response.requests.isEmpty()) null else currentLoadingPageKey + 1
            )
        }catch (e: Exception) {
            Log.e("TAG - ", "load: ${e.message}", )
            return LoadResult.Error(e)
        }
    }

}