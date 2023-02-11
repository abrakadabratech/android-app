package com.oss.abraakadabraaapp.activities

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.NotificationAdapter
import com.oss.abraakadabraaapp.databinding.ActivityNotificationBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.databinding.NoDataLayoutBinding
import com.oss.abraakadabraaapp.response.notificationResponse.NotificationResponse
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.NotificationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class NotificationActivity : BaseActivity(), NotificationAdapter.NotificationAdapterInterface {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding
    private lateinit var includeNoData: NoDataLayoutBinding

    private var notificationList: ArrayList<NotificationResponse.NotificationData> = ArrayList()
    private val notificationAdapter = NotificationAdapter(notificationList, this, this)

    private val notificationViewModel: NotificationViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private lateinit var notificationData: NotificationResponse.NotificationData
    private lateinit var position: String
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        includeNoData = binding.includeNoData

        val view = binding.root
        setContentView(view)

//        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text =
            applicationContext.resources.getString(R.string.notification)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        //setUpRecyclerView()
        setUpObserver()
        getNotificationData()
        LocalBroadcastManager.getInstance(this@NotificationActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    currentPage = pageStart
                    notificationList.clear()
                    noMoreData = false
                    isLastPage = false
                    isLoading = false
                    getNotificationData()
                }
            }
        }
    }

    private fun setUpObserver() {

        notificationViewModel.readNotificationSuccess.observe(this) {
            notificationData.seenStatus = 1
            notificationAdapter.notifyItemChanged(position.toInt())

            notificationList.forEach { notificationData ->
                if (notificationData.seenStatus == 0) {
                    count++
                }
            }

            when (notificationData.module) {
                Constants.pendingIntentRequest -> {
                    var intent =
                        Intent(this@NotificationActivity, ProductDetailActivity::class.java)

                    if (notificationData.role.toString() == Constants.giver) {//role //1-> taker // 2->giver
                        intent = Intent(
                            this@NotificationActivity,
                            RequestProductDetailActivity::class.java
                        )
                        intent.putExtra(Constants.productId, notificationData.moduleId.toString())
                    } else {
                        intent.putExtra(
                            Constants.titleStatus,
                            notificationData.moduleData.toString()
                        )
                        intent.putExtra(
                            Constants.productId,
                            notificationData.moduleData2.toString()
                        )//product id
                    }
                    intent.putExtra(Constants.productStatus, notificationData.moduleData.toString())
                    intent.putExtra(Constants.requestName, notificationData.userName)
                    startActivity(intent)
                }
                Constants.productDetail -> {
                    val intent = Intent(this@NotificationActivity, ProductDetailActivity::class.java)
                    intent.putExtra(Constants.productId, notificationData.moduleId.toString())
                    intent.putExtra(Constants.requestName, notificationData.userName)
                    startActivity(intent)
                }
            }

        }

        notificationViewModel.notificationSuccess.observe(this) {
            val data = it.data
            if (data.isNotEmpty()) {
                if (currentPage == pageStart) notificationList.clear()
                notificationList.addAll(data)
                notificationAdapter.notifyDataSetChanged()
                includeNoData.clNoData.visibility = View.GONE
                binding.rvNotification.visibility = View.VISIBLE
                currentPage += 1
            } else {
                noDataFound()
            }

            notificationList.forEach { notificationData ->
                if (notificationData.seenStatus == 0) {
                    count++
                }
            }

            isLoading = false
            isLastPage = false
            binding.llProgress.visibility = View.GONE
        }

        notificationViewModel.isLoading.observe(this) {
            if (isLoading && isLastPage) {
                binding.llProgress.visibility = View.VISIBLE
            } else {
                binding.llProgress.visibility = View.GONE
                loader(it)
            }
        }

        notificationViewModel.errorMessage.observe(this) {
            if (it.isNotBlank()) showToast(it)
        }

    }

    private fun getNotificationData() {
        if (isNetworkAvailable()) {

            includeNoData.clNoData.visibility = View.GONE
            binding.rvNotification.visibility = View.VISIBLE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.pageNumber] = "$currentPage"

            notificationViewModel.getAllNotification(
                Utility.getHeaders(this@NotificationActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clNotificationActivity,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun setUpRecyclerView() {
        binding.rvNotification.isNestedScrollingEnabled = false
        binding.rvNotification.setHasFixedSize(false)
        binding.rvNotification.layoutManager =
            LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false)
//        binding.rvNotification.adapter = notificationAdapter

        binding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            val lastChild = binding.sv.getChildAt(binding.sv.childCount - 1)
            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - binding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
                    if (!noMoreData) {
                        isLoading = true
                        isLastPage = true
                        getNotificationData()
                    }
                }
            }
        }
    }

    override fun onItemClick(data: NotificationResponse.NotificationData, position: Int) {

        if (isNetworkAvailable()) {
            notificationData = data
            this.position = position.toString()

            val map = HashMap<String, String>()

            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.notificationId] = data.id.toString()

            readNotification(map)
        } else {
            showSnackBar(
                binding.clNotificationActivity,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun readNotification(map: HashMap<String, String>) {
        notificationViewModel.readNotification(Utility.getHeaders(this@NotificationActivity), map)
    }

    private fun noDataFound() {
        if (!isLoading && !isLastPage) {
            noMoreData = true
            includeNoData.clNoData.visibility = View.VISIBLE
            includeNoData.tvNoData.text =
                applicationContext.resources.getString(R.string.no_notification_found)
            binding.rvNotification.visibility = View.GONE
            includeNoData.ivNoData.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.home_toolbar_app_logo
                )
            )
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(Constants.success, count.toString())
        setResult(RESULT_OK, intent)
        finish()
    }
}