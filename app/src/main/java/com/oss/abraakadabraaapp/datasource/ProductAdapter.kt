package com.oss.abraakadabraaapp.datasource

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.datasource.products.Product
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R

class ProductAdapter(val onClick: OnProductClicked) : PagingDataAdapter<Product, ProductAdapter.ViewHolder>(ProductDifferntiator) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var tv=itemView.rootView.findViewById<TextView>(R.id.tv_product_name)
        var iv=itemView.rootView.findViewById<ImageView>(R.id.iv_product)
        fun bind(item: Product?) {
            tv.text=item?.name
            Glide.with(itemView.context).load(item?.image).into(iv)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onClick.onProductClicked(getItem(position)!!)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        )
    }

    object ProductDifferntiator : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    interface OnProductClicked{
        fun onProductClicked(product: Product)
    }
}