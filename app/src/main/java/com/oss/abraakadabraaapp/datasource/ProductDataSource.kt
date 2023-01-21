package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductDataSource(val page:Int,private val api: APIs,
    val headers:Map<String,String>,
                        val maxDistange:Int,
                        val lat: Double,
                        val long:Double
) : PagingSource<Int, Product>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val currentLoadingPageKey = params.key ?: 1
            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            val response = api.getProductsData(headers,currentLoadingPageKey,maxDistange,lat,long)
            val responseData = mutableListOf<Product>()
            val data = response.data.products ?: emptyList()
            responseData.addAll(data)

            Log.d("TAG-", "load: ${Gson().toJson(response)}")
            LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = if (response.data.products.isEmpty()) null else currentLoadingPageKey + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}