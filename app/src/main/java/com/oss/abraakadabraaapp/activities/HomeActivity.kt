package com.oss.abraakadabraaapp.activities

import Data
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.adapter.NavigationAdapter
import com.oss.abraakadabraaapp.databinding.ActivityHomeBinding
import com.oss.abraakadabraaapp.databinding.ContentHomeBinding
import com.oss.abraakadabraaapp.databinding.NavigationHeaderBinding
import com.oss.abraakadabraaapp.databinding.NoDataHomeLayoutBinding
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.location.utils.LocationUtil
import com.oss.abraakadabraaapp.model.NavItem
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.ImageUtils
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity(),
    NavigationAdapter.NavigationItemInterface,
    CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var contentHomeBinding: ContentHomeBinding
    private lateinit var navHeaderBinding: NavigationHeaderBinding
    private lateinit var noDataBinding: NoDataHomeLayoutBinding

    private var navItemList: ArrayList<NavItem> = ArrayList()
    private val navigationAdapter = NavigationAdapter(navItemList, this, this)

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private val categoryAdapter = CategoryAdapter(categoryList, this, this,"")

    private var latestProductList: ArrayList<Data> = ArrayList()
    private val latestProductAdapter = LatestProductAdapter(latestProductList, this, this)

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private var isGPSEnabled = false

    private val locationViewModel: LocationViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private lateinit var position: String
    private lateinit var productData: LatestProductData

    private var latitude = "0.0"
    private var longitude = "0.0"

    private lateinit var notificationCount: String

    private lateinit var mAppUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE: Int = 1000

    private var launchActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    notificationCount = data.getStringExtra(Constants.success)!!
                    if (data.getStringExtra(Constants.success)!! != "0") {
                        contentHomeBinding.notificationActive.visibility = View.VISIBLE
                    } else {
                        contentHomeBinding.notificationActive.visibility = View.INVISIBLE
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        contentHomeBinding = binding.homeInclude
        navHeaderBinding = binding.navHeaderInclude
        noDataBinding = contentHomeBinding.includeNoData
        val view = binding.root
        setContentView(view)

        mAppUpdateManager = AppUpdateManagerFactory.create(this)

        with(contentHomeBinding) {
            ivOptionHeader.setOnClickListener {
                openDrawer()
            }

            cvSearch.setOnClickListener {
                launchActivity.launch(
                    SearchActivity.createIntent(
                        this@HomeActivity,
                        null,
                        null,
                        notificationCount,
                        latitude,
                        longitude,
                    )
                )
            }

            tvViewAll.setOnClickListener {
                launchActivity.launch(
                    AllCategoryActivity.createIntent(
                        this@HomeActivity,
                        notificationCount,
                        latitude,
                        longitude,
                    )
                )
            }

            ivNotification.setOnClickListener {
                launchActivity.launch(
                    Intent(
                        this@HomeActivity,
                        NotificationActivity::class.java
                    )
                )
            }

            llAddProduct.setOnClickListener {
                val intent = Intent(this@HomeActivity, AddProductActivity::class.java)
                launchAddProductActivity.launch(intent)
            }

            sRLHome.setColorSchemeResources(R.color.theme_color)

            sRLHome.setOnRefreshListener {
                currentPage = pageStart
                latestProductList.clear()
                categoryList.clear()
                noMoreData = false
                currentPage = pageStart
                getHomeData()
            }
        }

        setUpRecyclerView()
        setUpObserver()

        Log.d("MYT", "isGPSEnabled $isGPSEnabled")

        getHomeData()

    }


    private var launchAddProductActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: String = data!!.getStringExtra(Constants.success)!!
                if (intentData == Constants.success) {
                    currentPage = pageStart
                    latestProductList.clear()
                    categoryList.clear()
                    noMoreData = false
                    currentPage = pageStart
                    getHomeData()
                }
            }
        }

    override fun onStart() {
        super.onStart()
        LocationUtil(this).turnGPSOn(object :
            LocationUtil.OnLocationOnListener {
            override fun locationStatus(isLocationOn: Boolean) {
                this@HomeActivity.isGPSEnabled = isLocationOn
            }
        })

        if (isLocationEnabled()) {
            getLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            RC_APP_UPDATE -> if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                Log.d("MYT", "$resultCode")
                checkForUpdate()
            }
        }
    }

    private fun checkForUpdate() {
        mAppUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        this,
                        RC_APP_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("MYT", e.localizedMessage!!)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        mAppUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        this,
                        RC_APP_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("IntentSenderError", e.localizedMessage!!)
                }
            }
        }

        getUserProfileApi()
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
            val location: Location? = task.result
            if (location == null) {
                requestNewLocationData()
            } else {
                lat = location.latitude.toString()
                lng = location.longitude.toString()
                saveLocation()
            }
        }

    }

    private fun requestNewLocationData() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locationRequest = LocationRequest.create().apply {
            interval = 10000L
            fastestInterval = 0
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
            numUpdates = 1
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                lat = location.latitude.toString()
                lng = location.longitude.toString()
                saveLocation()
            }
        }
    }

    private fun setUpObserver() {

        authViewModel.getUserProfileSuccess.observe(this) {
            val data = it.data
            userData.profileImage = data.profileImage
            userData.name = data.name
            userData.phoneNumber = data.phoneNumber
            userData.lat = data.lat
            userData.lng = data.lng
            userData.fullAddress = data.fullAddress
            userData.email = data.email
            PreferencesManagement.saveUserData(this@HomeActivity, userData)
            getUserProfile()
        }

        mainViewModel.getHomeDataSuccess.observe(this) {
            val data = it.data
            val categoryData = data.category
            val latestProductData = data.products
            notificationCount = data.notificationCount.toString()

            if (notificationCount != "0") {
                contentHomeBinding.notificationActive.visibility = View.VISIBLE
            } else {
                contentHomeBinding.notificationActive.visibility = View.INVISIBLE
            }

            if (latestProductData.isNotEmpty()) {
                if (currentPage == pageStart) latestProductList.clear()
//                latestProductList.addAll(latestProductData)
                latestProductAdapter.notifyDataSetChanged()
                noDataBinding.clNoData.visibility = View.GONE

                val lastPosition = latestProductList.size - latestProductData.size

                if (latestProductList.size == latestProductData.size) {
                    contentHomeBinding.rvLatestProduct.smoothScrollToPosition(latestProductList.size)
                } else {
                    contentHomeBinding.rvLatestProduct.smoothScrollToPosition(lastPosition + 1)
                }

                currentPage += 1
            } else {
                noDataFound()
            }

            if (categoryData.isNotEmpty()) {
                categoryList.clear()
//                categoryList.addAll(categoryData)
                categoryAdapter.notifyDataSetChanged()
            }

            isLoading = false
            isLastPage = false
            contentHomeBinding.llProgress.visibility = View.GONE
            contentHomeBinding.sRLHome.isRefreshing = false
        }

        mainViewModel.isLoading.observe(this) {
            if (isLoading && isLastPage) {
                contentHomeBinding.llProgress.visibility = View.VISIBLE
            } else {
                contentHomeBinding.llProgress.visibility = View.GONE
                if (!contentHomeBinding.sRLHome.isRefreshing) {
                    loader(it)
                }
            }
        }

        mainViewModel.errorMessage.observe(this) {
            if (it.isNotBlank()) {
                showToast(it)
                contentHomeBinding.sRLHome.isRefreshing = false
            }
        }

        authViewModel.unAuthorization.observe(this) {
            if (it) {
                showToast("User is deleted by Admin.")
                backToLogIn()
            }
        }

    }

    private fun getHomeData() {
        if (isNetworkAvailable()) {

            noDataBinding.clNoData.visibility = View.GONE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.pageNumber] = "$currentPage"

            if (PreferencesManagement.getUserLocation(this@HomeActivity) != null) {
                val userLocation = PreferencesManagement.getUserLocation(this@HomeActivity)!!
                latitude = userLocation.lat
                longitude = userLocation.long
            } else {
                if ((userData.lat != null && userData.lat!!.isNotEmpty()) && (userData.lng != null && userData.lng!!.isNotEmpty())) {
                    latitude = userData.lat!!
                    longitude = userData.lng!!
                }
            }

            if (PreferencesManagement.getUserLocation(this@HomeActivity) != null) {
                val userLocation = PreferencesManagement.getUserLocation(this@HomeActivity)!!


                if (userLocation.address != null && userLocation.address!!.isNotEmpty()) {
                    binding.tvUserCurrentAddress.visibility = View.VISIBLE
                    binding.tvUserCurrentTitle.visibility = View.VISIBLE
                }
                binding.tvUserCurrentAddress.text = userLocation.address ?: ""

                binding.tvUserCurrentAddress.setTextColor(
                    ContextCompat.getColor(
                        this@HomeActivity,
                        R.color.theme_color
                    )
                )
            }

            Log.d("MYT", "latitude $latitude")
            Log.d("MYT", "longitude $longitude")

            map[RequestKeys.lat] = latitude
            map[RequestKeys.lng] = longitude

//            map[RequestKeys.lat] = "12.9715987"
//            map[RequestKeys.lng] = "77.5945627"

            mainViewModel.getHomeData(Utility.getHeaders(this@HomeActivity), map)
        } else {
            showSnackBar(
                contentHomeBinding.clHome,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
            contentHomeBinding.sRLHome.isRefreshing = false
        }
    }

    private fun getUserProfileApi() {
        if (isNetworkAvailable()) {
            val partMap = HashMap<String, String>()
            partMap[RequestKeys.userId] = userData.id.toString()
            authViewModel.getUserProfile(Utility.getHeaders(this@HomeActivity), partMap)
        }
    }

    private fun getUserProfile() {
        val userData = PreferencesManagement.getUserData(this)!!

        with(navHeaderBinding) {
            tvNavName.text = userData.name
            tvNavMobileNumber.text = userData.email

            if (userData.profileImage != null) {
                ImageUtils.setImage(
                    this@HomeActivity,
                    cvNavUserProfile,
                    userData.profileImage!!,
                    null,
                    R.drawable.default_user_profile,
                )
            }
        }

    }

    private fun noDataFound() {
        noMoreData = true
        if (!isLoading && !isLastPage) {
//            contentHomeBinding.sv.fitsSystemWindows = true
//            contentHomeBinding.sv.isFillViewport = true

            noDataBinding.clNoData.visibility = View.VISIBLE
            noDataBinding.tvNoData.text =
                applicationContext.resources.getString(R.string.latest_product_no_data_message)
            noDataBinding.ivNoData.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.home_toolbar_app_logo
                )
            )
        }
    }

    private fun setUpRecyclerView() {

        val lm = GridLayoutManager(this@HomeActivity, 2)

        binding.rvNav.layoutManager = LinearLayoutManager(this)
        binding.rvNav.adapter = navigationAdapter

        getNavItemList()

        contentHomeBinding.rvHomeCategory.apply {
            layoutManager =
                LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            recycledViewPool.setMaxRecycledViews(1, 0)
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }

        contentHomeBinding.rvLatestProduct.apply {
            layoutManager = lm
            adapter = latestProductAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }

        contentHomeBinding.rvLatestProduct.addOnScrollListener(object :
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
                            getHomeData()
                        }
                    }
                }
            }
        })

