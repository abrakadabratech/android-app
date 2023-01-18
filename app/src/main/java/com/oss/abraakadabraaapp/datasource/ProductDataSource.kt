//package com.oss.abraakadabraaapp.datasource
//
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
//import com.oss.abraakadabraaapp.activities.newflow.apimodels.SocialProfileResponse
//import com.oss.abraakadabraaapp.retrofit.api.APIs
//
//class ProductDataSource(private val api: APIs) : PagingSource<Int, Data>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
//        return try {
//            val nextPageNumber = params.key ?: 0
//            val response = api.getProductsData(nextPageNumber)
//
//            LoadResult.Page(
//                data = response.data,
//                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
//                nextKey = if (nextPageNumber < response.data?.count!!) nextPageNumber + 1 else null
//            )
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
//        TODO("Not yet implemented")
//    }
//}