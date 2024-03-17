package com.oss.abraakadabraaapp.activities.newflow.ui.chats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.adapter.RequestTabAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyChatsBinding
import com.oss.abraakadabraaapp.databinding.FragmentAccountsBinding
import com.oss.abraakadabraaapp.databinding.FragmentChatsBinding
import com.oss.abraakadabraaapp.utils.Constants

class ChatsFragment : Fragment() {
    lateinit var application: BaseActivity

    private lateinit var binding: FragmentChatsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_CHATS,null)

        binding.pager.adapter = RequestTabAdapter(requireActivity().supportFragmentManager, lifecycle)

        TabLayoutMediator(
            binding.tabs, binding.pager
        ) { tab, position ->
            when (position) {
//                0 -> tab.text = "All"
                0 -> tab.text = "Selling"
                1 -> tab.text = "Buying"
            }
        }.attach()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return root
    }

}