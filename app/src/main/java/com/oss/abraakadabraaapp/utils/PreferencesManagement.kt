package com.oss.abraakadabraaapp.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig.Flag
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.activities.StartAppActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.model.AllCategoryResponse
import com.oss.abraakadabraaapp.activities.newflow.model.Filters
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.model.UserData
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys.data
import kotlin.collections.ArrayList


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

    fun saveUserName(context: Context, userData: String?): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()
        prefsEditor.putString("userName", userData)

        return prefsEditor.commit()
    }

    fun getUserName(context: Context): String? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return pref.getString("userName", "")

    }
    fun saveUserEmail(context: Context, userData: String?): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()
        prefsEditor.putString("userEmail", userData)

        return prefsEditor.commit()
    }

    fun getUserEmail(context: Context): String? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return pref.getString("userEmail", "")

    }

    fun saveUserInfo(context: Context, userData: GetUserResponse?): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (userData != null) {
            val json = Gson().toJson(userData)
            prefsEditor.putString("userInfo", json)
        }
        return prefsEditor.commit()
    }

    fun saveUserFlag(context: Context, flag: Boolean): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val prefsEditor = pref.edit()
        prefsEditor.putBoolean("userInfoflag", flag)
        return prefsEditor.commit()
    }

    fun getUserInfoFlag(context: Context): Boolean? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getBoolean("userInfoflag", false)

        return json

    }

    fun saveUserProfileFlag(context: Context, flag: Boolean): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val prefsEditor = pref.edit()
        prefsEditor.putBoolean("userProfileflag", flag)
        return prefsEditor.commit()
    }

    fun getUserProfileFlag(context: Context): Boolean? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getBoolean("userProfileflag", false)

        return json

    }

    fun getUserInfo(context: Context): GetUserResponse? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("userInfo", "active")

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

    fun saveCategories(context: Context, data: AllCategoryResponse):Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (data == null) {
            prefsEditor.putString("userCategories", null)
        } else {
            val json = Gson().toJson(data)
            prefsEditor.putString("userCategories", json)
        }
        return prefsEditor.commit()
    }

    fun getCategories(context: Context): AllCategoryResponse? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("userCategories", null)

        return if (json == null)
            null
        else
            Gson().fromJson(json, AllCategoryResponse::class.java)

    }

    fun isFistOpen(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return pref.getBoolean("IS_FIRST_OPEN",true)
    }

    fun setFistOpen(context: Context,boolean: Boolean): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()
        prefsEditor.putBoolean("IS_FIRST_OPEN", boolean)

        return prefsEditor.commit()
    }

    fun getFilters(context: Context): Filters? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("Filters_key", Gson().toJson(Filters(true,false)))

        return if (json == null)
            null
        else
            Gson().fromJson(json, Filters::class.java)

    }

    fun setFilters(context: Context, data: Filters):Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (data == null) {
            prefsEditor.putString("Filters_key", null)
        } else {
            val json = Gson().toJson(data)
            prefsEditor.putString("Filters_key", json)
        }
        return prefsEditor.commit()
    }

    fun saveTempBaseUrl(context: Context,toString: String): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        if (toString == "") {
            prefsEditor.putString("Base_url", BuildConfig.BASE_URL)
        } else {
//            val json = Gson().toJson(data)
            prefsEditor.putString("Base_url", toString)
        }
        return prefsEditor.commit()
    }
    fun getTempBaseUrl(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("Base_url", BuildConfig.BASE_URL)

        return if (json == "")
            BuildConfig.BASE_URL
        else
            json.toString()
    }

    fun isTooltipShown(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getBoolean("Tooltip_cropActivity", false)

        return json
    }
    fun disableCropTooltip(context: Context,toString: Boolean): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        prefsEditor.putBoolean("Tooltip_cropActivity",toString)

        return prefsEditor.commit()
    }

}