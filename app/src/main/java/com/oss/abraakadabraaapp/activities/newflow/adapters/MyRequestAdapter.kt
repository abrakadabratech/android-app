package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.MyListingDetialActivity
import com.oss.abraakadabraaapp.activities.newflow.NewMyRequestActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.MyRequestDetailsActivity
import com.oss.abraakadabraaapp.response.productRequestResponse.Data
import com.oss.abraakadabraaapp.response.productRequestResponse.Product
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData

class MyRequestAdapter(
    val newMyRequestActivity: NewMyRequestActivity,
    private var data: ArrayList<Data>,
    var onclick: OnResponseClick
) : RecyclerView.Adapter<MyRequestAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardItem = itemView.findViewById<CardView>(R.id.cardItem)
        var itemName = itemView.findViewById<TextView>(R.id.itemName)
        var itemRaisedOn = itemView.findViewById<TextView>(R.id.itemRaisedOn)
        var status = itemView.findViewById<TextView>(R.id.status)
        var itemImage = itemView.findViewById<ImageView>(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.my_request_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.setText(data[position].product?.name?.capitalize())
        holder.itemRaisedOn.setText(data[position].requestedAt)

        when(data[position].status){
            "requested" -> {
                holder.status.setTextColor(newMyRequestActivity.getColor(R.color.status_pending))
                holder.status.setText(data[position].status?.capitalize())
            }
            "accepted" -> {
                holder.status.setTextColor(newMyRequestActivity.getColor(R.color.status_accepted))
                holder.status.setText(data[position].status?.capitalize())
            }
            "rejected" -> {
                holder.status.setTextColor(newMyRequestActivity.getColor(R.color.status_declined))
                holder.status.setText(data[position].status?.capitalize())
            }
            "received" -> {
                holder.status.setTextColor(newMyRequestActivity.getColor(R.color.status_accepted))
                holder.status.setText(data[position].status?.capitalize())
            }
            "delivered" -> {
                holder.status.setTextColor(newMyRequestActivity.getColor(R.color.status_accepted))
                holder.status.setText(data[position].status?.capitalize())
            }else ->{
                holder.status.setTextColor(newMyRequestActivity.getColor(R.color.status_declined))
                holder.status.setText(data[position].status?.capitalize())
            }
        }

        Glide.with(newMyRequestActivity)
            .load(data[position].product?.image)
            .into(holder.itemImage)

        holder.cardItem.setOnClickListener {
            onclick.onResponseClicked(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(list:ArrayList<Data>) {
        data = list
    }

    interface OnResponseClick{
        fun onResponseClicked(item:Data)
    }
}