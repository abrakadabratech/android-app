package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemProductBinding
import com.oss.abraakadabraaapp.response.mainResponse.ProductData
import com.oss.abraakadabraaapp.utils.ImageUtils
import com.oss.abraakadabraaapp.utils.PreferencesManagement


class MyProductAdapter(
    private val data: ArrayList<ProductData>,
    var context: Context,
    private var callback: MyProductAdapterInterface
) : RecyclerView.Adapter<MyProductAdapter.MyProductAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductAdapterVH {
        return MyProductAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyProductAdapterVH, position: Int) {

        val userData = PreferencesManagement.getUserData(context)!!

        val item = data[position]

        with(holder.binding) {

            tvProductName.text = item.title
            tvProductLocation.text = item.fullAddress

           /* if (item.isGiven == 1) {
                ivGiven.visibility = View.VISIBLE
                cv.elevation = 0f
                clMain.background = null
            } else {
                ivGiven.visibility = View.GONE
                clMain.background = ContextCompat.getDrawable(context, R.drawable.bg_product)
                cv.elevation = 1f
            }*/

            if (item.userId == userData.id) {
                ivMenu.visibility = View.VISIBLE
            }

            ivMenu.setOnClickListener {
                menuDropDown(item, callback, ivMenu, position)
            }

            ImageUtils.setImage(
                context,
                ivProduct,
                item.image,
                progressBar,
                R.drawable.home_toolbar_app_logo
            )

        }

        holder.itemView.setOnClickListener {
            callback.onItemDetail(item, position)
        }

    }

    private fun menuDropDown(
        data: ProductData,
        callback: MyProductAdapterInterface,
        ivMenu: ImageView,
        position: Int,
    ) {
        val popup = PopupMenu(context, ivMenu)//TODO custom menu create

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> callback.onItemEdit(data, position)
                R.id.delete -> callback.onItemDelete(data, position)
            }
            popup.dismiss()
            true
        }

        popup.inflate(R.menu.item_product_menu)

        try{
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(mPopup,true)
        }catch (e:Exception){
            Log.d("MenuError", e.localizedMessage!!)
        }finally {
            popup.show()
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

    class MyProductAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemProductBinding.bind(itemView)
    }

    interface MyProductAdapterInterface {
        fun onItemDetail(data: ProductData, position: Int)
        fun onItemEdit(data: ProductData, position: Int)
        fun onItemDelete(data: ProductData, position: Int)
    }

}