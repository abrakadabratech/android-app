package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oss.abraakadabraaapp.model.Notifications
import com.oss.abraakadabraaapp.model.Product
import com.oss.abraakadabraaapp.model.SellerList
import com.oss.abraakadabraaapp.retrofit.api.APIService

class SellerProductsDataSource(private val api: APIService) : PagingSource<Int, Product>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }
    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        try {
            Log.d("TAG - ", "load: Starting to load page ${params.key ?: 1}")

            val page = params.key ?: STARTING_PAGE_INDEX
            val response = api.getSellerProductsList(page = page, pageSize = 10)


            Log.d("TAG - ", "load:response data ${response.body()}")

            return LoadResult.Page(
                data = response.body()?.data?.products!!,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page -1,
                nextKey = if (response.body()!!.data.products.isEmpty()) null else page+1
            )
        }catch (e: Exception) {
            Log.e("TAG - ", "load: ${e.message}", )
            return LoadResult.Error(e)
        }
    }
}