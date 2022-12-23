package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemProductBinding
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.utils.ImageUtils


class SearchAdapter(
    private val data: ArrayList<LatestProductData>,
    var context: Context,
    private var callback: SearchAdapterInterface
) : RecyclerView.Adapter<SearchAdapter.SearchAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapterVH {
        return SearchAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchAdapterVH, position: Int) {

        val item = data[position]

        with(holder.binding) {

            /*if (item.isGiven == 1) {
                ivGiven.visibility = View.VISIBLE
                cv.elevation = 0f
                clMain.background = null
            } else {
                ivGiven.visibility = View.GONE
                clMain.background = ContextCompat.getDrawable(context, R.drawable.bg_product)
                cv.elevation = 1f
            }*/

            tvProductName.text = item.title
            tvProductLocation.text = item.fullAddress

            ImageUtils.setImage(
                context,
                ivProduct,
                item.image ?: "",
                progressBar,
                R.drawable.home_toolbar_app_logo
            )

        }

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

    class SearchAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductBinding.bind(itemView)
    }

    interface SearchAdapterInterface {
        fun onItemDetail(data: LatestProductData, position: Int)
    }

}