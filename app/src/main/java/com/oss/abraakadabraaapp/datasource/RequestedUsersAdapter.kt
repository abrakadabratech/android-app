package com.oss.abraakadabraaapp.datasource

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.MyListingDetialActivity
import com.oss.abraakadabraaapp.response.productRequestResponse.Requests
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class RequestedUsersAdapter(val context: MyListingDetialActivity, val onClick: OnRequestClicks) :
    PagingDataAdapter<Requests, RequestedUsersAdapter.ViewHolder>(ProductDifferntiator) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var userName = itemView.findViewById<TextView>(R.id.userName)
        var message = itemView.findViewById<TextView>(R.id.message)
        var locatinName = itemView.findViewById<TextView>(R.id.locatinName)
        var timeduration = itemView.findViewById<TextView>(R.id.textView113)
        var distance = itemView.findViewById<TextView>(R.id.textView109)
        var status = itemView.findViewById<TextView>(R.id.status)
        var imgaeView = itemView.findViewById<ImageView>(R.id.imageView20)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item?.user_avatar != null) {
            Glide.with(holder.itemView.context).load(item?.user_avatar)
                .placeholder(ContextCompat.getDrawable(holder.itemView.context,R.drawable.ic_profile))
                .into(holder.imgaeView)
        }
        holder.userName.setText(item?.username)
        holder.message.setText(item?.message)
        holder.locatinName.setText(context.getAddress(item?.coordinates?.Latitude?.toDouble()!!,
            item.coordinates?.Longitude?.toDouble()!!
        ))
        holder.itemView.setOnClickListener {
            onClick.onClick(item)
        }
        holder.timeduration.setText(getTime(item.timestamp?.toInt()!!))
        val d = item.distance?.div(1000)!!
        holder.distance.setText(if(d <= 0) "${item.distance}m Away" else "$d Km Away")

        holder.itemView.setOnClickListener {
            Log.d("NewReceiverFragment", "bind: ${item.distance}")
            onClick.onClick(item)
        }
        holder.status.text = item.status?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
        when(item.status){
            "accepted" -> holder.status.setTextColor(ContextCompat.getColor(context,R.color.status_accepted))
            else -> holder.status.setTextColor(ContextCompat.getColor(context,R.color.status_declined))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.my_request_users_row, parent, false)
        )
    }

    object ProductDifferntiator : DiffUtil.ItemCallback<Requests>() {

        override fun areItemsTheSame(oldItem: Requests, newItem: Requests): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Requests, newItem: Requests): Boolean {
            return oldItem == newItem
        }
    }
    interface OnRequestClicks{
        fun onClick(position:Requests)
    }

    private fun getTime(unix:Int):String{

        try {
            val sdf = SimpleDateFormat("MMM-dd-yy hh:mm a", Locale.getDefault())
            val netDate = Date(unix.toLong() * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}