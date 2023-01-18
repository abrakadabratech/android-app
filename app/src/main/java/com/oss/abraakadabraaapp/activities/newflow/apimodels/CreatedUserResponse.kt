package com.oss.abraakadabraaapp.activities.newflow.apimodels

import com.google.gson.annotations.SerializedName

data class CreatedUserResponse(
    @SerializedName("code") var code: Int? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("response_message") var responseMessage: String? = null,
    @SerializedName("data") var data: Data? = Data()
)

data class Data(

    @SerializedName("uid") var uid: String? = null

)