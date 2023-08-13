package com.oss.abraakadabraaapp.datasource

import android.util.Log
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

class ProductAdapter(val onClick: OnProductClicked) :
    PagingDataAdapter<Product, ProductAdapter.ViewHolder>(ProductDifferntiator) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var tv=itemView.rootView.findViewById<TextView>(R.id.tv_product_name)
        var tv_product_distance=itemView.rootView.findViewById<TextView>(R.id.tv_product_distance)
        var tv_product_location=itemView.rootView.findViewById<TextView>(R.id.tv_product_location)
        var iv=itemView.rootView.findViewById<ImageView>(R.id.iv_product)
        fun bind(item: Product?) {
            tv.text=item?.name?.capitalize()
            Glide.with(itemView.context).load(item?.display_image).placeholder(R.drawable.image_placeholder).into(iv)
            tv_product_distance.setText("${(item?.distance?.div(1000))} KM")
            tv_product_location.setText("${item?.condition}")

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            Log.d("NewReceiverFragment", "bind: ${getItem(position)?.name}")
            onClick.onProductClicked(getItem(position),position)
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
        fun onProductClicked(product: Product?,position: Int)
    }
}