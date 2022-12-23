package com.oss.abraakadabraaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.oss.abraakadabraaapp.adapter.MultipleImageAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMultipleImageBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.response.mainResponse.ProductImage
import com.oss.abraakadabraaapp.utils.Constants

class MultipleImageActivity : BaseActivity() {

    private lateinit var binding: ActivityMultipleImageBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding

    private var currentPosition: Int = 0
    private val imageList = ArrayList<String>()

    companion object{
        fun createIntent(context:Context, productImages:ArrayList<ProductImage>) : Intent{
            val intent = Intent(
                context,
                MultipleImageActivity::class.java
            )
            intent.putExtra(Constants.imageList, productImages)
            return  intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultipleImageBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        setSupportActionBar(includeToolbar.toolbar)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        val data = intent.getParcelableArrayListExtra<ProductImage>(Constants.imageList)!!

        for (item in data) {
            imageList.add(item.image)
        }

        val adapter = MultipleImageAdapter(this, imageList)

        with(binding) {
            viewPager.addOnPageChangeListener(myPageChangeCallback)
            viewPager.adapter = adapter
        }

        val value = "${binding.viewPager.currentItem + 1}/${imageList.size}"
        includeToolbar.imageCount.visibility = View.VISIBLE
        includeToolbar.imageCount.text = value

    }


    private var myPageChangeCallback = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            currentPosition = position
            binding.viewPager.currentItem = position

            val value = "${binding.viewPager.currentItem + 1}/${imageList.size}"
            includeToolbar.imageCount.text = value
        }

        override fun onPageScrollStateChanged(state: Int) {}

    }

}