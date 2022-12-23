package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemHomeCategoryBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.module.GlideApp

class HomeCategoryAdapter(
    private val data: ArrayList<CategoryData>,
    var context: Context,
    private var callback: HomeCategoryAdapterInterface
) : RecyclerView.Adapter<HomeCategoryAdapter.HomeCategoryAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryAdapterVH {
        return HomeCategoryAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_home_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeCategoryAdapterVH, position: Int) {

        val item = data[position]

        with(holder.binding){

            tvCategoryName.text = item.categoryName.trim()

            progressBar.visibility = View.VISIBLE

            GlideApp.with(context)
                .load(item.categoryImage)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .error(item.image)
                .into(ivCategoryImage)

        }

        holder.itemView.setOnClickListener {
            callback.onCategoryClick(item)
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

    class HomeCategoryAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemHomeCategoryBinding.bind(itemView)
    }

    interface HomeCategoryAdapterInterface {
        fun onCategoryClick(data: CategoryData)
    }

}