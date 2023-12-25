package com.oss.abraakadabraaapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.SearchAdapter
import com.oss.abraakadabraaapp.databinding.ActivitySearchBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.databinding.NoDataLayoutBinding
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : BaseActivity(), SearchAdapter.SearchAdapterInterface {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding
    private lateinit var noDataBinding: NoDataLayoutBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private var categoryId = ""
    private var categoryName = ""
    private var productAge = ""
    private var productCondition = ""
    private var productGender = ""
    private var fullAddress = ""

    private var latitude = ""
    private var longitude = ""

    private lateinit var notificationCount: String

    private val mainViewModel: MainViewModel by viewModel()

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private lateinit var productData: LatestProductData
    private lateinit var position: String

    private var searchList: ArrayList<LatestProductData> = ArrayList()
    private val searchAdapter = SearchAdapter(searchList, this, this)

    companion object {
        fun createIntent(
            context: Context,
            categoryId: String?,
            categoryName: String?,
            notificationCount: String,
            latitude: String,
            longitude: String
        ): Intent {
            val intent = Intent(context, SearchActivity::class.java)
            if (categoryId != null && categoryName != null) {
                intent.putExtra(Constants.categoryId, categoryId)
                intent.putExtra(Constants.categoryName, categoryName)
            }
            intent.putExtra(Constants.notificationCount, notificationCount)

            Log.d("MYT", "latitude $latitude")
            Log.d("MYT", "longitude $longitude")

            intent.putExtra(Constants.latitude, latitude)
            intent.putExtra(Constants.longitude, longitude)
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
        binding = ActivitySearchBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        noDataBinding = binding.includeNoData
        val view = binding.root
        setContentView(view)

        with(includeToolbar) {
            setSupportActionBar(toolbar)

            latitude =
                if (intent.hasExtra(Constants.latitude) && intent.getStringExtra(Constants.latitude) != null) {
                    intent.getStringExtra(Constants.latitude)!!
                } else {
                    ""
                }
            longitude =
                if (intent.hasExtra(Constants.longitude) && intent.getStringExtra(Constants.longitude) != null) {
                    intent.getStringExtra(Constants.longitude)!!
                } else {
                    ""
                }

            ivBack.setOnClickListener { onBackPressed() }

            if (intent.hasExtra(Constants.categoryId)) {
                categoryId = intent.getStringExtra(Constants.categoryId)!!
                categoryName = intent.getStringExtra(Constants.categoryName)!!
                tvToolbarTitle.text = categoryName
            } else {
                tvToolbarTitle.text =
                    applicationContext.resources.getString(R.string.search_str)
            }

            clNotificationContent.visibility = View.VISIBLE

            ivFilter.setOnClickListener {
                val intent = Intent(this@SearchActivity, FilterActivity::class.java)
                intent.putExtra(Constants.categoryName, categoryName)
                intent.putExtra(Constants.categoryId, categoryId)
                intent.putExtra(Constants.productAge, productAge)
                intent.putExtra(Constants.productCondition, productCondition)
                intent.putExtra(Constants.productGender, productGender)
                intent.putExtra(Constants.fullAddress, fullAddress)
                intent.putExtra(Constants.latitude, latitude)
                intent.putExtra(Constants.longitude, longitude)

                launchFilterActivity.launch(intent)
            }

            if (intent.hasExtra(Constants.notificationCount)) {
                notificationCount = intent.getStringExtra(Constants.notificationCount)!!
                if (notificationCount != "0") {
                    notificationActive.visibility = View.VISIBLE
                } else {
                    notificationActive.visibility = View.INVISIBLE
                }
            }

            ivNotification.setOnClickListener {
                locationIntent()
            }

        }

        with(binding) {
            ivClear.setOnClickListener {
                if (etSearch.text.toString().trim().isNotBlank()) {
                    etSearch.setText("")
                } else {
                    currentPage = pageStart
                    isLoading = false
                    isLastPage = false
                    noMoreData = false
                    searchAdapter.clearData()
                    getSearchList()
                }
            }

            sRLHome.setColorSchemeResources(R.color.theme_color)

            sRLHome.setOnRefreshListener {
                currentPage = pageStart
                searchAdapter.clearData()
                noMoreData = false
                isLastPage = false
                isLoading = false
                getSearchList()
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s!!.isNotBlank()) {
                        ivClear.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@SearchActivity,
                                R.drawable.ic_close_theme
                            )
                        )
                    } else {
                        ivClear.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@SearchActivity,
                                R.drawable.ic_search
                            )
                        )
                    }
                }
            })

            etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    currentPage = pageStart
                    isLoading = false
                    isLastPage = false
                    noMoreData = false
                    searchAdapter.clearData()
                    getSearchList()
                    return@OnEditorActionListener true
                }
                false
            })

        }



        setUpRecyclerView()
        setUpObserver()
        getSearchList()
    }

    private fun setUpObserver() {

        mainViewModel.searchListSuccess.observe(this) {
            val data = it.data
            if (data.isNotEmpty()) {
                if (currentPage == pageStart) searchList.clear()
                searchList.addAll(data)
                searchAdapter.notifyDataSetChanged()
                noDataBinding.clNoData.visibility = View.GONE
                binding.rvSearch.visibility = View.VISIBLE

                if (currentPage != pageStart && data.size >= 1) {

                        val lastPosition = searchList.size - data.size

                        if (searchList.size == data.size) {
                            binding.rvSearch.smoothScrollToPosition(searchList.size)
                        } else {
                            binding.rvSearch.smoothScrollToPosition(lastPosition + 1)
                        }
                }

                currentPage += 1

            } else {
                noDataFound()
            }

            isLoading = false
            isLastPage = false
            binding.llProgress.visibility = View.GONE
            binding.sRLHome.isRefreshing = false
        }

        mainViewModel.isLoading.observe(this, {
            if (isLoading && isLastPage) {
                binding.llProgress.visibility = View.VISIBLE
            } else {
                binding.llProgress.visibility = View.GONE
                if (!binding.sRLHome.isRefreshing) {
                    loader(it)
                }
            }
        })

        mainViewModel.errorMessage.observe(this, {
            if (it.isNotBlank()) {
//                showToast(it)
                binding.sRLHome.isRefreshing = false
            }
        })

    }

    private fun getSearchList() {
        if (isNetworkAvailable()) {

            if (binding.etSearch.text.toString().trim().isNotBlank()) {
                binding.llResultFor.visibility = View.VISIBLE
                val str = " \"${binding.etSearch.text.toString().trim()}\""
                binding.tvResultFor.text = str
            } else {
                binding.llResultFor.visibility = View.GONE
                binding.tvResultFor.text = ""
            }

            if (categoryName.isNotBlank()) {
                includeToolbar.tvToolbarTitle.text = categoryName
            }

            noDataBinding.clNoData.visibility = View.GONE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.pageNumber] = "$currentPage"
            map[RequestKeys.searchKey] = binding.etSearch.text.toString().trim()

            if ((latitude.isEmpty() && longitude.isEmpty()) && PreferencesManagement.getUserLocation(
                    this@SearchActivity
                ) != null
            ) {
                val userLocation = PreferencesManagement.getUserLocation(this@SearchActivity)!!
                latitude = userLocation.lat
                longitude = userLocation.long
            }

            Log.d("MYT", "latitude $latitude")
            Log.d("MYT", "longitude $longitude")

            map[RequestKeys.lat] = latitude
            map[RequestKeys.lng] = longitude

            map[RequestKeys.categoryId] = categoryId
            map[RequestKeys.genderFor] = productGender
            map[RequestKeys.productAge] = productAge
            map[RequestKeys.distance] = "50"

            mainViewModel.getSearchList(Utility.getHeaders(this@SearchActivity), map)
        } else {
            showSnackBar(
                binding.clSearchActivity,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
            binding.sRLHome.isRefreshing = false
        }
    }

    override fun onItemDetail(data: LatestProductData, position: Int) {
        productData = data
        this.position = position.toString()
        val intent = Intent(this@SearchActivity, ProductDetailActivity::class.java)
        intent.putExtra(Constants.productId, data.id.toString())
        intent.putExtra(Constants.newRequest, Constants.newRequest)
        launchProductDetailActivity.launch(intent)
    }

    private var launchProductDetailActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    when (data.getStringExtra(Constants.success)!!) {
                        Constants.success -> {
                            productData = data.getParcelableExtra(Constants.editProduct)!!
                            searchList[position.toInt()].fullAddress = productData.fullAddress
                            searchList[position.toInt()].title = productData.title
                            searchList[position.toInt()].image = productData.image
                            searchList[position.toInt()].productId = productData.productId
                            searchList[position.toInt()].userId = productData.userId
                        }
                        Constants.failure -> {
                            searchList.removeAt(position.toInt())
                        }
                    }
                    searchAdapter.notifyDataSetChanged()
                }
            }
        }

    private fun noDataFound() {
        noMoreData = true
        if (!isLoading && !isLastPage) {
            noDataBinding.clNoData.visibility = View.VISIBLE
            noDataBinding.tvNoData.text =
                applicationContext.resources.getString(R.string.search_no_data_message)
            binding.rvSearch.visibility = View.GONE
            noDataBinding.ivNoData.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.home_toolbar_app_logo
                )
            )
        }
    }

    private fun setUpRecyclerView() {
        val lm = GridLayoutManager(this@SearchActivity, 2)

        binding.rvSearch.apply {
            layoutManager = lm
            adapter = searchAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }

//        binding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
//
//            val lastChild = binding.sv.getChildAt(binding.sv.childCount - 1)
//
//            if (lastChild != null) {
//                if ((scrollY >= (lastChild.measuredHeight - binding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
//                    if (!noMoreData) {
//                        isLoading = true
//                        isLastPage = true
//                        getSearchList()
//                    }
//                }
//            }
//        }

        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {

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
                            getSearchList()
                        }
                    }
                }
            }
        })
    }

    private fun locationIntent() {
        launchNotificationActivity.launch(
            Intent(
                this@SearchActivity,
                NotificationActivity::class.java
            )
        )
    }

    private var launchFilterActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    latitude = data.getStringExtra(Constants.latitude)!!
                    longitude = data.getStringExtra(Constants.longitude)!!
                    fullAddress = data.getStringExtra(Constants.fullAddress)!!
                    categoryId = data.getStringExtra(Constants.categoryId)!!
                    categoryName = data.getStringExtra(Constants.categoryName)!!
                    productAge = data.getStringExtra(Constants.productAge)!!
                    productCondition = data.getStringExtra(Constants.productCondition)!!
                    productGender = data.getStringExtra(Constants.productGender)!!

                    includeToolbar.tvToolbarTitle.text = if (categoryId.isNotBlank()) {
                        categoryName
                    } else {
                        applicationContext.resources.getString(R.string.search_str)
                    }

                    currentPage = pageStart
                    searchAdapter.clearData()
                    noMoreData = false
                    isLastPage = false
                    isLoading = false
                    getSearchList()
                }
            }
        }

    private var launchNotificationActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    notificationCount = data.getStringExtra(Constants.success)!!
                    if (data.getStringExtra(Constants.success)!! != "0") {
                        includeToolbar.notificationActive.visibility = View.VISIBLE
                    } else {
                        includeToolbar.notificationActive.visibility = View.INVISIBLE
                    }
                }
            }
        }

}