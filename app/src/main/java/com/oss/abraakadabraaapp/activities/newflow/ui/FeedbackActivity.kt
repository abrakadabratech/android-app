package com.oss.abraakadabraaapp.activities.newflow.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.customeview.ColorCollector
import com.oss.abraakadabraaapp.databinding.ActivityFeedbackBinding
import com.oss.abraakadabraaapp.utils.Constants

class FeedbackActivity : BaseActivity() {
    private lateinit var binding:ActivityFeedbackBinding
    lateinit var application: BaseActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_FEEDBACK,null)

        binding.seekbar.customSectionTrackColor { colorIntArr ->
            colorIntArr!![0] = Color.parseColor("#F95565")
            colorIntArr!![1] = Color.parseColor("#FE962F")
            colorIntArr!![2] = Color.parseColor("#FECE2F")
            colorIntArr!![3] = Color.parseColor("#03C437")
            colorIntArr!![4] = Color.parseColor("#2D7A76")

            true
        }
        binding.seekbar1.customSectionTrackColor { colorIntArr ->
            colorIntArr!![0] = Color.parseColor("#F95565")
            colorIntArr!![1] = Color.parseColor("#FE962F")
            colorIntArr!![2] = Color.parseColor("#FECE2F")
            colorIntArr!![3] = Color.parseColor("#03C437")
            colorIntArr!![4] = Color.parseColor("#2D7A76")

            true
        }
        binding.seekbar2.customSectionTrackColor { colorIntArr ->
            colorIntArr!![0] = Color.parseColor("#F95565")
            colorIntArr!![1] = Color.parseColor("#FE962F")
            colorIntArr!![2] = Color.parseColor("#FECE2F")
            colorIntArr!![3] = Color.parseColor("#03C437")
            colorIntArr!![4] = Color.parseColor("#2D7A76")

            true
        }
    }
}