package com.oss.abraakadabraaapp.utils

import android.content.Context
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.model.UserData
import com.oss.abraakadabraaapp.model.UserLocation


object PreferencesManagement {

    private val PREF_NAME = "user_data"
    private val USER_LOCATION = "user_location"

    fun saveUserLocation(context: Context, latLong: UserLocation?): Boolean {
        val pref = context.getSharedPreferences(USER_LOCATION, Context.MODE_PRIVATE)
        val prefsEditor = pref.edit()
        if (latLong == null) {
            prefsEditor.putString("userLocation", null)
        } else {
            val json = Gson().toJson(latLong)
            prefsEditor.putString("userLocation", json)
        }
        return prefsEditor.commit()
    }

    fun getUserLocation(context: Context): UserLocation? {
        val pref = context.getSharedPreferences(USER_LOCATION, Context.MODE_PRIVATE)
        val json = pref.getString("userLocation", null)
        return if (json == null)
            null
        else
            Gson().fromJson(json, UserLocation::class.java)
    }

    fun saveUserData(context: Context, userData: UserData?): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (userData == null) {
            prefsEditor.putString("userData", null)
        } else {
            val json = Gson().toJson(userData)
            prefsEditor.putString("userData", json)
        }
        return prefsEditor.commit()
    }

    fun getUserData(context: Context): UserData? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("userData", null)

        return if (json == null)
            null
        else
            Gson().fromJson(json, UserData::class.java)
    }

    fun saveAuthToken(context: Context,authToken: String):Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (authToken == "Bearer ") {
            prefsEditor.putString("authToken", null)
        } else {
            prefsEditor.putString("authToken", authToken)
        }
        return prefsEditor.commit()
    }
    fun getAuthToken(context: Context): String? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("authToken", null)

        return if (json == "Bearer ")
            null
        else
            json

    }

    fun saveUserInfo(context: Context, userData: GetUserResponse?): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (userData == null) {
            prefsEditor.putString("userInfo", null)
        } else {
            val json = Gson().toJson(userData)
            prefsEditor.putString("userInfo", json)
        }
        return prefsEditor.commit()
    }

    fun getUserInfo(context: Context): GetUserResponse? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("userInfo", null)

        return if (json == null)
            null
        else
            Gson().fromJson(json, GetUserResponse::class.java)

    }

    fun saveFCMToken(context: Context, it: String?): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()
        if (it == null) {
            prefsEditor.putString("fcmToken", null)
        } else {
//            val json = it
            prefsEditor.putString("fcmToken", it)
        }
        return prefsEditor.commit()
    }

    fun getFCMToken(context: Context): String? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("fcmToken", null)

        return json

    }
}