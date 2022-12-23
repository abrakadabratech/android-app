package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityMyProductBinding
import com.oss.abraakadabraaapp.databinding.ActivityMyProfile2Binding
import com.oss.abraakadabraaapp.databinding.ActivityMyProfileBinding

class MyNewProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyProfile2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfile2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editProfile.setOnClickListener {
            binding.nameEdit.isEnabled = true
            binding.emailEdit.isEnabled = true
            binding.phoneEdit.isEnabled = true
            binding.locationEdit.isEnabled = true
            binding.instaEdit.isEnabled = true
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}