package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.MyListingDetialActivity
import com.oss.abraakadabraaapp.response.productRequestResponse.Requests
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class MyRequestedUsersAdapter(val context: MyListingDetialActivity, val data: ArrayList<Requests>,val onclick:OnRequestClicks)
    : RecyclerView.Adapter<MyRequestedUsersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userName = itemView.findViewById<TextView>(R.id.userName)
        var message = itemView.findViewById<TextView>(R.id.message)
        var locatinName = itemView.findViewById<TextView>(R.id.locatinName)
        var timeduration = itemView.findViewById<TextView>(R.id.textView113)
        var distance = itemView.findViewById<TextView>(R.id.textView109)
        var imgaeView = itemView.findViewById<ImageView>(R.id.imageView20)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_request_users_row,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(data[position].user_avatar)
            .placeholder(context.resources.getDrawable(R.drawable.ic_profile))
            .into(holder.imgaeView)
        holder.userName.setText(data[position].username)
        holder.message.setText(data[position].message)
        holder.locatinName.setText(context.getAddress(data[position].coordinates?.Latitude?.toDouble()!!,
            data[position].coordinates?.Longitude?.toDouble()!!
        ))
        holder.itemView.setOnClickListener {
            onclick.onClick(position)
        }
        holder.timeduration.setText(getTime(data[position].timestamp?.toInt()!!))
        var d = data[position].distance?.div(1000)!!
        holder.distance.setText(if(d <= 0) "${data[position].distance}m Away" else "$d Km Away")
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnRequestClicks{
        fun onClick(position:Int)
    }

    fun getTime(unix:Int):String{
        val oldTime: Long = unix.toLong()
        val currentTime: Long = System.currentTimeMillis() / 1000;
        val result = convertFromDuration(currentTime - oldTime)
        Log.i("TAG", result.toString())

        return result.toString()
    }
    fun convertFromDuration(timeInSeconds: Long): TimeInHours {
        var time = timeInSeconds
        val hours = time / 3600
        time %= 3600
        val minutes = time / 60
        time %= 60
        val seconds = time
        return TimeInHours(hours.toInt(), minutes.toInt(), seconds.toInt())
    }
    class TimeInHours(val hours: Int, val minutes: Int, val seconds: Int) {
        override fun toString(): String {

            if (hours > 24 ){
                var day = hours/24
                if (day == 1) return "${hours/24} Day Ago"
                else return "${hours/24} Days Ago"
            }else if(minutes > 60) {
                var day = minutes / 60
                if (day == 1) return "${minutes / 60} Hour Ago"
                else return "${minutes / 60} Minutes Ago"
            }else{
                return "$minutes Min $seconds Seconds"
            }
        }
    }
}