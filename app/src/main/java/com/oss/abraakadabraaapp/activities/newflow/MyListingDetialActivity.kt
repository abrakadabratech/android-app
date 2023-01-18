package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestedUsersAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyListingBinding
import com.oss.abraakadabraaapp.databinding.ActivityMyListingDetailsBinding
import com.oss.abraakadabraaapp.utils.Constants

class MyListingDetialActivity : BaseActivity() {
    lateinit var application: BaseActivity

    private lateinit var binding:ActivityMyListingDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_LISTING_DETAIL,null)

        tempData()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun tempData() {
//        Glide.with(this).load("https://www.gstatic.com/webp/gallery/1.jpg").into(binding.imageSlider)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.FIT))
        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "", ScaleTypes.FIT))

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)

        val rvRequestedUsers = findViewById<RecyclerView>(R.id.rvRequestedUsers)
        rvRequestedUsers.layoutManager = LinearLayoutManager(this)

        var adapter = MyRequestedUsersAdapter(this,3)
        rvRequestedUsers.adapter = adapter
    }
}