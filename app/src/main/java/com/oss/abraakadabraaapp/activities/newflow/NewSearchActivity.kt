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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.postDelayed
import androidx.core.widget.doAfterTextChanged
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
import com.oss.abraakadabraaapp.activities.newflow.adapters.SearchProductAdapter
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.whileSelect
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class NewSearchActivity : BaseActivity() ,CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface,SearchProductAdapter.LatestProductAdapterInterface {


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


    private var latestProductList: ArrayList<Product> = ArrayList()
    private var searchProductList: ArrayList<Data> = ArrayList()
    private lateinit var latestProductAdapter: LatestProductAdapter
    private lateinit var searchProductAdapter: SearchProductAdapter

    private var from = ""
    private var searchQuery = ""

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
                csvString = csvString + i.id.toString() + ","
//                showToast(categoryList.size.toString())
            }
            searchQuery = csvString

//            showToast(categoryList.size.toString())
            setupList()
            searchCategories(csvString)
        }else{
            binding.searchEdit.requestFocus()
            searchProductAdapter = SearchProductAdapter(searchProductList, this, this)
            setUpRecyclerView()

        }

        binding.searchEdit.debounce(500L) { text -> searchProducts(text.toString()) }


//        setupList()

//        searchProducts()


        setUpObserver()


        clickEvents()

    }

    private fun searchCategories(string:String) {
        searchQuery = string
        Log.d("TAG - ", "searchCategories: $string")
        if (isNetworkAvailable()) {
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

                            getProductFromServer()

                        }else{
                            showToast("Error generating the token!")
                        }
                    }
                }

        }
    }
    private fun setupList() {
        latestProductAdapter = LatestProductAdapter(latestProductList, this, this)
        val lm = GridLayoutManager(this, 2)
        binding.rvLatestProduct.apply {
//            layoutManager = LinearLayoutManager(requireContext())
            layoutManager = lm
            addItemDecoration(
                MarginItemDecoration(18)
            )
            adapter = latestProductAdapter
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
                            getProductFromServer()
                        }
                    }
                }
            }
        })
    }

    private fun getProductFromServer() {
        if (from == "category"){
            val userLocation = PreferencesManagement.getUserLocation(this)

            val map = HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map["Authorization"] = token
//                            map["logging"] = "true"

            authViewModel.getProductsData(map,currentPage,
                50,
                userLocation!!.lat.toDouble(),
                userLocation.long.toDouble(),searchQuery)
        }else{
            searchProducts(query = searchQuery)
        }
    }

    fun searchProducts(query : String){
        searchProductAdapter = SearchProductAdapter(searchProductList, this, this)
        setUpRecyclerView()
        searchQuery = query
        currentPage = 1
        generateAuthToken()
        val userLocation = PreferencesManagement.getUserLocation(this)
        authViewModel.searchQuery(Utility.getAuthentication(this),query,userLocation?.lat?.toDouble()!!
        , userLocation?.long?.toDouble()!!,50,currentPage
        )
    }
    private fun setUpObserver() {
        authViewModel.searchSuccess.observe(this) {

            if (currentPage == pageStart) searchProductList.clear()
            searchProductList.clear()
            latestProductList.clear()
            searchProductList.addAll(it.data)
            searchProductAdapter.notifyDataSetChanged()
//            binding.nodata2.visibility = View.GONE

            val lastPosition = searchProductList.size - it.data.size

            if (latestProductList.size == it.data.size) {
                binding.rvLatestProduct.smoothScrollToPosition(latestProductList.size)
            } else {
                binding.rvLatestProduct.smoothScrollToPosition(lastPosition + 1)
            }
            binding.textView75.text = "${it.data.size} Items"

//            currentPage += 1


            if (it.data.size == 0) {
//                binding.nodata2.visibility = View.VISIBLE
                showToast("No Data Found")
            }
//
//            searchProductAdapter.setData(it.data)
//            searchProductAdapter.notifyDataSetChanged()
//        //            binding.sRLHome.isRefreshing = false

        }
        authViewModel.allproductsSuccess.observe(this) {
            if (it.data.products.isNotEmpty()) {
                if (currentPage == pageStart) latestProductList.clear()
                latestProductList.addAll(it.data.products)
                latestProductAdapter.notifyDataSetChanged()
                binding.nodata2.visibility = View.GONE

                val lastPosition = latestProductList.size - it.data.products.size

                if (latestProductList.size == it.data.products.size) {
                    binding.rvLatestProduct.smoothScrollToPosition(latestProductList.size)
                } else {
                    binding.rvLatestProduct.smoothScrollToPosition(lastPosition + 1)
                }
                binding.textView75.text = "${it.data.products.size} Items"

                currentPage += 1
            } else {
                //noDataFound()
                if (it.data.products.size == 0 && currentPage == 1) {
                    binding.nodata2.visibility = View.VISIBLE
                }
            }
//            latestProductAdapter.setData(it.data.products)
//            latestProductAdapter.notifyDataSetChanged()
//        //            binding.sRLHome.isRefreshing = false

        }

        authViewModel.errorMessage.observe(this) {
//            if (it.isNotBlank())//showToast(it)
            }
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


    override fun onCategoryClick(data: UserCatData,position: Int) {

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

        //when search the products
        binding.rvLatestProduct.apply {
            layoutManager = lm
            addItemDecoration(
                MarginItemDecoration(18)
            )
            adapter = searchProductAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }

        binding.rvLatestProduct.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, oldScrollY: Int) {
                super.onScrolled(recyclerView, dx, oldScrollY)
                if (from == "category"){
                    Log.d("scroll", "scrolling")
                    val total: Int = lm.itemCount
                    val lastVisibleItemCount: Int = lm.findLastVisibleItemPosition()
                    if (!isLoading) {
                        if (total > 0) if (total - 1 == lastVisibleItemCount) {
                            if (!noMoreData) {
                                isLoading = true
                                isLastPage = true
                                getProductFromServer()
                            }
                        }
                    }
                }else{

                }

            }
        })
    }
    //Alert Dialog for show nearby filter
    private fun showNearByFilterDialog() {

        var filters = PreferencesManagement.getFilters(this)
        Log.d("TAG - ", "showNearByFilterDialog: ${Gson().toJson(filters)}")
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_nearby_filter, null)
        dialogBuilder.setView(dialogView)

        val nearToMeTxt = dialogView.findViewById<TextView>(R.id.nearToMeTxt)
        val nearToMeSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.nearToMeSwitch)

        val newestFirstTxt = dialogView.findViewById<TextView>(R.id.newrstFirstTxt)
        val newestFirstSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.newestFirstSwitch)

        val applyBtn = dialogView.findViewById<TextView>(R.id.applyBtn)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        if (filters?.nearest!!){
            nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            newestFirstSwitch.isChecked = false
            nearToMeSwitch.isChecked = true
        }else{
            nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            newestFirstSwitch.isChecked = true
            nearToMeSwitch.isChecked = false
        }

        nearToMeSwitch.addOnStatusChangedListener(FlexibleSwitch.OnStatusChangedListener {
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

        newestFirstSwitch.addOnStatusChangedListener(FlexibleSwitch.OnStatusChangedListener {
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
            postClick(Constants.BUTTON_FILTER_APPLY)
//            Toast.makeText(this, "Under Development ${newestFirstSwitch.isChecked}", Toast.LENGTH_SHORT).show()
            if (filters.newest){
                sort()
            }else{
                getProductFromServer()
            }
            PreferencesManagement.setFilters(this,filters)
            alertDialog.dismiss()
        }
        alertDialog.window?.setLayout(800, 700)

    }
    fun sort(){
        latestProductList.sortByDescending { list -> list.timestamp }
        latestProductAdapter.notifyDataSetChanged()
    }

    override fun onSearchItemDetail(data: Data, position: Int) {
        val intent = Intent(this,NewProductDetailActivity::class.java)
        //need some parsing the json data
//        val product = Product(data.condition,data.tim)
        intent.putExtra(Constants.PRODUCT,Gson().toJson(data))
        startActivity(intent)
    }

    override fun onItemDetail(data: Product, position: Int) {
        val intent = Intent(this,NewProductDetailActivity::class.java)
        intent.putExtra(Constants.PRODUCT, Gson().toJson(data))
        startActivity(intent)
    }

    fun EditText.debounce(delay: Long, action: (Editable?) -> Unit) {
        doAfterTextChanged { text ->
            var counter = getTag(id) as? Int ?: 0
            handler.removeCallbacksAndMessages(counter)
            handler.postDelayed(delay, ++counter) { action(text) }
            setTag(id, counter)
        }
    }
}