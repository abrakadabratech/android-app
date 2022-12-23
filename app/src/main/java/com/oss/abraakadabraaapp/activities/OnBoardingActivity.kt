package com.oss.abraakadabraaapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.databinding.ActivityOnBoardingBinding
import com.oss.abraakadabraaapp.adapter.OnboardingViewPagerAdapter

class OnBoardingActivity : BaseActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    private lateinit var mViewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor =
            ContextCompat.getColor(
                this@OnBoardingActivity,
                R.color.blue_status_bar_color
            )

        binding.tvSkip.setOnClickListener {
            skip()
        }

        initSlider()

    }

    private fun skip() {
        startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
    }

    private fun initSlider() {
        mViewPager = binding.viewPager

        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
        mViewPager.offscreenPageLimit = 1

        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.fabPrevious.visibility = View.INVISIBLE
                } else {
                    binding.fabPrevious.visibility = View.VISIBLE
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })

        binding.indicator.attachTo(mViewPager)

        binding.fabNext.setOnClickListener {
            if (getItem() <= mViewPager.childCount) {
                mViewPager.setCurrentItem(getItem() + 1, true)
            } else {
                skip()
            }
        }

        binding.fabPrevious.setOnClickListener {
            if (getItem() != 0) {
                mViewPager.setCurrentItem(getItem() - 1, true)
            }
        }

    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }
}