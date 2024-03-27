package com.oss.abraakadabraaapp.activities.newflow.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.adapters.SellerChatListAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.SubAdapter
import com.oss.abraakadabraaapp.databinding.ActivitySellerChatsBinding
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.viewModel.BuyerProductViewModel
import com.oss.abraakadabraaapp.viewModel.SellerChatListViewModel
import com.oss.abraakadabraaapp.viewmodelfactory.BuyerChatListViewModelFactory
import com.oss.abraakadabraaapp.viewmodelfactory.SellerChatViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SellerChatsActivity : AppCompatActivity() {

    lateinit var binding : ActivitySellerChatsBinding
    var productId = ""

    private lateinit var viewModel: SellerChatListViewModel
    private lateinit var adapter: SellerChatListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        actionBar?.hide()

        window.statusBarColor =
            ContextCompat.getColor(
                this,
                R.color.blue_status_bar_color
            )


        adapter = SellerChatListAdapter(this)

        binding.rvSeller.apply {
            layoutManager = LinearLayoutManager(this@SellerChatsActivity)
            addItemDecoration(DividerItemDecoration(this@SellerChatsActivity, LinearLayoutManager.VERTICAL))
            adapter = this@SellerChatsActivity.adapter
        }

        productId = intent.extras?.getString(Constants.productId,"")!!

        viewModel = ViewModelProvider(
            this,
            SellerChatViewModelFactory(APIService.getApiService(),productId)
        )[SellerChatListViewModel::class.java]

        lifecycleScope.launch {
            viewModel.notificationList.collectLatest { paginatedData ->

                launch(Dispatchers.Main) {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        if (loadStates.refresh is LoadState.Loading) {
                            binding.shimmer.visibility = View.VISIBLE
                            binding.shimmer.stopShimmer()
                        } else {
                            //shimmer OFF
                            binding.shimmer.visibility = View.GONE
                            binding.shimmer.stopShimmer()
                            if (adapter.itemCount < 1) {
                                binding.nodata6.visibility = View.VISIBLE
                            } else {
                                binding.nodata6.visibility = View.GONE
                            }
                        }
                    }
                }
                adapter.submitData(lifecycle, PagingData.empty())
                adapter.submitData(paginatedData)

            }

        }
    }
}