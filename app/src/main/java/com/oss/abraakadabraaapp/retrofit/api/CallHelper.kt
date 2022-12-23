package com.oss.abraakadabraaapp.retrofit.api

import android.util.Log
import com.google.gson.Gson
import com.oss.abraakadabraaapp.response.commonResponse.HttpErrorResponse
import retrofit2.Response
import java.net.SocketTimeoutException

interface CallHelper<T> {
    fun onSuccessful(data: T)
    fun onError(errorResponse: HttpErrorResponse)
}

suspend fun <T> callApi(api: suspend () -> Response<T>, callback: CallHelper<T>) {

    try {
        val response: Response<T> = api.invoke()

        if (response.isSuccessful) {
            val data = response.body()!!
            callback.onSuccessful(data)
        } else {
            val gson = Gson()
            val errorResponse: HttpErrorResponse = gson.fromJson(
                response.errorBody()!!.string(),
                HttpErrorResponse::class.java
            )
            callback.onError(errorResponse)
        }

    } catch (e: Exception) {
        val error = HttpErrorResponse(999, e.localizedMessage ?:"", false)
        callback.onError(error)
    } catch (e: SocketTimeoutException) {
        Log.d("SocketTimeoutException", e.localizedMessage ?:"")
    }
}