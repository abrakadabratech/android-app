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
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.NewSearchActivity
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.datasource.APIService
import com.oss.abraakadabraaapp.datasource.MainViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModelFactory
import com.oss.abraakadabraaapp.datasource.ProductAdapter
import com.oss.abraakadabraaapp.datasource.products.Data
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SWIPE_REFRESH
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewReceiverFragment : Fragment(), CategoryAdapter.CategoryAdapterInterface,
    ProductAdapter.OnProductClicked {
    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION: Int = 1001
    lateinit var application: BaseActivity

    private var _binding: NewReceiverFlowBinding? = null
    private val binding get() = _binding!!
    private val locationViewModel: LocationViewModel by viewModel()

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter
    private var pageStart = 1
    private var currentPage = pageStart
    private var noMoreData = false
    private var isLoading = false
    private var isLastPage = false
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var latestProductList: ArrayList<LatestProductData> = ArrayList()
    //Pagination

    //    private lateinit var latestProductAdapter: LatestProductAdapter
    private var mainListAdapter: ProductAdapter? = null

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewReceiverFlowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_RECEIVER, null)
        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        categoryAdapter = CategoryAdapter(categoryList, requireContext(), this, "home")
//        latestProductAdapter = LatestProductAdapter(latestProductList, requireContext(), this)

        with(binding) {
//            sRLHome.setColorSchemeResources(R.color.theme_color)

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


        binding.searchEdit.setOnClickListener{
            startActivity(Intent(requireContext(), NewSearchActivity::class.java))
        }

        binding.cardView4.setOnClickListener{
            startActivity(Intent(requireContext(), NewSearchActivity::class.java))
        }
        setUpObserver()
//        setupViewModel()
        setupList()
//        setupView()

//        Log.d("TAG-", "setupList: ${Gson().toJson(passengersAdapter.snapshot().items)}")

        clickEvents()

//        setUpRecyclerView()

        return root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)

//        startApp()
        /*if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(requireActivity())
                .setTitle("Permission Needed")
                .setMessage("Permission is needed to access files from your device...")
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
                    checkPermissions()
                }
                .setNegativeButton(
                    "Cancel"
                ) { dialog, which -> dialog.dismiss() }.create().show()
        }*/
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

    fun startApp(){
        if (application.checkPermission()) {
            if (application.isLocationEnabled()) {
                if (PreferencesManagement.getUserLocation(requireContext()) != null){
                    setupViewModel()
                }else{
                    application.showToast("Please restart your app to get products")
                    application.getLastLocation()
                }
            } else {
                //show the dialog to enable the location permission
                var alertDialog = AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Please enable location permission to view the products nearer to you.")

                alertDialog.setPositiveButton("Enable", DialogInterface.OnClickListener { dialog, id ->

//                    checkPermissions()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri: Uri = Uri.fromParts("package", requireActivity().getPackageName(), null)
                    intent.data = uri
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
                val uri: Uri = Uri.fromParts("package", requireActivity().getPackageName(), null)
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
            if (it.code == 200){
                setUpCategories()

                PreferencesManagement.saveCategories(requireActivity(),it)
                categoryAdapter.setData(it.data)
                categoryAdapter.notifyDataSetChanged()
                isLoading = false
            }
            binding.sRLHome.isRefreshing = false

        }

        authViewModel.errorMessage.observe(requireActivity()) { if (it.isNotBlank()) application.showToast(it) }
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
            val userLocation = PreferencesManagement.getUserLocation(requireContext())
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
                            if(PreferencesManagement.saveAuthToken(requireActivity(),auth))
                            {

                                val map = HashMap<String, String>()
                                val token = PreferencesManagement.getAuthToken(requireContext())!!
                                map["Authorization"] = token
                                map["logging"] = "true"

                                authViewModel.getAllCategoriesData(map)

                                val viewModel =
                                    ViewModelProvider(
                                        this,
                                        MainViewModelFactory(
                                            APIService.getApiService(),
                                            map,
                                            50,
                                            userLocation!!.lat.toDouble(),
                                            userLocation.long.toDouble(),""
                                        )
                                    )[MainViewModel::class.java]

                                lifecycleScope.launch {
                                    viewModel.listData.collect {
                                        mainListAdapter?.submitData(it)
//                                    categoryAdapter.setData()
                                    }
                                }

                            }else{
                                application.showToast("Error generating the token!")
                            }
                        }

                    }
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
        _binding = null
    }

    override fun onCategoryClick(data: UserCatData) {
        var cats = ArrayList<UserCatData>()
        cats.add(data)
        val intent = Intent(context, NewSearchActivity::class.java)
        intent.putExtra("CATEGORIES",Gson().toJson(cats))
        intent.putExtra("from","category")

        startActivity(intent)
    }

//    override fun onItemDetail(data: LatestProductData, position: Int) {
////        startActivity(Intent(context, NewProductDetailActivity::class.java))
//    }

    //Alert Dialog for show nearby filter
    private fun showNearByFilterDialog() {

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

        nearToMeSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        })

        newestFirstSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        })

        applyBtn.setOnClickListener {
            application.postClick(Constants.BUTTON_FILTER_APPLY)
            Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show()
        }

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        closeBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setLayout(800, 700)

    }

    override fun onProductClicked(product: Product) {
        val intent = Intent(requireContext(),NewProductDetailActivity::class.java)
        intent.putExtra(Constants.PRODUCT,Gson().toJson(product))
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(data: Data?) {
        Log.d("TAG - ", "onMessageEvent: ${Gson().toJson(data)}")
        if (data?.products?.size == 0){
            binding.nodata.visibility = View.VISIBLE
        }else binding.nodata.visibility = View.GONE

//        application.showToast(data?.data?.products?.size.toString())
    }


    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }
}