package com.oss.abraakadabraaapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oss.abraakadabraaapp.activities.newflow.chat.AllChatsFragment
import com.oss.abraakadabraaapp.activities.newflow.chat.GivingChatsFragment
import com.oss.abraakadabraaapp.activities.newflow.chat.ReceivingChatsFragment
import com.oss.abraakadabraaapp.fragments.GiverFragment
import com.oss.abraakadabraaapp.fragments.ReceiverFragment

data class RequestTabAdapter(
    var fragmentManager: FragmentManager,
    var lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return  AllChatsFragment()
            1 -> return AllChatsFragment()
            //2 -> return ReceivingChatsFragment()
        }
        return  AllChatsFragment()
        /*when (position) {
            0 -> return  GiverFragment()
            1 -> return ReceiverFragment()
        }
        return  GiverFragment()*/
    }

    override fun getItemCount(): Int {
        return 3
    }
}