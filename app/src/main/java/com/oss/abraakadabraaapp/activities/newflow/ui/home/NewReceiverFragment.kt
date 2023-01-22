package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.anilhappy.paginationsample.APIService
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.datasource.MainViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModelFactory
import com.oss.abraakadabraaapp.datasource.ProductAdapter
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.codersroute.flexiblewidgets.FlexibleSwitch.OnStatusChangedListener
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.NewSearchActivity
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewReceiverFragment : Fragment(), CategoryAdapter.CategoryAdapterInterface,
    ProductAdapter.OnProductClicked {
    lateinit var application: BaseActivity

    private var _binding: NewReceiverFlowBinding? = null
    private val binding get() = _binding!!

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter
    private var pageStart = 1
    private var currentPage = pageStart
    private var noMoreData = false
    private var isLoading = false
    private var isLastPage = false

    private var latestProductList: ArrayList<LatestProductData> = ArrayList()
    //Pagination

    //    private lateinit var latestProductAdapter: LatestProductAdapter
    lateinit var viewModel: MainViewModel
    lateinit var mainListAdapter: ProductAdapter

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

        categoryAdapter = CategoryAdapter(categoryList, requireContext(), this, "home")
//        latestProductAdapter = LatestProductAdapter(latestProductList, requireContext(), this)

        with(binding) {
//            sRLHome.setColorSchemeResources(R.color.theme_color)
            sRLHome.setOnRefreshListener {
                currentPage = pageStart
                latestProductList.clear()
                categoryList.clear()
                noMoreData = false
                currentPage = pageStart
                setupViewModel()
            }
        }
        setUpObserver()
        setupViewModel()
        setupList()
        setupView()

//        Log.d("TAG-", "setupList: ${Gson().toJson(passengersAdapter.snapshot().items)}")

        clickEvents()

//        setUpRecyclerView()

        return root
    }

    private fun setUpObserver() {
        authViewModel.getAllcategoriesSuccess.observe(requireActivity()) {
            if (it.code == 200){
                setUpCategories()
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

    private fun setupView() {
        lifecycleScope.launch {
            viewModel.listData.collect {
                mainListAdapter.submitData(it)
            }
        }
    }

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

            val map = HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(requireContext())!!
            map["Authorization"] = token
            viewModel =
                ViewModelProvider(
                    this,
                    MainViewModelFactory(
                        APIService.getApiService(),
                        map,
                        800,
                        userLocation!!.lat.toDouble(),
                        userLocation.long.toDouble()
                    )
                )[MainViewModel::class.java]

            authViewModel.getAllCategoriesData(map)

        }

    }

    private fun clickEvents() {
        binding.catFilter.setOnClickListener {
            application.postEvent(Constants.BUTTON_FILTER, null)

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
        startActivity(Intent(context, NewSearchActivity::class.java))
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
            application.postEvent(Constants.BUTTON_FILTER_APPLY, null)
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

}