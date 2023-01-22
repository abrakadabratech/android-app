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
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.ActivityContentManagementBinding
import com.oss.abraakadabraaapp.databinding.ActivityNewProductDetailBinding
import com.oss.abraakadabraaapp.databinding.ActivityNewSearchBinding
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration

class NewSearchActivity : BaseActivity() ,CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface{


    private lateinit var binding: ActivityNewSearchBinding

    private var categoryList: ArrayList<UserCatData> = ArrayList()
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
        postEvent(Constants.PAGE_SEARCH,null)

        categoryAdapter = CategoryAdapter(categoryList, this, this,"search")
        latestProductAdapter = LatestProductAdapter(latestProductList, this, this)

        setUpRecyclerView()
        clickEvents()


    }
    private fun clickEvents() {
        binding.catFilter.setOnClickListener {
            postEvent(Constants.BUTTON_FILTER_SEARCH,null)
            showNearByFilterDialog()
//            startActivity(Intent(requireContext(),CategorySelectActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onCategoryClick(data: UserCatData) {

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
            addItemDecoration(
                MarginItemDecoration(18)
            )
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

        applyBtn.setOnClickListener {
            postEvent(Constants.BUTTON_FILTER_APPLY_SEARCH,null)
            Toast.makeText(this, "Under Development", Toast.LENGTH_SHORT).show() }

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
        alertDialog.window?.setLayout(800, 700)

    }
}