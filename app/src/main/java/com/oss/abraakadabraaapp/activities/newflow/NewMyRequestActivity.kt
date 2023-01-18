package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestAdapter
import com.oss.abraakadabraaapp.databinding.ActivityNewMyRequestBinding
import com.oss.abraakadabraaapp.utils.Constants

class NewMyRequestActivity : BaseActivity() {
    private lateinit var binding: ActivityNewMyRequestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postEvent(Constants.PAGE_MY_REQUEST,null)

        binding = ActivityNewMyRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpRecyclerView()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpRecyclerView() {
        var adapter = MyRequestAdapter(this,4)
        binding.rvMyrequest.layoutManager = LinearLayoutManager(this)
        binding.rvMyrequest.adapter = adapter
    }
}