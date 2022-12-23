package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.ActivityContentManagementBinding
import com.oss.abraakadabraaapp.databinding.ActivityNewProductDetailBinding
import com.oss.abraakadabraaapp.databinding.ActivityNewSearchBinding
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData

class NewSearchActivity : AppCompatActivity() ,CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface{


    private lateinit var binding: ActivityNewSearchBinding

    private var categoryList: ArrayList<CategoryData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter
    private var pageStart = 1
    private var currentPage = pageStart
    private var noMoreData = false
    private var isLoading = false
    private var isLastPage = false

    private var latestProductList: ArrayList<LatestProductData> = ArrayList()
    private lateinit var latestProductAdapter: LatestProductAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        categoryAdapter = CategoryAdapter(categoryList, this, this,"search")
        latestProductAdapter = LatestProductAdapter(latestProductList, this, this)

        setUpRecyclerView()
        clickEvents()


    }
    private fun clickEvents() {
        binding.catFilter.setOnClickListener {
            showNearByFilterDialog()
//            startActivity(Intent(requireContext(),CategorySelectActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
    private fun getHomeData() {

        var catList : ArrayList<CategoryData> = ArrayList()
        var cat = CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Electronics",
            R.drawable.temp_one,
            "2021-11-22 19:14:06",
            1,
            ""
        )

        categoryList.add(cat)
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Books",
            R.drawable.temp_two,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Furniture",
            R.drawable.temp_three,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
//        var cat = CategoryData("https://www.gstatic.com/webp/gallery/1.jpg",
//            "Electronics",
//            "https://abrakadabraapp.app/app/user_assets/category/electronic.png","2021-11-22 19:14:06",
//            1,"")
//
//        categoryList.add(cat)


        var prod = LatestProductData(6.921098996597988,
            "Barely Used",
            240,
            "https://www.gstatic.com/webp/gallery/1.jpg",
            220,
            2,
            "Beautiful Toy",
            2
        )
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)

        categoryAdapter.notifyDataSetChanged()
        latestProductAdapter.notifyDataSetChanged()

//        binding.sRLHome.isRefreshing = false

    }

    override fun onCategoryClick(data: CategoryData) {

    }

    override fun onItemDetail(data: LatestProductData, position: Int) {
        startActivity(Intent(this,NewProductDetailActivity::class.java))
    }

    private fun setUpRecyclerView() {

        val lm = GridLayoutManager(this, 2)
//        binding.rvHomeCategory.isNestedScrollingEnabled = false
//        binding.rvLatestProduct.isNestedScrollingEnabled = false
        binding.rvHomeCategory.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            recycledViewPool.setMaxRecycledViews(1, 0)
//            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }

        binding.rvLatestProduct.apply {
            layoutManager = lm
            adapter = latestProductAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }

        binding.rvLatestProduct.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, oldScrollY: Int) {
                super.onScrolled(recyclerView, dx, oldScrollY)
                Log.d("scroll", "scrolling")
                val total: Int = lm.itemCount
                val lastVisibleItemCount: Int = lm.findLastVisibleItemPosition()
                if (!isLoading) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
                        if (!noMoreData) {
                            isLoading = true
                            isLastPage = true
                            //getHomeData()
                        }
                    }
                }
            }
        })
        getHomeData()
    }
    //Alert Dialog for show nearby filter
    private fun showNearByFilterDialog() {

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_nearby_filter, null)
        dialogBuilder.setView(dialogView)

        val nearToMeTxt = dialogView.findViewById<TextView>(R.id.nearToMeTxt)
        val nearToMeSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.nearToMeSwitch)

        val newestFirstTxt = dialogView.findViewById<TextView>(R.id.newrstFirstTxt)
        val newestFirstSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.newestFirstSwitch)

        val applyBtn = dialogView.findViewById<TextView>(R.id.applyBtn)

        nearToMeSwitch.addOnStatusChangedListener {
            if (it) nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        }

        newestFirstSwitch.addOnStatusChangedListener {
            if (it) newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        }

        applyBtn.setOnClickListener { Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show() }

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }
}