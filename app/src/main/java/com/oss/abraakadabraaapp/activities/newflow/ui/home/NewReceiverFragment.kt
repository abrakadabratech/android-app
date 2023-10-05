package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.codersroute.flexiblewidgets.FlexibleSwitch.OnStatusChangedListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.CategorySelectActivity
import com.oss.abraakadabraaapp.activities.newflow.MyListingDetialActivity
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.NewSearchActivity
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.datasource.APIService
import com.oss.abraakadabraaapp.datasource.MainFilterViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModelFactory
import com.oss.abraakadabraaapp.datasource.ProductAdapter
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SWIPE_REFRESH
import com.oss.abraakadabraaapp.utils.Constants.NOTIFICATION_REFRESH_EVENT
import com.oss.abraakadabraaapp.utils.Constants.productId
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewReceiverFragment : Fragment(), CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface, ProductAdapter.OnProductClicked {
    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION: Int = 1001
    lateinit var application: BaseActivity

    private lateinit var binding: NewReceiverFlowBinding
    private var KEY_RECYCLER_STATE = "recycler_state"
    private var mBundleRecyclerViewState: Bundle? = null

    private val locationViewModel: LocationViewModel by viewModel()

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter
    private var pageStart = 1
    private var currentPage = pageStart
    private var noMoreData = false
    private var isLoading = false
    private var isLastPage = false
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var latestProductAdapter: LatestProductAdapter

    private var latestProductList: ArrayList<Product> = ArrayList()
    //Pagination

    //    private lateinit var latestProductAdapter: LatestProductAdapter
    private var mainListAdapter: ProductAdapter? = null
    private val TAG = "NewReceiverFragment"
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = NewReceiverFlowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_RECEIVER, null)
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        categoryAdapter = CategoryAdapter(categoryList, requireContext(), this, "home")

        with(binding) {

            sRLHome.setOnRefreshListener {
                application.postClick(BUTTON_SWIPE_REFRESH)
                currentPage = pageStart
                latestProductList.clear()
                categoryList.clear()
                noMoreData = false
                currentPage = pageStart
                checkPermissions()
            }
        }

        binding.searchEdit.setOnClickListener {
            startActivity(Intent(requireContext(), NewSearchActivity::class.java))
        }

        binding.cardView4.setOnClickListener {
            startActivity(Intent(requireContext(), NewSearchActivity::class.java))
        }
        setupList()

        setUpObserver()

        clickEvents()

        EventBus.getDefault().post(NOTIFICATION_REFRESH_EVENT)

        return root
    }

    override fun onPause() {
        super.onPause()
        mBundleRecyclerViewState = Bundle()
        val listState: Parcelable = binding.rvLatestProduct.layoutManager?.onSaveInstanceState()!!
        mBundleRecyclerViewState!!.putParcelable(KEY_RECYCLER_STATE, listState)
    }
    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    private fun checkPermissions() {
        val listener = object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                startApp()
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }

        val permissions =
            if (Build.VERSION.SDK_INT <= 29) {
                arrayListOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                arrayListOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }

        Dexter.withContext(requireContext())
            .withPermissions(permissions)
            .withListener(listener)
            .check()
    }

    fun startApp() {
        if (application.checkPermission()) {
            if (application.isLocationEnabled()) {
                if (PreferencesManagement.getUserLocation(requireContext()) != null) {
                    setupViewModel()
                } else {
                    application.showToast("Please restart your app to get products")
                    application.getLastLocation()
                }
            } else {
                //show the dialog to enable the location permission
                var alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Please turn on your location to view the products nearer to you.")

                alertDialog.setPositiveButton(
                    "Enable",
                    DialogInterface.OnClickListener { dialog, id ->

//                    checkPermissions()
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(intent)
                        dialog.dismiss()
                    })
                alertDialog.setCancelable(true)
                alertDialog.show()
//                startApp()
            }
        } else {
//            show the dialog to enable the location permission
            var alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Alert")
            alertDialog.setMessage("Please enable location permission to view the products nearer to you.")

            alertDialog.setPositiveButton("Enable", DialogInterface.OnClickListener { dialog, id ->

//                checkPermissions()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
            })
            alertDialog.setCancelable(true)
            alertDialog.show()
        }
    }

    private fun setUpObserver() {
        authViewModel.getAllcategoriesSuccess.observe(requireActivity()) {
            if (it.code == 200) {
                setUpCategories()

                PreferencesManagement.saveCategories(requireActivity(), it)
                categoryList = it.data
                categoryList.add(UserCatData("", "More", ""))
                categoryAdapter.setData(categoryList)
                categoryAdapter.notifyDataSetChanged()
                isLoading = false
            }
            binding.sRLHome.isRefreshing = false

        }
        authViewModel.allproductsSuccess.observe(requireActivity()) {

//            latestProductAdapter.setData(it.data.products)
//            binding.textView75.text = "${it.data.products.size} Items"
//            latestProductAdapter.notifyDataSetChanged()
            if (currentPage == pageStart) latestProductList.clear()
            latestProductList.addAll(it.data.products)
            latestProductAdapter.notifyDataSetChanged()
//                noDataBinding.clNoData.visibility = View.GONE

            val lastPosition = latestProductList.size - it.data.products.size

            if (latestProductList.size == it.data.products.size) {
                binding.rvLatestProduct.smoothScrollToPosition(latestProductList.size)
            } else {
                binding.rvLatestProduct.smoothScrollToPosition(lastPosition + 1)
            }

            currentPage += 1

            if (latestProductList.size == 0) {
                binding.nodata.visibility = View.VISIBLE
            }

        }

        authViewModel.errorMessage.observe(requireActivity()) {
            if (it.isNotBlank()) application.showToast(
                it
            )
        }
        authViewModel.isLoading.observe(requireActivity()) { application.loader(it) }

    }

    private fun setUpCategories() {
        lifecycleScope.launch {

            binding.rvHomeCategory.apply {
                layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = categoryAdapter
                recycledViewPool.setMaxRecycledViews(1, 0)
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
            }
        }
    }

    /* private fun setupView() {
         lifecycleScope.launch {
             viewModel.listData.collect {
                 mainListAdapter.submitData(it)
             }
         }
     }
 */


    private fun setupList() {
        mainListAdapter = ProductAdapter(this)
        val lm = GridLayoutManager(requireContext(), 2)
        binding.rvLatestProduct.apply {
            //            layoutManager = LinearLayoutManager(requireContext())
            layoutManager = lm
            addItemDecoration(
                MarginItemDecoration(18)
            )
            adapter = mainListAdapter
        }
    }

    private fun setupViewModel() {
        if (application.isNetworkAvailable()) {
            application.generateAuthToken()

//            lateinit var viewModel: MainViewModel

            val mUser = FirebaseAuth.getInstance().currentUser

            mUser!!.getIdToken(true)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val idToken = it.result.token
                        val auth = "Bearer $idToken"

                        val activity: Activity? = activity
                        if (activity != null) {
                            if (PreferencesManagement.saveAuthToken(requireActivity(), auth)) {
                                val userLocation =
                                    PreferencesManagement.getUserLocation(requireContext())

                                val map = HashMap<String, String>()
                                val token = PreferencesManagement.getAuthToken(requireContext())!!
                                map["Authorization"] = token
                                if (application.isNetworkAvailable()){
                                    authViewModel.getAllCategoriesData(map)
                                }else{
                                    application.showToast(getString(R.string.no_internet_connection_found))
                                }
                                if (PreferencesManagement.getFilters(requireContext())!!.nearest)
                                    getProductFromServer("nearest")
                                else
                                    getProductFromServer("latest")

                            } else {
                                application.showToast("Error generating the token!")
                            }
                        }

                    }
                }
        }else{
            application.showToast(getString(R.string.no_internet_connection_found))
            binding.sRLHome.isRefreshing = false
        }
    }

    private fun getProductFromServer(sortBy: String) {

        if (application.isNetworkAvailable()){
            val userLocation = PreferencesManagement.getUserLocation(requireContext())

            val map = HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(requireContext())!!
            map["Authorization"] = token

            // Pagination Library
            if (sortBy == "latest") {
                loadLatest()
            } else {

                val viewModel =
                    ViewModelProvider(
                        this,
                        MainViewModelFactory(
                            APIService.getApiService(),
                            map,
                            50,
                            userLocation!!.lat.toDouble(),
                            userLocation.long.toDouble(), "", sortBy
                        )
                    )[MainFilterViewModel::class.java]

                mainListAdapter!!.submitData(lifecycle,PagingData.empty())
                lifecycleScope.launchWhenCreated {

                    viewModel.listData2.collectLatest {
                        launch(Dispatchers.Main) {
                            mainListAdapter!!.loadStateFlow.collectLatest { loadStates ->
                                if (loadStates.refresh is LoadState.Loading) {
//                                    application.loader(true)
                                } else {
                                    if (mainListAdapter!!.itemCount < 1) {
                                        binding.nodata.visibility = View.VISIBLE
                                    } else {
                                        binding.nodata.visibility = View.GONE
                                    }
//                                    application.loader(false)
                                }
                            }
                        }
                        mainListAdapter!!.submitData(lifecycle,PagingData.empty())
                        mainListAdapter!!.submitData(it)
                    }

                }
            }
        }else{
            application.showToast(getString(R.string.no_internet_connection_found))
        }


    }

    private fun loadLatest() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())

        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(requireContext())!!
        map["Authorization"] = token

        val viewModel =
            ViewModelProvider(
                this,
                MainViewModelFactory(
                    APIService.getApiService(),
                    map,
                    50,
                    userLocation!!.lat.toDouble(),
                    userLocation.long.toDouble(), "", "latest"
                )
            )[MainViewModel::class.java]
        mainListAdapter!!.submitData(lifecycle,PagingData.empty())
        lifecycleScope.launchWhenCreated {

            viewModel.listData.collectLatest {
                launch(Dispatchers.Main) {
                    mainListAdapter!!.loadStateFlow.collectLatest { loadStates ->
                        if (loadStates.refresh is LoadState.Loading) {
//                                    application.loader(true)
                            //shimmer ON
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.shimmerLayout.startShimmer()
                        } else {
                            //shimmer OFF
                            binding.shimmerLayout.visibility = View.GONE
                            binding.shimmerLayout.stopShimmer()
                            if (mainListAdapter!!.itemCount < 1) {
                                binding.nodata.visibility = View.VISIBLE
                            } else {
                                binding.nodata.visibility = View.GONE
                            }
//                                    application.loader(false)
                        }
                    }
                }
                mainListAdapter!!.submitData(lifecycle,PagingData.empty())
                mainListAdapter!!.submitData(it)
            }

        }
    }


    private fun clickEvents() {
        binding.catFilter.setOnClickListener {
            application.postClick(Constants.BUTTON_FILTER)

            showNearByFilterDialog()

//            startActivity(Intent(requireContext(),CategorySelectActivity::class.java))
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        setUpRecyclerView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
//        binding = null
    }

    override fun onCategoryClick(data: UserCatData, position: Int) {
        if (position == 0) {

            val intent = Intent(context, CategorySelectActivity::class.java)
//            intent.putExtra("CATEGORIES",PreferencesManagement.getCategories())
            startActivity(intent)
        } else {
            var cats = ArrayList<UserCatData>()
            cats.add(data)
            val intent = Intent(context, NewSearchActivity::class.java)
            intent.putExtra("CATEGORIES", Gson().toJson(cats))
            intent.putExtra("from", "category")
            startActivity(intent)
        }

    }

//    override fun onItemDetail(data: LatestProductData, position: Int) {
////        startActivity(Intent(context, NewProductDetailActivity::class.java))
//    }

    //Alert Dialog for show nearby filter
    private fun showNearByFilterDialog() {
        var filters = PreferencesManagement.getFilters(requireContext())
        Log.d("TAG - ", "showNearByFilterDialog: ${Gson().toJson(filters)}")
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_nearby_filter, null)
        dialogBuilder.setView(dialogView)

        val nearToMeTxt = dialogView.findViewById<TextView>(R.id.nearToMeTxt)
        val nearToMeSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.nearToMeSwitch)

        val newestFirstTxt = dialogView.findViewById<TextView>(R.id.newrstFirstTxt)
        val newestFirstSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.newestFirstSwitch)

        val applyBtn = dialogView.findViewById<TextView>(R.id.applyBtn)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        if (filters?.nearest!!) {
            nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            newestFirstSwitch.isChecked = false
            nearToMeSwitch.isChecked = true
        } else {
            nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            newestFirstSwitch.isChecked = true
            nearToMeSwitch.isChecked = false
        }

        nearToMeSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) {
                filters?.newest = false
                filters?.nearest = true


                nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
//                nearToMeSwitch.isChecked = true
                newestFirstSwitch.isChecked = false
                newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))

            } else {
                filters?.newest = true
                filters?.nearest = false

                nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                newestFirstSwitch.isChecked = true
            }
        })

        newestFirstSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) {
                filters?.newest = true
                filters?.nearest = false

                newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                nearToMeSwitch.isChecked = false
            } else {
                filters?.newest = false
                filters?.nearest = true

                newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                nearToMeSwitch.isChecked = true
            }
        })


        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        closeBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        applyBtn.setOnClickListener {
            application.postClick(Constants.BUTTON_FILTER_APPLY)
//            Toast.makeText(context, "Under Development ${newestFirstSwitch.isChecked}", Toast.LENGTH_SHORT).show()
            if (filters.newest) {
//                sort()
                getProductFromServer("latest")
            } else {
                getProductFromServer("nearest")
            }
            PreferencesManagement.setFilters(requireContext(), filters)
            alertDialog.dismiss()
        }
        alertDialog.window?.setLayout(800, 700)

    }


    private fun sort() {
        latestProductList.sortByDescending { list -> list.timestamp }
        latestProductAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onItemDetail(data: Product, position: Int) {
        val intent = Intent(requireContext(), NewProductDetailActivity::class.java)
        intent.putExtra(productId, data.id)
        startActivity(intent)
    }

    override fun onProductClicked(product: Product?, position: Int) {
        Log.d(TAG, "onProductClicked: ${product?.name}")
        if (product?.isSelfProduct!!){
            val intent = Intent(requireContext(), MyListingDetialActivity::class.java)
            intent.putExtra(productId, product.id)
            startActivity(intent)
        }else{
            val intent = Intent(requireContext(), NewProductDetailActivity::class.java)
            intent.putExtra(productId, product.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("Cycle-TAG", "onResume: RECEIVERFRAGMENT")
        if (mBundleRecyclerViewState != null) {
            val listState = mBundleRecyclerViewState!!.getParcelable<Parcelable>(KEY_RECYCLER_STATE)
            binding.rvLatestProduct.layoutManager?.onRestoreInstanceState(listState)
        }
    }
}