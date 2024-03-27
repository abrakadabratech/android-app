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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.ExpandableAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.SubAdapter
import com.oss.abraakadabraaapp.databinding.ChatRowBinding
import com.oss.abraakadabraaapp.databinding.FragmentReceivingChatsBinding
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.viewModel.BuyerProductViewModel
import com.oss.abraakadabraaapp.viewModel.SellerProductListViewModel
import com.oss.abraakadabraaapp.viewmodelfactory.BuyerChatListViewModelFactory
import com.oss.abraakadabraaapp.viewmodelfactory.SellerProductViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class ReceivingChatsFragment : Fragment(),ChatAdapter.onChatClicked {
    lateinit var application: BaseActivity
    private lateinit var binding:FragmentReceivingChatsBinding

    private lateinit var viewModel: BuyerProductViewModel
    private lateinit var adapter: SubAdapter

    private val TAG = "ReceivingChatsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceivingChatsBinding.inflate(layoutInflater)

        val view =  binding.root
        application = (activity as BaseActivity)

        application.postEvent(Constants.PAGE_RECEIVER_CHAT,null)

        adapter = SubAdapter(requireContext())

        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(),LinearLayoutManager.VERTICAL))
            adapter = this@ReceivingChatsFragment.adapter
        }

        viewModel = ViewModelProvider(
            this,
            BuyerChatListViewModelFactory(APIService.getApiService())
        )[BuyerProductViewModel::class.java]

        lifecycleScope.launch {
            viewModel.notificationList.collectLatest { paginatedData ->

                launch(Dispatchers.Main) {
                    adapter.loadStateFlow.collectLatest { loadStates ->
                        if (loadStates.refresh is LoadState.Loading) {
                            binding.shimmer.visibility = View.VISIBLE
                            binding.shimmer.startShimmer()
                        } else {
                            //shimmer OFF
                            binding.shimmer.visibility = View.GONE
                            binding.shimmer.stopShimmer()
                            if (adapter.itemCount < 1) {
                                binding.nodata3.visibility = View.VISIBLE
                            } else {
                                binding.nodata3.visibility = View.GONE
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
    private fun navigateToChats(model: ChatListModel) {

        val intent = Intent(requireContext(),ChatDetailActivity::class.java)
        intent.putExtra(Constants.CHATS_DATA, Gson().toJson(model))
        intent.putExtra("data_from","fragment")
        intent.putExtra(Constants.DISPLAY_NAME,model.sender_name)
        intent.putExtra(Constants.DISPLAY_PIC,model.sender_avatar)
        startActivity(intent)
    }
    override fun onChatClick(item: GiverChatModel) {

    }
    class UsersViewholder(val binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(documentSnapshot: ChatListModel) {

        }
    }
}