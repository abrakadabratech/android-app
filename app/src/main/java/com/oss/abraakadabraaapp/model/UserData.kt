package com.oss.abraakadabraaapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    @SerializedName("auth_token")
    val authToken: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("phone_number")
    var phoneNumber: String,
    @SerializedName("profile_image")
    var profileImage: String?,
    @SerializedName("lng")
    var lng: String?,
    @SerializedName("lat")
    var lat: String?,
    @SerializedName("full_address")
    var fullAddress: String?,
    @SerializedName("contry_code")
    var contryCode: String?,
) : Parcelable