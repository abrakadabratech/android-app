package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.ExpandableAdapter
import com.oss.abraakadabraaapp.adapter.NotificationAdapter
import com.oss.abraakadabraaapp.databinding.ChatRowBinding
import com.oss.abraakadabraaapp.databinding.FragmentGivingChatsBinding
import com.oss.abraakadabraaapp.datasource.NotificationViewModel
import com.oss.abraakadabraaapp.datasource.NotificationViewModelFactory
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.viewModel.SellerProductListViewModel
import com.oss.abraakadabraaapp.viewmodelfactory.SellerProductViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GivingChatsFragment : Fragment() {
    lateinit var application: BaseActivity
    private val TAG = "GivingChatsFragment"

    private lateinit var binding : FragmentGivingChatsBinding

    private lateinit var viewModel: SellerProductListViewModel
    private lateinit var sellerAdapter: ExpandableAdapter

    override fun onResume() {
        super.onResume()
        loadData()
    }
    fun loadData(){
        sellerAdapter.refresh()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGivingChatsBinding.inflate(layoutInflater)
        val view = binding.root


        application = (activity as BaseActivity)

        sellerAdapter = ExpandableAdapter(requireContext())

        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL))
            adapter = sellerAdapter
        }

        viewModel = ViewModelProvider(
            this,
            SellerProductViewModelFactory(APIService.getApiService())
        )[SellerProductListViewModel::class.java]

        application.postEvent(Constants.PAGE_GIVER_CHAT, null)


        lifecycleScope.launch {
            viewModel.notificationList.collectLatest { paginatedData ->

                launch(Dispatchers.Main) {
                    sellerAdapter.loadStateFlow.collectLatest { loadStates ->
                        if (loadStates.refresh is LoadState.Loading) {
                            binding.shimmer.visibility = View.VISIBLE
                            binding.shimmer.stopShimmer()
                        } else {
                            //shimmer OFF
                            binding.shimmer.visibility = View.GONE
                            binding.shimmer.stopShimmer()
                            if (sellerAdapter.itemCount < 1) {
                                binding.nodata3.visibility = View.VISIBLE
                            } else {
                                binding.nodata3.visibility = View.GONE
                            }
                        }
                    }
                }
                sellerAdapter.submitData(lifecycle, PagingData.empty())
                sellerAdapter.submitData(paginatedData)

            }

        }
        return view
    }


}