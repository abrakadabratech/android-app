package com.oss.abraakadabraaapp.utils

import android.text.format.DateFormat
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

    fun toDate(timestamp:Int):String{
        val formatter = SimpleDateFormat("MMM dd,yyyy HH:mm")

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp.toLong()
         return  DateFormat.format("MMM dd,yyyy HH:mm", timestamp.toLong() *1000).toString()

//        formatter.format(calendar.time)
    }
}