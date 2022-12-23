package com.oss.abraakadabraaapp.response.locationResponse


import com.google.gson.annotations.SerializedName

data class LocationAddressResponse(
    @SerializedName("plus_code")
    val plusCode: PlusCode,
    @SerializedName("results")
    val results: ArrayList<Result>,
    @SerializedName("status")
    val status: String
){
    data class Result(
        @SerializedName("address_components")
        val addressComponents: ArrayList<AddressComponent>,
        @SerializedName("formatted_address")
        val formattedAddress: String,
        @SerializedName("geometry")
        val geometry: Geometry,
        @SerializedName("place_id")
        val placeId: String,
        @SerializedName("plus_code")
        val plusCode: PlusCode,
        @SerializedName("types")
        val types: ArrayList<String>
    )
}