package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.SocialData
import com.oss.abraakadabraaapp.databinding.SocialShareItemBinding

class SocialShareAdapter(
    var newHomeActivity: Context,
    var images: ArrayList<SocialData>,
    val onclick: OnSocialProfileClicked
) :
    RecyclerView.Adapter<SocialShareAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = SocialShareItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.social_share_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imageSocial.setImageResource(images[position].icon)
        if (images[position].status) {
            holder.binding.imageSocial.setBackgroundResource(R.drawable.circle_with_green_stroke)
            images.get(position).status = true
        } else{
            holder.binding.imageSocial.setBackgroundResource(R.color.transparent)
            images.get(position).status = false
        }

        holder.itemView.setOnClickListener {
            onclick.onSocialIconClick(position,!images.get(position).status)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    interface OnSocialProfileClicked {
        fun onSocialIconClick(position: Int,status:Boolean)
    }
}