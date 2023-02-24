package com.oss.abraakadabraaapp.activities.newflow

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.adapter.RatingAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyAchievementsBinding
import com.oss.abraakadabraaapp.databinding.ItemImageBinding
import com.oss.abraakadabraaapp.databinding.RatingRowBinding
import com.oss.abraakadabraaapp.model.ProductImage
import com.oss.abraakadabraaapp.module.GlideApp
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

            val count = userInfo?.data?.userStats?.rating
            var list:ArrayList<Boolean> = ArrayList()
            for (i in 0..4){
                if (i < count!!) list.add(true)
                else list.add(false)
//                if (i< count!!){
//
//                }else list.add(false)
            }
            Log.d("Rating", "setupProfile: ${Gson().toJson(list)}")

            binding.imageView28.layoutManager = LinearLayoutManager(this@MyAchievementsActivity,LinearLayoutManager.HORIZONTAL,false)
            binding.imageView28.adapter = RatingAdapter(this@MyAchievementsActivity,list)
        }
    }

}