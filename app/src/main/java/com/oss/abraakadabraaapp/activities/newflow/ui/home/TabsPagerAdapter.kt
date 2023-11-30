package com.oss.abraakadabraaapp.activities.newflow.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        when(position){
            1 -> return NewReceiverFragment()
            2 -> return NewGiverFragment()
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