package com.oss.abraakadabraaapp.fragments

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.MyRequestActivity
import com.oss.abraakadabraaapp.activities.ProductDetailActivity
import com.oss.abraakadabraaapp.adapter.ReceiverAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyRequestBinding
import com.oss.abraakadabraaapp.databinding.FragmentReceiverBinding
import com.oss.abraakadabraaapp.databinding.NoDataLayoutBinding
import com.oss.abraakadabraaapp.response.mainResponse.ReceiverData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReceiverFragment : Fragment(), DefaultLifecycleObserver,
    ReceiverAdapter.TakerAdapterInterface {

    private lateinit var baseAct: BaseActivity

    private lateinit var binding: FragmentReceiverBinding
    private lateinit var myRequestBinding: ActivityMyRequestBinding

    private var receiverList: ArrayList<ReceiverData> = ArrayList()
    private lateinit var receiverAdapter: ReceiverAdapter
    private lateinit var includeNoData: NoDataLayoutBinding

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private val mainViewModel: MainViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(activity as BaseActivity)!! }

    private lateinit var receiverData: ReceiverData
    private lateinit var position: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceiverBinding.inflate(inflater, container, false)
        includeNoData = binding.includeNoData

        init()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentPage = pageStart
        isLoading = false
        isLastPage = false
        noMoreData = false

        receiverList.clear()

        setUpRecyclerView()
        getReceiverList()

    }

    private fun init() {
        baseAct = (activity as BaseActivity)
        myRequestBinding = (activity as MyRequestActivity).binding

        binding.sRLHome.setColorSchemeResources(R.color.theme_color)

        binding.sRLHome.setOnRefreshListener {
            currentPage = pageStart
            receiverList.clear()
            noMoreData = false
            isLastPage = false
            isLoading = false
            getReceiverList()
        }

        LocalBroadcastManager.getInstance(baseAct)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    currentPage = pageStart
                    receiverList.clear()
                    noMoreData = false
                    isLastPage = false
                    isLoading = false
                    getReceiverList()
                }
            }
        }
    }

    private fun setUpObserver() {

        mainViewModel.receiverListSuccess.observe(this@ReceiverFragment) {
            val data = it.data

            if (data.isNotEmpty()) {
                if (currentPage == pageStart) receiverList.clear()
                receiverList.addAll(data)
                receiverAdapter.notifyDataSetChanged()
                includeNoData.clNoData.visibility = View.GONE
                currentPage += 1
            } else {
                noDataFound()
            }

            isLoading = false
            isLastPage = false
            binding.llProgress.visibility = View.GONE
            binding.sRLHome.isRefreshing = false
        }

        mainViewModel.isLoading.observe(this@ReceiverFragment) {
            if (isLoading && isLastPage) {
                binding.llProgress.visibility = View.VISIBLE
            } else {
                binding.llProgress.visibility = View.GONE
                if (!binding.sRLHome.isRefreshing) {
                    baseAct.loader(it)
                }
            }
        }

        mainViewModel.errorMessage.observe(this@ReceiverFragment) {
            if (it.isNotEmpty()) {
//                baseAct.showToast(it)
                isLoading = false
                isLastPage = false
                binding.llProgress.visibility = View.GONE
                binding.sRLHome.isRefreshing = false
            }
        }
    }

    private fun getReceiverList() {
        if (baseAct.isNetworkAvailable()) {

            includeNoData.clNoData.visibility = View.GONE
            binding.rvTaker.visibility = View.VISIBLE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.tabType] = Constants.receiver
            map[RequestKeys.pageNumber] = "$currentPage"

            mainViewModel.getReceiverList(
                Utility.getHeaders(baseAct),
                map
            )
        } else {
            baseAct.showSnackBar(
                myRequestBinding.clRequestActivity,
                baseAct.resources.getString(R.string.no_internet_connection_found)
            )
            binding.sRLHome.isRefreshing = false
        }
    }

    private fun setUpRecyclerView() {

        receiverAdapter = ReceiverAdapter(receiverList, baseAct, this)

        binding.rvTaker.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(baseAct, LinearLayoutManager.VERTICAL, false)
            adapter = receiverAdapter
        }

        binding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

            val lastChild = binding.sv.getChildAt(binding.sv.childCount - 1)

            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - binding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
                    if (!noMoreData) {
                        isLoading = true
                        isLastPage = true
                        getReceiverList()
                    }
                }
            }
        }

    }

    override fun onItemClick(data: ReceiverData, position: Int) {
        receiverData = data
        this.position = position.toString()
        val intent = Intent(baseAct, ProductDetailActivity::class.java)
        intent.putExtra(Constants.productId, data.productId.toString())
        intent.putExtra(Constants.productStatus, data.requestStatus.toString())
        intent.putExtra(Constants.titleStatus, data.requestStatus.toString())
        intent.putExtra(Constants.hasReceiver, Constants.hasReceiver)
        launchActivity.launch(intent)
    }

    private var launchActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    val cancelRequest = data.getStringExtra(Constants.success)!!
                    if (cancelRequest == "1") {
                        receiverList.remove(receiverData)
                        receiverAdapter.notifyDataSetChanged()
                        if(receiverList.isEmpty()){
                            noDataFound()
                        }
                    }
                }
            }
        }

    private fun noDataFound() {
        if (!isLoading && !isLastPage) {
            noMoreData = true
            includeNoData.clNoData.visibility = View.VISIBLE
            includeNoData.tvNoData.text =
                baseAct.resources.getString(R.string.receiving_no_data_message)
            binding.rvTaker.visibility = View.GONE
            includeNoData.ivNoData.setImageDrawable(
                ContextCompat.getDrawable(
                    baseAct,
                    R.drawable.home_toolbar_app_logo
                )
            )
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(this)
    }

    override fun onCreate(owner: LifecycleOwner) {
        lifecycle.removeObserver(this)
        setUpObserver()
    }

    override fun onDetach() {
        super.onDetach()
        lifecycle.removeObserver(this)
    }

}