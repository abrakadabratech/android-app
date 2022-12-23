package com.oss.abraakadabraaapp.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.fragments.OnboardingFragment


class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment.newInstance(
                context.resources.getString(R.string.give),
                context.resources.getString(R.string.on_boarding_1),
                R.raw.onboarding_1,
                R.drawable.ic_onboarding_bottom_bg_1,
                R.drawable.onboarding_top_1,
                R.drawable.ic_onboarding_center_1,
                0
            )
            1 -> OnboardingFragment.newInstance(
                context.resources.getString(R.string.receive),
                context.resources.getString(R.string.on_boarding_2),
                R.raw.onboarding_2,
                R.drawable.ic_onboarding_bottom_bg_2,
                R.drawable.onboarding_top_2,
                null,
                1
            )
            else -> OnboardingFragment.newInstance(
                context.resources.getString(R.string.reuse),
                context.resources.getString(R.string.on_boarding_3),
                R.raw.onboarding_3,
                R.drawable.ic_onboarding_bottom_bg_3,
                R.drawable.onboarding_top_3,
                R.drawable.ic_onboarding_center_3,
                2
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}