package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.CropSelectModel

class CropSelectAdapter(val context: Context, var i: ArrayList<CropSelectModel>,val listner:OnSelectorClicks
) : RecyclerView.Adapter<CropSelectAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cropImage: ImageView = itemView.findViewById(R.id.cropImage2)
        var cropLayout: ConstraintLayout = itemView.findViewById(R.id.cropLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.crop_row2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (i[position].isSelect){
            holder.cropLayout.setBackgroundDrawable(context.resources.getDrawable(R.drawable.rect_border))
        }else{
            holder.cropLayout.setBackgroundColor(context.resources.getColor(R.color.transparent))
        }
        Glide.with(context).load(i[position].uri).into(holder.cropImage)
        holder.cropImage.setOnClickListener {
            listner.onSelectorClick(position)
        }
    }

    override fun getItemCount(): Int {
        return i.size
    }

    interface OnSelectorClicks{
        fun onSelectorClick(position: Int)
    }
}