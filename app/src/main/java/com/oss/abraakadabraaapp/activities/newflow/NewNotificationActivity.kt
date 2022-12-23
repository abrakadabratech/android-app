package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.adapters.NewNotificationAdapter
import com.oss.abraakadabraaapp.databinding.ActivityNewNotificationBinding

class NewNotificationActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNewNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        binding.rvNotification.adapter = NewNotificationAdapter(this,4)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}