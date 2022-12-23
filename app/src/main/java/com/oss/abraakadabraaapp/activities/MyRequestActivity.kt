package com.oss.abraakadabraaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.RequestTabAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyRequestBinding

class MyRequestActivity : BaseActivity() {

    lateinit var binding: ActivityMyRequestBinding

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MyRequestActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRequestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        with(binding) {

            ivBack.setOnClickListener {
                onBackPressed()
            }

            tvToolbarTitle.text = applicationContext.resources.getString(R.string.my_request)

            pager.adapter = RequestTabAdapter(supportFragmentManager, lifecycle)

            TabLayoutMediator(
                tabs, pager
            ) { tab, position ->
                when (position) {
                    0 -> tab.text = resources.getString(R.string.giving)
                    1 -> tab.text = resources.getString(R.string.receiving)
                }
            }.attach()

        }

    }
}