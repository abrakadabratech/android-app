package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.adapter.RequestTabAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyChatsBinding
import com.oss.abraakadabraaapp.utils.Constants

class MyChatsActivity : BaseActivity() {
    lateinit var application: BaseActivity

    private lateinit var binding:ActivityMyChatsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_CHATS,null)

        var pager = findViewById<ViewPager2>(R.id.pager)
        var tabs = findViewById<TabLayout>(R.id.tabs)
        pager.adapter = RequestTabAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(
            tabs, pager
        ) { tab, position ->
            when (position) {
//                0 -> tab.text = "All"
                0 -> tab.text = "Giving"
                1 -> tab.text = "Receiving"
            }
        }.attach()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}