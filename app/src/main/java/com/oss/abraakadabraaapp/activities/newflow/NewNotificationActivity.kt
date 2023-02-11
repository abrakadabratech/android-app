package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.NewNotificationAdapter
import com.oss.abraakadabraaapp.databinding.ActivityNewNotificationBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_NOTIFICATIONS

class NewNotificationActivity : BaseActivity() {
    private lateinit var binding:ActivityNewNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_NOTIFICATIONS,null)

        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        //binding.rvNotification.adapter = NewNotificationAdapter(this,4)

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_NOTIFICATIONS)
            onBackPressed()
        }
    }
}