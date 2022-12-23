package com.oss.abraakadabraaapp.model

import android.os.Parcelable
import com.oss.abraakadabraaapp.response.mainResponse.ProductImage
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditProductData(
    val productId: String,
    val productName: String,
    val gender: String,
    val categoryId: String,
    val brand: String,
    val productAge: String,
    val productCondition: String,
    val productLocation: String,
    val lat: String,
    val lng: String,
    val productDescription: String,
    val productImages: ArrayList<ProductImage>,
) : Parcelable
