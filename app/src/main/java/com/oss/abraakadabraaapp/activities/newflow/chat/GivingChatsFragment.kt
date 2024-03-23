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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var nodata: TextView
    private lateinit var rvChats: RecyclerView
    private lateinit var oldChatText: TextView
    private lateinit var oldChats: RecyclerView
    private val TAG = "GivingChatsFragment"

    private lateinit var viewModel: SellerProductListViewModel
    private lateinit var adapter: ExpandableAdapter


    override fun onResume() {
        super.onResume()
//        loadGroupedChats()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_giving_chats, container, false)

        rvChats = view.findViewById(R.id.rvChats)
        nodata = view.findViewById(R.id.nodata3)
        oldChats = view.findViewById(R.id.oldChats)
        oldChatText = view.findViewById(R.id.oldChatsTxt)
        application = (activity as BaseActivity)

        adapter = ExpandableAdapter(requireContext())

        rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this.adapter
        }

        viewModel = ViewModelProvider(
            this,
            SellerProductViewModelFactory(APIService.getApiService())
        )[SellerProductListViewModel::class.java]

        application.postEvent(Constants.PAGE_GIVER_CHAT, null)


        lifecycleScope.launch {
            viewModel.notificationList.collectLatest { paginatedData ->

                launch(Dispatchers.Main) {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        if (loadStates.refresh is LoadState.Loading) {
//                                    application.loader(true)
                            //shimmer ON
//                            binding.shimmerLayout.visibility = View.VISIBLE
//                            binding.shimmerLayout.startShimmer()
                        } else {
                            //shimmer OFF
                            if (adapter.itemCount < 1) {
                                nodata.visibility = View.VISIBLE
                            } else {
                                nodata.visibility = View.GONE
                            }
                        }
                    }
                }
                adapter.submitData(lifecycle, PagingData.empty())
                adapter.submitData(paginatedData)

            }

        }
        return view
    }


}