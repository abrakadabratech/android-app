package com.oss.abraakadabraaapp.datasource

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.databinding.ItemProductBinding

class ProductsAdapter : PagingDataAdapter<Product, ProductsAdapter.ViewHolder>(ProductDifferntiator) {
    lateinit var context: Context
    class ViewHolder(view: ItemProductBinding) : RecyclerView.ViewHolder(view.root){

        fun bind(item: Product?) {
            Log.d("TAG-", "bind: ${item?.name}")
            Glide.with(itemView.context).load(item?.image).into(itemView.rootView.findViewById<ImageView>(R.id.iv_product))
           itemView.rootView.findViewById<TextView>(R.id.tv_product_name).text = item?.name
           itemView.rootView.findViewById<TextView>(R.id.tv_product_location).text = item?.location.toString()
            itemView.rootView.setOnClickListener {
                val i=Intent(itemView.context,NewProductDetailActivity::class.java)
                i.putExtra("id",item?.id)
                itemView.context.startActivity(i)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
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
}