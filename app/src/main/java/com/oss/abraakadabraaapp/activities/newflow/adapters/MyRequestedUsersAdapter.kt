package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.MyListingDetialActivity
import com.oss.abraakadabraaapp.activities.newflow.RequesterActivity
import com.oss.abraakadabraaapp.response.productRequestResponse.Requests

class MyRequestedUsersAdapter(val context: MyListingDetialActivity, val data: ArrayList<Requests>,val onclick:OnRequestClicks)
    : RecyclerView.Adapter<MyRequestedUsersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var userName = itemView.findViewById<TextView>(R.id.userName)
        var message = itemView.findViewById<TextView>(R.id.message)
        var locatinName = itemView.findViewById<TextView>(R.id.locatinName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_request_users_row,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.setText(data[position].username)
        holder.message.setText(data[position].message)
        holder.locatinName.setText(context.getAddress(data[position].coordinates?.Latitude?.toDouble()!!,
            data[position].coordinates?.Longitude?.toDouble()!!
        ))
        holder.itemView.setOnClickListener {
            onclick.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnRequestClicks{
        fun onClick(position:Int)
    }
}