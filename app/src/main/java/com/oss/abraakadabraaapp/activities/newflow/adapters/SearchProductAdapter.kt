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
import androidx.core.content.ContextCompat
import com.oss.abraakadabraaapp.databinding.ItemProduct2Binding
import com.oss.abraakadabraaapp.datasource.products.Product

class SearchProductAdapter(
    private var data: ArrayList<Product>,
    var context: Context,
    private var callback: LatestProductAdapterInterface
) : RecyclerView.Adapter<SearchProductAdapter.LatestProductAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestProductAdapterVH {
        return LatestProductAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_product2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LatestProductAdapterVH, position: Int) {

        val item = data[position]

        Glide.with(context).load(item.display_image).into(holder.binding.ivProduct)
        holder.binding.tvProductDistance.text = "${item.distance?.div(1000)} KM"
        holder.binding.tvProductName.text = item.name?.capitalize()
        holder.binding.tvProductLocation.text = item.condition
        if (item!!.isSelfProduct){
            holder.binding.ivGiven.visibility = View.VISIBLE
        }else{
            holder.binding.ivGiven.visibility = View.GONE
        }

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

    fun setData(d: ArrayList<Product>) {
        data = d
    }

    class LatestProductAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProduct2Binding.bind(itemView)
    }

    interface LatestProductAdapterInterface {
        fun onSearchItemDetail(data: Product, position: Int)
    }

}