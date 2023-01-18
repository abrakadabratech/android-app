package com.oss.abraakadabraaapp.datasource

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.databinding.ItemProductBinding

class ProductsAdapter :
    PagingDataAdapter<Data, ProductsAdapter.ProductViewHolder>(PassengersComparator) {

    lateinit var context:Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        context = parent.context
        return ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bindPassenger(it) }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindPassenger(item: Data) = with(binding) {
            Glide.with(context).load(item.products.get(0).imageUrl).into(ivProduct)
//            ivProduct.loadImage(item.airline.get(0).logo)
            tvProductName.text = item.products.get(0).name
            tvProductLocation.text = item.products.get(0).location.toString()
        }
    }

    object PassengersComparator : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.products.get(0).id == newItem.products.get(0).id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }
}