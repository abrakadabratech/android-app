package com.oss.abraakadabraaapp.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.SocialProfileResponse
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductDataSource(val page:Int,private val api: APIs,
    val headers:Map<String,String>,
                        val maxDistange:Int,
                        val lat: Double,
                        val long:Double
) : PagingSource<Int, Products>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Products> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = api.getProductsData(headers,nextPageNumber,maxDistange,lat,long)

            LoadResult.Page(
                data = response.data?.products!!,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (nextPageNumber < response.data?.count!!) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Products>): Int? {
        TODO("Not yet implemented")
    }
}