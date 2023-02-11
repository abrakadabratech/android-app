package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.ItemProductBinding
import Data

class SearchProductAdapter(
    private var data: ArrayList<Data>,
    var context: Context,
    private var callback: LatestProductAdapterInterface
) : RecyclerView.Adapter<SearchProductAdapter.LatestProductAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestProductAdapterVH {
        return LatestProductAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LatestProductAdapterVH, position: Int) {

        val item = data[position]

        Glide.with(context).load(item.image).into(holder.binding.ivProduct)
        holder.binding.tvProductDistance.text = "${item.distance?.div(1000)} KM"
        holder.binding.tvProductName.text = item.name?.capitalize()
        holder.binding.tvProductLocation.text = item.condition


        holder.itemView.setOnClickListener {
            callback.onSearchItemDetail(item, position)
        }

    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    fun setData(d: ArrayList<Data>) {
        data = d
    }

    class LatestProductAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductBinding.bind(itemView)
    }

    interface LatestProductAdapterInterface {
        fun onSearchItemDetail(data: Data, position: Int)
    }

}