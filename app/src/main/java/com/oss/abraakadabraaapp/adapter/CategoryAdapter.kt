package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.content.Intent
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
import com.oss.abraakadabraaapp.activities.newflow.CategorySelectActivity
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.databinding.ItemCategory2Binding
import com.oss.abraakadabraaapp.databinding.ItemCategoryBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.module.GlideApp

class CategoryAdapter(
    private var data: ArrayList<UserCatData>,
    var context: Context,
    private var callback: CategoryAdapterInterface,
    private var keyFrom: String
) : RecyclerView.Adapter<CategoryAdapter.CategoryAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapterVH {
        return CategoryAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_category2, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryAdapterVH, position: Int) {

        val item = data[position]

        with(holder.binding) {

            tvCategoryName.text = item.title?.capitalize()?.trim()

            if (!keyFrom.equals("search")) {
                checkFrame.visibility = View.GONE
            } else {
                checkFrame.visibility = View.VISIBLE
            }
            if (position == data.size-1){
                Glide.with(context).load(R.drawable.temp_seven).into(ivCategoryImage)
            }else  Glide.with(context).load(item.image).into(ivCategoryImage)
            if (keyFrom.equals("search")){
                Glide.with(context).load(item.image).into(ivCategoryImage)
            }

        }

        holder.itemView.setOnClickListener {
            if (!keyFrom.equals("search")){
                if (position == data.size-1){
                    callback.onCategoryClick(item,0)
//                    context.
                }else  callback.onCategoryClick(item,1)
            }else{
                //callback.onCategoryClick(item)
            }
        }
    }


    override fun getItemCount() = data.size

  /*  override fun getItemViewType(position: Int): Int {
        return 1
    }
*/

    fun setData(list:ArrayList<UserCatData>) {
        data = list
    }

    class CategoryAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemCategory2Binding.bind(itemView)
    }

    interface CategoryAdapterInterface {
        fun onCategoryClick(data: UserCatData,position: Int)
    }

}