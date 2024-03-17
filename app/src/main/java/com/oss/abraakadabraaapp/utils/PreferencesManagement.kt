package com.oss.abraakadabraaapp.utils

import android.content.Context
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.model.AllCategoryResponse
import com.oss.abraakadabraaapp.activities.newflow.model.Filters
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
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


    fun saveUserProfileFlag(context: Context, flag: Boolean): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val prefsEditor = pref.edit()
        prefsEditor.putBoolean("userProfileflag", flag)
        return prefsEditor.commit()
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

    fun saveCategories(context: Context, data: AllCategoryResponse?):Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        val json = Gson().toJson(data)
        prefsEditor.putString("userCategories", json)
        return prefsEditor.commit()
    }

    fun getCategories(context: Context): AllCategoryResponse? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("userCategories", null)
        val a = arrayListOf<UserCatData>()
        a.add(UserCatData("","No Data","",true))

        return if (json == null)
            AllCategoryResponse(100,0,a )
        else
            Gson().fromJson(json, AllCategoryResponse::class.java)

    }

    fun isFistOpen(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return pref.getBoolean("IS_FIRST_OPEN",true)
    }

    fun isNotificationEnabled(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return pref.getBoolean("IS_NOTIFICATION_ENABLED",false)
    }

    fun setFistOpen(context: Context,boolean: Boolean): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()
        prefsEditor.putBoolean("IS_FIRST_OPEN", boolean)

        return prefsEditor.commit()
    }
    fun setisNotificationEnabled(context: Context,boolean: Boolean): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()
        prefsEditor.putBoolean("IS_NOTIFICATION_ENABLED", boolean)

        return prefsEditor.commit()
    }

    fun getFilters(context: Context): Filters? {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("Filters_key", Gson().toJson(Filters("free","latest")))

        return if (json == null)
            null
        else
            Gson().fromJson(json, Filters::class.java)

    }

    fun setFilters(context: Context, data: Filters):Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        val json = Gson().toJson(data)
        prefsEditor.putString("Filters_key", json)
        return prefsEditor.commit()
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

    fun saveSignInMethod(context: Context, signinMethod: String):Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        prefsEditor.putString("SIGN_IN_METHOD",signinMethod)

        return prefsEditor.commit()
    }
    fun getSignInMethod(context: Context): String {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val json = pref.getString("SIGN_IN_METHOD", "phone").toString()

        return json
    }
}