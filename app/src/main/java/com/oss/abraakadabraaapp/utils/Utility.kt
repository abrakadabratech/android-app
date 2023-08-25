package com.oss.abraakadabraaapp.utils

import android.content.Context
import android.util.Log
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import java.io.File


object Utility {
    const val EXTRA_PREFIX = BuildConfig.APPLICATION_ID

    fun getHeaders(context: Context): HashMap<String, String> {
        val map = HashMap<String, String>()
        map[RequestKeys.platform] = ApiConstants.apiPlatformValue
        map[RequestKeys.token] = ApiConstants.apiTokenValue
        if (PreferencesManagement.getUserData(context) != null) {
            val token = PreferencesManagement.getUserData(context)!!.authToken
            map[RequestKeys.authorization] = ApiConstants.authConstants + token
        } else {
            map[RequestKeys.authorization] = ""
        }
        return map
    }

    fun getAuthHeaders(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map[RequestKeys.platform] = ApiConstants.apiPlatformValue
        map[RequestKeys.token] = ApiConstants.apiTokenValue
        return map
    }

    fun getAuthentication(context: Context): HashMap<String,String>{
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(context)!!
        map[RequestKeys.authorization] = token

        return map
    }

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()) {
                deleteRecursive(child)
            }
        }
        fileOrDirectory.delete()
        Log.d("ok", "deleteRecursive: cache cleared")
    }
}
