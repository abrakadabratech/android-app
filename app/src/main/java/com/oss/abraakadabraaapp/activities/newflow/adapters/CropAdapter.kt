package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData

class CropAdapter(
    val context: Context, var i: ArrayList<Uri>
) : RecyclerView.Adapter<CropAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cropImage: ImageView = itemView.findViewById(R.id.cropImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.crop_row1, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(i[position]).into(holder.cropImage)
    }

    override fun getItemCount(): Int {
        return i.size
    }

}