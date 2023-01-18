package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyPayAsYouGoBinding
import com.oss.abraakadabraaapp.utils.Constants

class MyPayAsYouGoActivity : BaseActivity() {

    private lateinit var binding:ActivityMyPayAsYouGoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPayAsYouGoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.button3.setOnClickListener {
            showToast(Constants.UNDER_DEV)
        }
        binding.button5.setOnClickListener { showToast(Constants.UNDER_DEV) }
        binding.button6.setOnClickListener { showToast(Constants.UNDER_DEV) }
        binding.button7.setOnClickListener { showToast(Constants.UNDER_DEV) }
    }
}