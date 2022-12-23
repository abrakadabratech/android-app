package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemProductFeatureBinding


class ProductFeatureAdapter(
    private val data: ArrayList<ProductFeature>,
    var context: Context
) : RecyclerView.Adapter<ProductFeatureAdapter.ProductFeatureAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductFeatureAdapterVH {
        return ProductFeatureAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_product_feature, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductFeatureAdapterVH, position: Int) {
        val item = data[position]
        with(holder.binding){
            tvTitle.text = item.title.trim()
            tvSubTitle.text = item.subTitle.trim()
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

    class ProductFeatureAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductFeatureBinding.bind(itemView)
    }

}