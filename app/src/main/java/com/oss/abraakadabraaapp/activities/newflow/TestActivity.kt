package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.ActivityCategorySelectBinding
import com.oss.abraakadabraaapp.databinding.ActivityTestBinding
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData

class TestActivity : AppCompatActivity(),CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface  {
    private lateinit var binding: ActivityTestBinding
//    private val binding get() = _binding!!
    private var latestProductList: ArrayList<LatestProductData> = ArrayList()
    private lateinit var latestProductAdapter: LatestProductAdapter

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryAdapter = CategoryAdapter(categoryList, this, this, "")
        latestProductAdapter = LatestProductAdapter(latestProductList, this, this)

        setUpRecyclerView()

    }

    override fun onCategoryClick(data: UserCatData) {
        TODO("Not yet implemented")
    }

    override fun onItemDetail(data: LatestProductData, position: Int) {
        TODO("Not yet implemented")
    }


    private fun setUpRecyclerView() {
//        val view = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

//        view.visibility = View.GONE
//        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        val lm = GridLayoutManager(this, 2)
//        binding.rvHomeCategory.isNestedScrollingEnabled = false

        binding.rvHomeCategory.apply {
            layoutManager =
                LinearLayoutManager(this@TestActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            recycledViewPool.setMaxRecycledViews(1, 0)
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }

        binding.rvLatestProduct.apply {
            layoutManager = lm
            adapter = latestProductAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }
        var firstVisibleInListview: Int

        binding.rvLatestProduct.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, oldScrollY: Int) {
                super.onScrolled(recyclerView, dx, oldScrollY)
                val total: Int = lm.itemCount
                val lastVisibleItemCount: Int = lm.findLastVisibleItemPosition()


//                if (total == lastVisibleItemCount) {
//
//                }


            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                /*if (!recyclerView.canScrollVertically(1)) {
                    EventBus.getDefault().post(0)
                }else EventBus.getDefault().post(1)*/
            }
        })
    }

}