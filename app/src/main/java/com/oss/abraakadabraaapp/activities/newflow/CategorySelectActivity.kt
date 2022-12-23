package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.activities.newflow.adapters.CategorySelectAdapter
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import com.oss.abraakadabraaapp.databinding.ActivityCategorySelectBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData

class CategorySelectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategorySelectBinding

    var list = arrayListOf<CatData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySelectBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tempLoadData()

        val adapter = CategorySelectAdapter(this, list)
        binding.catRecycler.layoutManager = LinearLayoutManager(this)
        binding.catRecycler.adapter = adapter

        binding.clearBtn.setOnClickListener {
            list.clear()
            tempLoadData()
            adapter.notifyDataSetChanged()
        }
        binding.applyBtn.setOnClickListener {
            startActivity(Intent(this, NewSearchActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun tempLoadData() {
        list.add(CatData("Electronics", false))
        list.add(CatData("Clothing", false))
        list.add(CatData("Book", false))
        list.add(CatData("Sports", false))
        list.add(CatData("Footwear", false))
        list.add(CatData("Fashion", false))
        list.add(CatData("Furniture", false))
        list.add(CatData("Home Decor", false))
        list.add(CatData("Kitchen & Appliance", false))
        list.add(CatData("Others", false))
    }

}