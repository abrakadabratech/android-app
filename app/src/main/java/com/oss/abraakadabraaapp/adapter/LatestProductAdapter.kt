package com.oss.abraakadabraaapp.adapter

import Data
import SearchModel
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemProductBinding
import com.oss.abraakadabraaapp.databinding.LayoutProductBinding
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.utils.ImageUtils


class LatestProductAdapter(
    private var data: ArrayList<Product>,
    var context: Context,
    private var callback: LatestProductAdapterInterface
) : RecyclerView.Adapter<LatestProductAdapter.LatestProductAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestProductAdapterVH {
        return LatestProductAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LatestProductAdapterVH, position: Int) {

        val item = data[position]

        Glide.with(context).load(item.image).placeholder(R.drawable.image_placeholder)
            .into(holder.binding.ivProduct)
        holder.binding.tvProductDistance.text = "${item.distance?.div(1000)} KM"
        holder.binding.tvProductName.text = item.name?.capitalize()
        holder.binding.tvProductLocation.text = item.condition

        /*with(holder.binding) {

            if (item.isGiven == 1) {
                //ivGiven.visibility = View.VISIBLE
                cv.elevation = 0f
//                clMain.background = null
            } else {
//                ivGiven.visibility = View.GONE
//                clMain.background = ContextCompat.getDrawable(context, R.drawable.bg_product)
                cv.elevation = 1f
            }

            tvProductName.text = item.title
            tvProductLocation.text = item.fullAddress

            ImageUtils.setImage(
                context,
                ivProduct,
                item.image ?: "",
                null,
                R.drawable.home_toolbar_app_logo
            )

        }*/

        holder.itemView.setOnClickListener {
            callback.onItemDetail(item, position)
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
        val binding = ItemProductBinding.bind(itemView)
    }

    interface LatestProductAdapterInterface {
        fun onItemDetail(data: Product, position: Int)
    }

}