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
import com.oss.abraakadabraaapp.activities.RequestProductDetailActivity
import com.oss.abraakadabraaapp.adapter.GiverAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyRequestBinding
import com.oss.abraakadabraaapp.databinding.FragmentGiverBinding
import com.oss.abraakadabraaapp.databinding.NoDataLayoutBinding
import com.oss.abraakadabraaapp.response.mainResponse.GiverData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class GiverFragment : Fragment(), GiverAdapter.GiverAdapterInterface, DefaultLifecycleObserver {

    private lateinit var baseAct: BaseActivity

    private lateinit var binding: FragmentGiverBinding
    private lateinit var myRequestBinding: ActivityMyRequestBinding

    private var giverList: ArrayList<GiverData> = ArrayList()
    private lateinit var giverAdapter: GiverAdapter
    private lateinit var includeNoData: NoDataLayoutBinding

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private val mainViewModel: MainViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(activity as BaseActivity) }

    private lateinit var giverData: GiverData
    private lateinit var position: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGiverBinding.inflate(inflater, container, false)
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

        giverList.clear()

        setUpRecyclerView()
        getGiverList()

    }

    private fun init() {
        baseAct = (activity as BaseActivity)
        myRequestBinding = (activity as MyRequestActivity).binding

        binding.sRLHome.setColorSchemeResources(R.color.theme_color)

        binding.sRLHome.setOnRefreshListener {
            currentPage = pageStart
            giverList.clear()
            noMoreData = false
            isLastPage = false
            isLoading = false
            getGiverList()
        }

        LocalBroadcastManager.getInstance(baseAct)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))
    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    currentPage = pageStart
                    giverList.clear()
                    noMoreData = false
                    isLastPage = false
                    isLoading = false
                    getGiverList()

                }

            }
        }
    }

    private fun setUpObserver() {

        mainViewModel.unAuthorization.observe(this@GiverFragment) {
            if(it) baseAct.backToLogIn()
        }
        mainViewModel.giverListSuccess.observe(this@GiverFragment) {
            val data = it.data

            if (data.isNotEmpty()) {
                if (currentPage == pageStart) giverList.clear()
                giverList.addAll(data)
                giverAdapter.notifyDataSetChanged()
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

        mainViewModel.isLoading.observe(this@GiverFragment) {
            if (isLoading && isLastPage) {
                binding.llProgress.visibility = View.VISIBLE
            } else {
                binding.llProgress.visibility = View.GONE
                if (!binding.sRLHome.isRefreshing) {
                    baseAct.loader(it)
                }
            }
        }

        mainViewModel.errorMessage.observe(this@GiverFragment) {
            if (it.isNotEmpty()) {
//                baseAct.showToast(it)
                isLoading = false
                isLastPage = false
                binding.llProgress.visibility = View.GONE
                binding.sRLHome.isRefreshing = false
            }
        }
    }

    private fun getGiverList() {
        if (baseAct.isNetworkAvailable()) {

            includeNoData.clNoData.visibility = View.GONE
            binding.rvGiver.visibility = View.VISIBLE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = if(userData != null) userData!!.id.toString() else ""
            map[RequestKeys.tabType] = Constants.giver
            map[RequestKeys.pageNumber] = "$currentPage"

            mainViewModel.getGiverList(
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
        giverAdapter = GiverAdapter(giverList, baseAct, this)

        binding.rvGiver.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(baseAct, LinearLayoutManager.VERTICAL, false)
            adapter = giverAdapter
        }

        binding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

            val lastChild = binding.sv.getChildAt(binding.sv.childCount - 1)

            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - binding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
                    if (!noMoreData) {
                        isLoading = true
                        isLastPage = true
                        getGiverList()
                    }
                }
            }
        }

    }

    override fun onItemClick(data: GiverData) {
        giverData = data
        val intent = Intent(
            baseAct,
            RequestProductDetailActivity::class.java
        )
        intent.putExtra(Constants.productId, data.id.toString())
        intent.putExtra(Constants.hasReceiver, Constants.hasReceiver)
        launchActivity.launch(intent)

    }

    private var launchActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data != null && data.hasExtra(Constants.success)) {
                    val status = data.getStringExtra(Constants.success)!!
                    if (status == "1") {
                        giverData.requestStatus = 1
                    }
                    if (status == "2") {
                        giverData.requestStatus = 2
                    }
                    giverAdapter.notifyDataSetChanged()
                }
            }
        }


    private fun noDataFound() {
        noMoreData = true
        if (!isLoading && !isLastPage) {
            includeNoData.clNoData.visibility = View.VISIBLE
            includeNoData.tvNoData.text =
                baseAct.resources.getString(R.string.giving_no_data_message)
            binding.rvGiver.visibility = View.GONE
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