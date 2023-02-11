package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.databinding.ActivityMyAchievementsBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement

class MyAchievementsActivity : BaseActivity() {
    lateinit var application: BaseActivity

    private lateinit var binding : ActivityMyAchievementsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_ACHIEVEMENTS,null)

        setupProfile(PreferencesManagement.getUserInfo(this))
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupProfile(userInfo: GetUserResponse?) {
        with(binding){
            myRatingTxt.text = userInfo?.data?.userStats?.rating.toString()
            costSavingTxt.text = "Rs ."+ userInfo?.data?.userStats?.costSaving.toString()
            energySaving.text = "Rs ."+ userInfo?.data?.userStats?.energySaving.toString()
        }
    }
}