//        contentHomeBinding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
//
//            val lastChild = contentHomeBinding.sv.getChildAt(contentHomeBinding.sv.childCount - 1)
//
//            if (lastChild != null) {
//                if ((scrollY >= (lastChild.measuredHeight - contentHomeBinding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
//                    if (!noMoreData) {
//                        isLoading = true
//                        isLastPage = true
//                        getHomeData()
//                    }
//                }
//            }
//        }
    }

    private fun getNavItemList() {
        navigationAdapter.addItem(getMenu())
    }

    private fun getMenu(): ArrayList<NavItem> {
        val menuList = ArrayList<NavItem>()
        menuList.clear()
        menuList.addAll(
            arrayListOf(
                NavItem(
                    0,
                    resources.getString(R.string.my_profile),
                    1,
                ),
                NavItem(
                    1,
                    resources.getString(R.string.my_request),
                    1,
                ),
                NavItem(
                    2,
                    resources.getString(R.string.my_product),
                    1,
                ),
                NavItem(
                    4,
                    resources.getString(R.string.about_us),
                    1,
                ),
                NavItem(
                    5,
                    resources.getString(R.string.contact_us),
                    1,
                ),
                NavItem(
                    6,
                    resources.getString(R.string.privacy_policy),
                    1,
                ),
                NavItem(
                    7,
                    resources.getString(R.string.logout),
                    0,
                )
            )
        )

        return menuList
    }

    override fun itemClick(id: Int) {
        when (id) {
            0 -> startActivity(MyProfileActivity.createIntent(this@HomeActivity))
            1 -> startActivity(MyRequestActivity.createIntent(this@HomeActivity))
            2 -> startActivity(MyProductActivity.createIntent(this@HomeActivity))
            4 -> startActivity(
                ContentManagementActivity.createIntent(
                    this@HomeActivity,
                    Constants.aboutUs
                )
            )
            5 -> startActivity(
                ContentManagementActivity.createIntent(
                    this@HomeActivity,
                    Constants.contactUs
                )
            )
            6 -> startActivity(
                ContentManagementActivity.createIntent(
                    this@HomeActivity,
                    Constants.privacyPolicy
                )
            )
            7 -> logoutDialog()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    private fun logoutDialog() {
        val dialogBuilder = AlertDialog.Builder(this@HomeActivity)
        dialogBuilder
            .setMessage(R.string.logout_message)
            .setCancelable(false)

        dialogBuilder.setPositiveButton(resources.getString(R.string.yes_string), null)

        dialogBuilder.setNegativeButton(resources.getString(R.string.no_string), null)

        val alertDialog = dialogBuilder.create()

        alertDialog.setOnShowListener {

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                val map = HashMap<String, String>()
                map[RequestKeys.userId] = userData.id.toString()
                map[RequestKeys.deviceId] = getDeviceId()

                mainViewModel.logout(Utility.getHeaders(this), map)
                alertDialog.dismiss()
                backToLogIn()

            }
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    override fun onCategoryClick(data: UserCatData) {
        launchActivity.launch(
            SearchActivity.createIntent(
                this@HomeActivity,
                data.id.toString(),
                data.title,
                notificationCount,
                latitude,
                longitude
            )
        )
    }

    override fun onItemDetail(data: Data, position: Int) {
//        productData = data
        this.position = position.toString()
        val intent = Intent(this@HomeActivity, ProductDetailActivity::class.java)
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
                       /* Constants.success -> {
                            productData = data.getParcelableExtra(Constants.editProduct)!!
                            latestProductList[position.toInt()].fullAddress =
                                productData.fullAddress
                            latestProductList[position.toInt()].title = productData.title
                            latestProductList[position.toInt()].image = productData.image
                            latestProductList[position.toInt()].productId = productData.productId
                            latestProductList[position.toInt()].userId = productData.userId
                        }*/
                        Constants.failure -> {
                            latestProductList.removeAt(position.toInt())
                        }
                    }
                    latestProductAdapter.notifyDataSetChanged()
                }
            }
        }

//    override fun onStart() {
//        super.onStart()
//        startLocationUpdates()
//    }

    private fun startLocationUpdates() {
        when {
            isGPSEnabled -> observeLocationUpdates()
            isPermissionGranted() -> observeLocationUpdates()
            else -> askLocationPermission()
        }
    }



    private fun observeLocationUpdates() {
        locationViewModel.getLocationData.observe(this) {
            latitude = it.latitude.toString()
            longitude = it.longitude.toString()

            Log.d("getLocationData", "latitude $latitude")
            Log.d("getLocationData", "longitude $longitude")

            if (PreferencesManagement.getUserLocation(this@HomeActivity) != null) {
                val userLocation = PreferencesManagement.getUserLocation(this@HomeActivity)

                PreferencesManagement.saveUserLocation(
                    this@HomeActivity,
                    UserLocation(
                        latitude, longitude, userLocation?.address ?: "",
                    )
                )
            } else {
                PreferencesManagement.saveUserLocation(
                    this@HomeActivity,
                    UserLocation(
                        latitude, longitude, "",
                    )
                )

                getHomeData()
            }

        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("EXIT", true)
            return intent
        }
    }
}