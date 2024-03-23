package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.chat.GroupedChatListModel
import com.oss.abraakadabraaapp.model.Notifications
import com.oss.abraakadabraaapp.model.Product

class ExpandableAdapter(
    val context: Context
) :
    PagingDataAdapter<Product, ExpandableAdapter.ViewHolder>(ProductDifferentiator) {
    private val TAG = "GivingChatsFragment"
    interface HandleClicks {
        fun enableOptions()

        fun onItemClick(notification: Notifications)

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage = itemView.findViewById<ImageView>(R.id.profilePic)!!
        val product = itemView.findViewById<TextView>(R.id.userName)!!
        val unreadCount = itemView.findViewById<TextView>(R.id.unreadCount)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!

        holder.product.text = item.name
        Glide.with(context).load(item.productImage).into(holder.productImage)
        holder.unreadCount.text = item.chatCount

        holder.itemView.setOnClickListener {

        }
    }


    companion object ProductDifferentiator : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
