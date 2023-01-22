package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyListingAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyListingBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyListingActivity : BaseActivity() , MyListingAdapter.OnResponseClick {
    private lateinit var binding: ActivityMyListingBinding
    lateinit var application: BaseActivity
    private val mainViewModel: AuthViewModel by viewModel()
    private lateinit var adapterList : MyListingAdapter
    private var categoryList: ArrayList<RequestData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyListingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterList = MyListingAdapter(this,categoryList,this)

        setUpRecyclerView()
        setUpObserver()
        loaddata()

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_LISTING, null)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }


    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getProductListings(map)
    }

    private fun setUpObserver() {
        mainViewModel.getProductListingsSuccess.observe(this) {
            Log.d("TAG - Product deails", "is it rue : ${it}")

            if (it.code == 200) {
                adapterList.setData(it.data)
                adapterList.notifyDataSetChanged()
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.reportProductSuccess.observe(this) {
            if (it.code == 201) {
                showToast("Product reported")
            } else {
                showToast(it.responseMessage.toString())
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }


    private fun setUpRecyclerView() {
        lifecycleScope.launch {

            binding.rvMyListing.apply {
                layoutManager =
                    LinearLayoutManager(this@MyListingActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = adapterList
                recycledViewPool.setMaxRecycledViews(1, 0)
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
            }
        }
    }

    override fun onResponseClicked(item: RequestData) {
        val intent = Intent(this,MyListingDetialActivity::class.java)
        intent.putExtra(Constants.PRODUCT,Gson().toJson(item))
        startActivity(intent)
    }

}