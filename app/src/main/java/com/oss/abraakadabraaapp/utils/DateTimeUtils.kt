package com.oss.abraakadabraaapp.utils

import android.util.Log
import androidx.annotation.IntRange
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    private const val timeFormat = "mm:ss"

    fun convertLongToTimeFormat(secondTime: Int): String {
        val tz = TimeZone.getTimeZone("UTC")
        val df = SimpleDateFormat(timeFormat, Locale.US)
        df.timeZone = tz
        return df.format(Date(secondTime * 1000L))
    }

}