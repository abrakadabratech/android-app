package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
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
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData

class MyListingAdapter(val newMyRequestActivity: Context,
                       private var data: ArrayList<RequestData>,var onclick:OnResponseClick)
    : RecyclerView.Adapter<MyListingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView){
        var cardItem = itemView.findViewById<CardView>(R.id.cardItem)
        var nameTxt = itemView.findViewById<TextView>(R.id.textView57)
        var listedOnTxt = itemView.findViewById<TextView>(R.id.textView58)
        var statusTxt = itemView.findViewById<TextView>(R.id.textView59)
        var responsed = itemView.findViewById<TextView>(R.id.textView115)
        var image = itemView.findViewById<ImageView>(R.id.imageView24)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.my_listing_row,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cardItem.setOnClickListener {
            onclick.onResponseClicked(data.get(position))
//            newMyRequestActivity.startActivity(Intent(newMyRequestActivity,MyListingDetialActivity::class.java))
        }

        holder.nameTxt.setText(data.get(position).name?.capitalize())
        holder.listedOnTxt.setText("Listed on "+data.get(position).createdAt)
        holder.statusTxt.setText("Status - "+data.get(position).status?.capitalize())
        if (data.get(position).status == "given"){
            holder.statusTxt.setTextColor(newMyRequestActivity.resources.getColor(R.color.given_color))
        }else{
            holder.statusTxt.setTextColor(newMyRequestActivity.resources.getColor(R.color.cat_select_color))
        }
        holder.responsed.setText("Responses :"+data.get(position).responses.toString())
        Glide.with(newMyRequestActivity).load(data.get(position).image).into(holder.image)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(list:ArrayList<RequestData>) {
        data = list
    }

    interface OnResponseClick{
        fun onResponseClicked(item:RequestData)
    }


}