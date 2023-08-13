package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemImageBinding
import com.oss.abraakadabraaapp.model.ProductImage
import com.oss.abraakadabraaapp.module.GlideApp
import com.oss.abraakadabraaapp.utils.ItemMoveCallback
import java.util.Collections

class ImageAdapter(
    private val data: ArrayList<ProductImage>,
    var context: Context,
    private var callback: ImageAdapterInterface
) :
    RecyclerView.Adapter<ImageAdapter.PostImageVH>(), ItemMoveCallback.ItemTouchHelperContract {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageVH {
        val v = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return PostImageVH(v)
    }

    override fun onBindViewHolder(holder: PostImageVH, position: Int) {

        if (position == 0){
            holder.itemView.tag = "-1"
        }



        val item = data[position]

        with(holder.binding){
            if (position == 1){
                holder.binding.llAddProductImage.background = context.resources.getDrawable(R.drawable.filled_dotted_border)
            }else{
                holder.binding.llAddProductImage.background = context.resources.getDrawable(R.drawable.ic_add_image_frame)
            }
            if (position == data.size){
                clMainImage.visibility = View.GONE
                llDeleteBtn.visibility = View.GONE
                llAddProductImage.visibility = View.VISIBLE
            }else{
                clMainImage.visibility = View.VISIBLE
                llDeleteBtn.visibility = View.VISIBLE

                llAddProductImage.visibility = View.GONE
                if (item.uri != null) {
                    GlideApp.with(context)
                        .load(item.uri ?: item.image)
                        .into(ivProductImage)
                }else{
                    GlideApp.with(context)
                        .load(item.image)
                        .into(ivProductImage)
                }
            }

            if (item.id != -2) {

                clMainImage.visibility = View.VISIBLE
                llDeleteBtn.visibility = View.VISIBLE

                llAddProductImage.visibility = View.GONE

                GlideApp.with(context)
                    .load(item.uri ?: item.image)
                    .into(ivProductImage)

            } else {
                clMainImage.visibility = View.GONE
                llDeleteBtn.visibility = View.GONE
                llAddProductImage.visibility = View.VISIBLE
            }

            llDeleteBtn.setOnClickListener {
                callback.onItemRemove(position, item)
            }

            llAddProductImage.setOnClickListener {
                callback.addProductImage()
            }

        }
    }


    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    class PostImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemImageBinding.bind(itemView)
    }

    interface ImageAdapterInterface {
        fun onItemRemove(position: Int, data: ProductImage)
        fun addProductImage()

        fun sorted(list: ArrayList<ProductImage>)
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
                callback.sorted(data)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
                callback.sorted(data)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }


}