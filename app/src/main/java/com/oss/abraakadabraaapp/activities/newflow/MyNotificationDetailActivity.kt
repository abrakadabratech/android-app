package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyNotificationDetailBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_NOTIFICATION_DETAILS

class MyNotificationDetailActivity : BaseActivity() {
    private lateinit var binding:ActivityMyNotificationDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_NOTIFICATION_DETAILS,null)

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_NOTIFICATION_DETAILS)
            onBackPressed()
        }
        binding.acceptBtn.setOnClickListener {
            postClick(Constants.BUTTON_ACCEPT_IN_NOTIFICATION)
            showToast("Under Development")
        }
        binding.rejectBtn.setOnClickListener {
            postClick(Constants.BUTTON_REJECT_IN_NOTIFICATION)
            showToast("Under Development")
        }
    }
}