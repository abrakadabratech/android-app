package com.oss.abraakadabraaapp.retrofit.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class DiscoverResult(
    val page: Int,
    val results: List<Movie>,
    val total_pages: Int,
    val total_results: Int
)

@Parcelize
data class Movie(
    val id: Int = -1,
    val adult: Boolean?=null,
    val backdrop_path: String?=null,
    val overview: String?=null,
    val popularity: Double?=null,
    val poster_path: String?=null,
    val release_date: String?=null,
    val title: String?=null,
    val vote_average: Double?=null,
    val vote_count: Int?=null
): Parcelable