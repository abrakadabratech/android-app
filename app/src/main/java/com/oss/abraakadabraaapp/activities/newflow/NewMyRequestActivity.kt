package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyListingAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestAdapter
import com.oss.abraakadabraaapp.activities.newflow.ui.MyRequestDetailsActivity
import com.oss.abraakadabraaapp.databinding.ActivityNewMyRequestBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.Data
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_MY_REQUEST
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewMyRequestActivity : BaseActivity() , MyRequestAdapter.OnResponseClick{
    private lateinit var binding: ActivityNewMyRequestBinding
    private val mainViewModel: AuthViewModel by viewModel()
    private lateinit var adapterList : MyRequestAdapter
    private var categoryList: ArrayList<Data> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postEvent(Constants.PAGE_MY_REQUEST,null)

        binding = ActivityNewMyRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapterList = MyRequestAdapter(this,categoryList,this)

        setUpRecyclerView()
        setUpObserver()

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_MY_REQUEST)
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        loaddata()
    }
    private fun setUpObserver() {
        mainViewModel.getMyRequestsSuccess.observe(this) {
            Log.d("TAG - Product deails", "is it rue : ${it}")

            if (it.code == 200) {
                adapterList.setData(it.data)
                adapterList.notifyDataSetChanged()
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }
    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getMyRequests(map)
    }
    private fun setUpRecyclerView() {
        lifecycleScope.launch {

            binding.rvMyrequest.apply {
                layoutManager =
                    LinearLayoutManager(this@NewMyRequestActivity)
                adapter = adapterList
                recycledViewPool.setMaxRecycledViews(1, 0)
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
            }
        }
    }

    override fun onResponseClicked(item: Data) {
        postClick(Constants.BUTTON_MY_REQUEST_CARD)
        val intent = Intent(this,MyRequestDetailsActivity::class.java)
        intent.putExtra(Constants.PRODUCT, Gson().toJson(item))
        Log.d("ok", "onCreate in linsting activity: $${Gson().toJson(item)}")
        startActivity(intent)
    }
}