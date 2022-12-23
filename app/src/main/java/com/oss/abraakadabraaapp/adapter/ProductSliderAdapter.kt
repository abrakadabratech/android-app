package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.response.mainResponse.ProductImage
import com.oss.abraakadabraaapp.utils.ImageUtils

class ProductSliderAdapter(
    private val data: ArrayList<ProductImage>,
    var context: Context,
    private var callback: ProductSliderAdapterInterface
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = data[position]

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_product_slider, null)
        val ivProduct = view.findViewById<ImageView>(R.id.iv_product)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        ImageUtils.setImage(context, ivProduct, item.image, progressBar, R.drawable.home_toolbar_app_logo)

        ivProduct.setOnClickListener {
            callback.onProductImageClick(data)
        }

        val viewPager = container as ViewPager
        viewPager.addView(view, 0)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val viewPager = container as ViewPager
        val view = `object` as View
        viewPager.removeView(view)
    }

    interface ProductSliderAdapterInterface {
        fun onProductImageClick(data: ArrayList<ProductImage>)
    }

}