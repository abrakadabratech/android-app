package com.oss.abraakadabraaapp.datasource

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.databinding.ItemProductBinding

class ProductsAdapter : PagingDataAdapter<Products, ProductsAdapter.ProductViewHolder>(PassengersComparator) {

    lateinit var context:Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        context = parent.context
        Log.d("TAG-", "adapter")
        return ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        getItem(position).let {
            Log.d("TAG-", "bindPassenger: ${it!!.name}")
            holder.bindPassenger(it)
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPassenger(item: Products) = with(binding) {
            Log.d("TAG-", "bindPassenger: ${item.name}")
            Glide.with(context).load(item.imageUrl).into(ivProduct)
//            ivProduct.loadImage(item.airline.get(0).logo)
            tvProductName.text = item.name
            tvProductLocation.text = item.location.toString()
        }
    }

    object PassengersComparator : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }
}