package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.MyListingDetialActivity
import com.oss.abraakadabraaapp.activities.newflow.NewMyRequestActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.MyRequestDetailsActivity

class MyRequestedUsersAdapter(val newMyRequestActivity: MyListingDetialActivity, val i: Int)
    : RecyclerView.Adapter<MyRequestedUsersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//        var cardItem = itemView.findViewById<CardView>(R.id.cardItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_request_users_row,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return i
    }
}