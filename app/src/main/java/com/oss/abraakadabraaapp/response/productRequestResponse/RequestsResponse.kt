package com.oss.abraakadabraaapp.response.productRequestResponse

import com.google.gson.annotations.SerializedName

class RequestsResponse (@SerializedName("code"       ) var code       : Int?              = null,
                        @SerializedName("status"     ) var status     : Int?              = null,
                        @SerializedName("message"    ) var message    : String?           = null,
                        @SerializedName("requests"   ) var requests   : ArrayList<Requests> = arrayListOf(),
                        @SerializedName("pageSize"   ) var pageSize   : Int?              = null,
                        @SerializedName("pageNumber" ) var pageNumber : Int?              = null,
                        @SerializedName("totalPages" ) var totalPages : Int?              = null)
