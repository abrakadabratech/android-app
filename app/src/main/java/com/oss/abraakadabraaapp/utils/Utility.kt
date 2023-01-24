package com.oss.abraakadabraaapp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants


object Utility {

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
}
