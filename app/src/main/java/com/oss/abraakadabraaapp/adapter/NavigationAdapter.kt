package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.model.NavItem

class NavigationAdapter(
    private val data: ArrayList<NavItem>,
    var context: Context,
    private var callback: NavigationItemInterface
) :
    RecyclerView.Adapter<NavigationAdapter.NavigationVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavigationVH {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.item_navigation, parent, false)
        return NavigationVH(v)
    }

    override fun onBindViewHolder(holder: NavigationVH, position: Int) {
        val item = data[position]

        holder.itemView.setOnClickListener {
            callback.itemClick(item.id)
        }

        holder.navigationText.text = item.title

        if (item.iconVisibility != 1) {
            holder.navigationText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            holder.navigationText.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_forward_ios,
                0
            )
        }

    }

    fun addItem(itemData: ArrayList<NavItem>) {
        data.clear()
        data.addAll(itemData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    class NavigationVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val navigationText: TextView = itemView.findViewById(R.id.tv_nav_list_item)
    }

    interface NavigationItemInterface {
        fun itemClick(id: Int)
    }


}