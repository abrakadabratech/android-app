package com.oss.abraakadabraaapp.model

import android.net.Uri

class ProductImage(
    val uri: Uri?, val id: Int, val image: String,
) {
    constructor(uri: String, id: Int, image: String) : this(null,id,image)
}