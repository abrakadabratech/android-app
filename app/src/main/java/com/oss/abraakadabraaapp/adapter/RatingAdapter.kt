package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.RatingRowBinding

class RatingAdapter(
    var context: Context,
    private val data: ArrayList<Boolean>,
) :
    RecyclerView.Adapter<RatingAdapter.PostImageVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageVH {
        val v = LayoutInflater.from(context).inflate(R.layout.rating_row, parent, false)
        return PostImageVH(v)
    }

    override fun onBindViewHolder(holder: PostImageVH, position: Int) {
        if (data[position]){
            Glide.with(context).load(context.resources.getDrawable(R.drawable.filled_star))
                .into(holder.binding.image)
        }else{
            Glide.with(context).load(context.resources.getDrawable(R.drawable.not_filled_star))
                .into(holder.binding.image)
        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    class PostImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RatingRowBinding.bind(itemView)
    }


}