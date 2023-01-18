package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyAchievementsBinding
import com.oss.abraakadabraaapp.utils.Constants

class MyAchievementsActivity : BaseActivity() {
    lateinit var application: BaseActivity

    private lateinit var binding : ActivityMyAchievementsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_ACHIEVEMENTS,null)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}