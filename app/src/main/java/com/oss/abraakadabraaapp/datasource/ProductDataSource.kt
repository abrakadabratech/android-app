package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.retrofit.api.APIService

class ProductDataSource(private val apiService: APIService,
                        private val maxDistance:Int,
                        private val lat:Double,
                        private val long:Double,
                        private val sortBy:String,private val type:String) : PagingSource<Int, Product>() {
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

            /*val position = params.key ?: STARTING_PAGE_INDEX
            return when(val result =
                apiService.getProductsData(position,10,maxDistance,lat,long,sortBy)){
                is Resource.Failure -> LoadResult.Error(Exception(result.toString()))
                is Resource.Loading -> LoadResult.Error(Exception())
                is Resource.Success -> {
                    LoadResult.Page(data = result.data.products,
                        prevKey = if(position == STARTING_PAGE_INDEX) null else -1,
                        nextKey = if (result.data.passengerList.isEmpty()) null else position + 1)
                }

            }*/

            val page = params.key ?: STARTING_PAGE_INDEX
            val response = apiService.getProductsData(
                page = page,
                maxDistance = maxDistance,/*12.9715987&long=77.5945627*/
                lat = lat,
                long = long,
                sortBy = sortBy,
                type = type
            )

            Log.d("TAG - ", "load:response data ${response.body()} ")

            return LoadResult.Page(
                data = response.body()!!.data.products,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page -1,
                nextKey = if (response.body()!!.data.products.isEmpty()) null else page+1
            )
        }catch (e: Exception) {
            Log.e("TAG - ", "load: ${e.message}", )
            return LoadResult.Error(e)
        }
    }
}