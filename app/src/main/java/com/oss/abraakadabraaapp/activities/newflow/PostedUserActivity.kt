package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityPostedUserBinding

class PostedUserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPostedUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_posted_user)

        binding = ActivityPostedUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.submitBtn.setOnClickListener {
            binding.successAlertDialog.visibility = View.VISIBLE
        }

        binding.successOkBtn.setOnClickListener {
//            startActivity(Intent(this,MyListingActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}