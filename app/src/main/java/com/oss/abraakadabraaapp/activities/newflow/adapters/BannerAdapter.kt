package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.apimodels.BannerData

class BannerAdapter(val context: Context, val i: ArrayList<BannerData>) :
RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.title)
        val profilePic = itemView.findViewById<ImageView>(R.id.bannerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.userName.text = i[position].title
        Glide.with(context).load(i[position].imageUrl).into(holder.profilePic)
    }

    override fun getItemCount(): Int {
        return i.size
    }

}