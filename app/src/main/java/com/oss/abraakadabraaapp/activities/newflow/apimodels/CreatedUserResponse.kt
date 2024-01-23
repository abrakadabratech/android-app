package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class CreatedUserResponse(
    @SerializedName("code") var code: Int? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("error_code") var error_code: String,
    @SerializedName("error") var error: Boolean = false,
    @SerializedName("message") var responseMessage: String? = "",
    @SerializedName("data") var data: Data? = Data()
)

data class Data(

    @SerializedName("uid") var uid: String? = "",
    @SerializedName("method") var method: String? = ""

)