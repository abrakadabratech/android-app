package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyListingAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyListingBinding
import com.oss.abraakadabraaapp.utils.Constants

class MyListingActivity : BaseActivity() {
    private lateinit var binding:ActivityMyListingBinding
    lateinit var application: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_LISTING,null)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpRecyclerView() {
        var adapter = MyListingAdapter(this,4)
        binding.rvMyListing.layoutManager = LinearLayoutManager(this)
        binding.rvMyListing.adapter = adapter
    }

}