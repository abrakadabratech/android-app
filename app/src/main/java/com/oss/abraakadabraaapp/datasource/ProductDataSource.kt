package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.oss.abraakadabraaapp.datasource.products.Product
import org.greenrobot.eventbus.EventBus

class ProductDataSource(private val apiService: APIService,
                        private val headers:Map<String,String>,
                        private val maxDistance:Int,
                        private val lat:Double,
                        private val long:Double,
                        private val categories:String,
private val sortBy:String) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getProductsData(headers, currentLoadingPageKey,maxDistance,lat,long,sortBy)
            Log.d("TAG - ", "load:response data $response")
            val responseData = mutableListOf<Product>()
            EventBus.getDefault().post(response.data.products.size.toFloat())
            val data = response.data.products ?: emptyList()
            responseData.addAll(data)
            Log.d("TAG - ", "load:response data ${responseData.size} ${Gson().toJson(responseData)}")
            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            return LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = if (response.data.products.isEmpty()) null else currentLoadingPageKey + 1
            )
        }catch (e: Exception) {
            Log.e("TAG - ", "load: ${e.message}", )
            return LoadResult.Error(e)
        }
    }

}