package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.ortiz.touchview.TouchImageView
import com.oss.abraakadabraaapp.R
import java.util.ArrayList

class MultipleImageAdapter(var mContext: Context, private val urlList: ArrayList<String>) :
    PagerAdapter() {
    var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater.inflate(R.layout.fragment_placeholder, container, false)
        val imageView: TouchImageView = itemView.findViewById(R.id.imageView)

        Glide.with(mContext)
            .load(urlList[position])
            .placeholder(R.drawable.home_toolbar_app_logo)
            .into(imageView)
        imageView.maxZoom = 4f
        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return urlList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

}