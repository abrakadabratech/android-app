package com.oss.abraakadabraaapp.activities.newflow

import Data
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.google.android.datatransport.cct.internal.LogResponse.fromJson
import com.google.android.gms.wallet.IsReadyToPayRequest.fromJson
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.ActivityNewSearchBinding
import com.oss.abraakadabraaapp.datasource.APIService
import com.oss.abraakadabraaapp.datasource.MainViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModelFactory
import com.oss.abraakadabraaapp.datasource.ProductAdapter
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.reflect.Type

class NewSearchActivity : BaseActivity() ,CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface,ProductAdapter.OnProductClicked {


    private lateinit var binding: ActivityNewSearchBinding
    private val authViewModel: AuthViewModel by viewModel()

    private var categoryList: ArrayList<UserCatData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter
    private var pageStart = 1
    private var currentPage = pageStart
    private var noMoreData = false
    private var isLoading = false
    private var isLastPage = false
    private var mainListAdapter: ProductAdapter? = null


    private var latestProductList: ArrayList<Data> = ArrayList()
    private lateinit var latestProductAdapter: LatestProductAdapter

    private var from = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewSearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        postEvent(Constants.PAGE_SEARCH,null)

        val type: Type = object : TypeToken<List<UserCatData?>?>() {}.getType()

        from = intent.extras?.getString("from").toString()
        if (from == "category"){
            categoryList =  Gson().fromJson(intent.extras?.getString("CATEGORIES"), type)
            categoryAdapter = CategoryAdapter(categoryList, this, this,"search")
            binding.rvHomeCategory.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            binding.rvHomeCategory.adapter = categoryAdapter
            var csvString = ""
            for (i in categoryList){
                csvString = i.id.toString() + ","
            }
            searchCategories(csvString)
        }else{
            binding.searchEdit.requestFocus()
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                searchProducts(charSequence.toString())

            }
            override fun afterTextChanged(editable: Editable) {

//                editText2.setText(editText1.doubleValue() * 5)
            }
        })

//        setupList()

//        searchProducts()

        latestProductAdapter = LatestProductAdapter(latestProductList, this, this)

        setUpObserver()

        setUpRecyclerView()

        clickEvents()

    }

    private fun searchCategories(string:String) {
        Log.d("TAG - ", "searchCategories: $string")
        if (isNetworkAvailable()) {
            val userLocation = PreferencesManagement.getUserLocation(this)
            generateAuthToken()
//            lateinit var viewModel: MainViewModel

            val mUser = FirebaseAuth.getInstance().currentUser
            mUser!!.getIdToken(true)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val idToken = it.result.token
                        val auth = "Bearer $idToken"

                        if(PreferencesManagement.saveAuthToken(this,auth))
                        {

                            val map = HashMap<String, String>()
                            val token = PreferencesManagement.getAuthToken(this)!!
                            map["Authorization"] = token
//                            map["logging"] = "true"

//                            authViewModel.getAllCategoriesData(map)

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
                            showToast("Error generating the token!")
                        }
                    }
                }

        }
    }
    private fun setupList() {
        mainListAdapter = ProductAdapter(this)
        val lm = GridLayoutManager(this, 2)
        binding.rvLatestProduct.apply {
//            layoutManager = LinearLayoutManager(requireContext())
            layoutManager = lm
            addItemDecoration(
                MarginItemDecoration(18)
            )
            adapter = mainListAdapter
        }
    }
    fun searchProducts(query : String){
        generateAuthToken()
        val userLocation = PreferencesManagement.getUserLocation(this)
        authViewModel.searchQuery(Utility.getAuthentication(this),query,userLocation?.lat?.toDouble()!!
        , userLocation?.long?.toDouble()!!,50
        )
    }
    private fun setUpObserver() {
        authViewModel.searchSuccess.observe(this) {

            latestProductAdapter.setData(it.data)
            binding.textView75.text = "${it.data.size} Items"
            latestProductAdapter.notifyDataSetChanged()
        //            binding.sRLHome.isRefreshing = false

        }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank())showToast(it) }
        authViewModel.isLoading.observe(this) { loader(it) }

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

    override fun onItemDetail(data: Data, position: Int) {
        val intent = Intent(this,NewProductDetailActivity::class.java)
        intent.putExtra(Constants.PRODUCT, Gson().toJson(data))
        startActivity(intent)
    }

    private fun setUpRecyclerView() {

        val lm = GridLayoutManager(this, 2)
//        binding.rvHomeCategory.isNestedScrollingEnabled = false
//        binding.rvLatestProduct.isNestedScrollingEnabled = false
        binding.rvHomeCategory.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = categoryAdapter
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

    override fun onProductClicked(product: Product) {
        val intent = Intent(this,NewProductDetailActivity::class.java)
        intent.putExtra(Constants.PRODUCT,Gson().toJson(product))
        startActivity(intent)
    }
}