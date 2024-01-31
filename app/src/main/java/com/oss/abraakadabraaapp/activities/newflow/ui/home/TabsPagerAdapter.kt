package com.oss.abraakadabraaapp.activities.newflow.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return NewReceiverFragment()
            1 -> return NewGiverFragment()
        }
        return NewReceiverFragment()
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }

    companion object {
        private const val NUM_PAGES = 2 // Set the number of pages
    }
}