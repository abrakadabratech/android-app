package com.oss.abraakadabraaapp.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.SocialProfileResponse
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductDataSource(val page:Int,private val api: APIs,
    val headers:Map<String,String>,
                        val maxDistange:Int,
                        val lat: Double,
                        val long:Double
) : PagingSource<Int, Products>() {

    companion object {
        private const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Products> {
        return try {
            val position = params.key ?: STARTING_PAGE_INDEX
            val response = api.getProductsData(headers,position,maxDistange,lat,long)
            Log.d("TAG-", "load: ${Gson().toJson(response)}")
            LoadResult.Page(
                data = response.data?.products!!,
                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                nextKey = if (response.data!!.products.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Products>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}