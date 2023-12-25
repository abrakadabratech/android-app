package com.oss.abraakadabraaapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.databinding.ActivityAllCategoryBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.databinding.NoDataLayoutBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AllCategoryActivity : BaseActivity(), CategoryAdapter.CategoryAdapterInterface {

    private lateinit var binding: ActivityAllCategoryBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding
    private lateinit var noDataBinding: NoDataLayoutBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private val mainViewModel: MainViewModel by viewModel()

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private val categoryAdapter = CategoryAdapter(categoryList, this, this,"")

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private lateinit var notificationCount:String

    private lateinit var latitude: String
    private lateinit var longitude: String

    companion object {
        fun createIntent(context: Context, notificationCount:String,    latitude: String,
                         longitude: String): Intent {
            val intent = Intent(context, AllCategoryActivity::class.java)
            intent.putExtra(Constants.notificationCount, notificationCount)

            intent.putExtra(Constants.latitude, latitude)
            intent.putExtra(Constants.longitude, longitude)

            Log.d("MYT", "latitude $latitude")
            Log.d("MYT", "longitude $longitude")

            return intent
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constants.success, notificationCount)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllCategoryBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        noDataBinding = binding.includeNoData
        val view = binding.root
        setContentView(view)

        latitude = intent.getStringExtra(Constants.latitude)!!
        longitude = intent.getStringExtra(Constants.longitude)!!

        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text =
            applicationContext.resources.getString(R.string.category)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(Constants.notificationCount)){
            notificationCount = intent.getStringExtra(Constants.notificationCount)!!
        }

        setUpRecyclerView()
        setUpObserver()
        getCategory()
    }

    private fun setUpObserver() {

        mainViewModel.getCategorySuccess.observe(this, {
            val data = it.data
            if (data.isNotEmpty()) {
                if (currentPage == pageStart) categoryList.clear()
//                categoryList.addAll(data)
                categoryAdapter.notifyDataSetChanged()
                noDataBinding.clNoData.visibility = View.GONE
                binding.sv.visibility = View.VISIBLE
                currentPage += 1
            } else {
                noDataFound()
            }
            isLoading = false
            isLastPage = false
            binding.llProgress.visibility = View.GONE
        })

        mainViewModel.isLoading.observe(this, {
            if (isLoading && isLastPage) {
                binding.llProgress.visibility = View.VISIBLE
            } else {
                binding.llProgress.visibility = View.GONE
                loader(it)
            }
        })

        mainViewModel.errorMessage.observe(this, { /*if (it.isNotBlank()) showToast(it)*/ })

    }

    private fun getCategory() {
        if (isNetworkAvailable()) {

            noDataBinding.clNoData.visibility = View.GONE
            binding.sv.visibility = View.VISIBLE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.pageNumber] = "$currentPage"

            mainViewModel.getCategory(Utility.getHeaders(this@AllCategoryActivity))
        } else {
            showSnackBar(
                binding.clAllCategory,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun setUpRecyclerView() {

        binding.rvCategory.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager = GridLayoutManager(this@AllCategoryActivity, 4)
            adapter = categoryAdapter
        }

        binding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

            val lastChild = binding.sv.getChildAt(binding.sv.childCount - 1)

            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - binding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
                    if (!noMoreData) {
                        isLoading = true
                        isLastPage = true
                        getCategory()
                    }
                }
            }
        }
    }

    private fun noDataFound() {
        if (!isLoading && !isLastPage) {
            noDataBinding.clNoData.visibility = View.VISIBLE
            noDataBinding.tvNoData.text =
                applicationContext.resources.getString(R.string.no_data_found)
            binding.sv.visibility = View.GONE
            noDataBinding.ivNoData.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.home_toolbar_app_logo
                )
            )
        }
    }

    private var launchSearchActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    notificationCount = data.getStringExtra(Constants.success)!!
                }
            }
        }

    override fun onCategoryClick(data: UserCatData,posi:Int) {
        launchSearchActivity.launch(
            SearchActivity.createIntent(
                this@AllCategoryActivity,
                data.id.toString(),
                data.title,
                notificationCount,
                latitude,
                longitude
            )
        )
    }
}