package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyPayAsYouGoBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_100
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_200
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_500
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_PAYASWISH
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CONTRIBUTE

class MyPayAsYouGoActivity : BaseActivity() {

    private lateinit var binding:ActivityMyPayAsYouGoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPayAsYouGoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_PAYASWISH)
            onBackPressed()
        }
        binding.button3.setOnClickListener {
            postClick(BUTTON_100)
            showToast(Constants.UNDER_DEV)
        }
        binding.button5.setOnClickListener {
            postClick(BUTTON_200)
            showToast(Constants.UNDER_DEV) }
        binding.button6.setOnClickListener {
            postClick(BUTTON_500)
            showToast(Constants.UNDER_DEV) }
        binding.button7.setOnClickListener {
            postClick(BUTTON_CONTRIBUTE)
            showToast(Constants.UNDER_DEV) }
    }
}