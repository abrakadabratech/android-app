package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.GiverChatModel
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.activities.newflow.model.NotificationDataModel
import com.oss.abraakadabraaapp.activities.newflow.ui.MyRequestDetailsActivity
import com.oss.abraakadabraaapp.adapter.NotificationAdapter
import com.oss.abraakadabraaapp.databinding.ActivityNewNotificationBinding
import com.oss.abraakadabraaapp.databinding.NotificationRowBinding
import com.oss.abraakadabraaapp.datasource.MainFilterViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModelFactory
import com.oss.abraakadabraaapp.datasource.NotificationViewModel
import com.oss.abraakadabraaapp.datasource.NotificationViewModelFactory
import com.oss.abraakadabraaapp.datasource.ProductAdapter
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.localdb.NotificationEntity
import com.oss.abraakadabraaapp.model.DeleteAll
import com.oss.abraakadabraaapp.model.Notifications
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.retrofit.api.Movie
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_NOTIFICATIONS
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Date

class NewNotificationActivity : BaseActivity(),NotificationAdapter.HandleClicks {
    private lateinit var binding: ActivityNewNotificationBinding
    private lateinit var adapter:NotificationAdapter

    private var actionMode: ActionMode? = null

    private val LIST_STATE_KEY = "recycler_state"
    private var recyclerViewState: Parcelable? = null

    private lateinit var viewModel: NotificationViewModel

    private val authViewModel: MainViewModel by viewModel()
    private var mainListAdapter: ProductAdapter? = null
    private lateinit var nearestViewModel:MainFilterViewModel
    private var mainMenu: Menu? = null


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recyclerViewState = binding.rvNotification.layoutManager?.onSaveInstanceState()
        outState.putParcelable(LIST_STATE_KEY, recyclerViewState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_NOTIFICATIONS, null)
        actionBar?.hide()

        window.statusBarColor =
            ContextCompat.getColor(
                this,
                R.color.blue_status_bar_color
            )
        adapter = NotificationAdapter(this, lifecycleOwner = this,this)


        binding.rvNotification.apply {
            layoutManager = LinearLayoutManager(this@NewNotificationActivity)
            adapter = this@NewNotificationActivity.adapter
        }
        val userLocation = PreferencesManagement.getUserLocation(this)


        viewModel = ViewModelProvider(
            this,
            NotificationViewModelFactory(APIService.getApiService())
        )[NotificationViewModel::class.java]

        lifecycleScope.launch {
            viewModel.notificationList.collectLatest { paginatedData ->
                adapter.submitData(lifecycle, PagingData.empty())
                adapter.submitData(paginatedData)
            }

        }

//        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        //binding.rvNotification.adapter = NewNotificationAdapter(this,4)

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_NOTIFICATIONS)
            onBackPressed()
        }

        binding.deleteBtn.setOnClickListener {
            //Delete Logic
        }
        binding.markRead.setOnClickListener {
            //Mark Read Logic
        }
        binding.optionMenu.setOnClickListener {
            binding.editMenuDialog.visibility = View.VISIBLE
        }
        binding.selectAll.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
            adapter.selectAll()
        }
        binding.deleteAll.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
            authViewModel.deleteAllNotification(DeleteAll(all = true))
        }
        binding.markAllRead.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
            authViewModel.readAllNotification(DeleteAll(all = true))
        }
        binding.editMenuDialog.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
        }
//        binding.editCardview.setOnClickListener {
//            binding.editMenuDialog.visibility = View.VISIBLE
//        }
    }

    private fun setupObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }
        authViewModel.deleteNotificationSuccess.observe(this) {
            if (it.code == 200) {
                showToast("All Notifications Deleted")
                viewModel.refresh()
            }
        }
        authViewModel.deleteMultipleNotificationSuccess.observe(this) {
            if (it.code == 200) {
                showToast("Selected Notifications are Deleted")
                viewModel.refresh()
            }
        }
        authViewModel.readNotificationSuccess.observe(this) {
            if (it.code == 200) {
                showToast("All Notifications Marked As Read")
                viewModel.refresh()
            }
        }
        authViewModel.deleteMultipleNotificationSuccess.observe(this) {
            if (it.code == 200) {
                showToast("Selected Notifications are Marked as read")
                viewModel.refresh()
            }
        }
        authViewModel.deleteNotificationSuccess.observe(this) {
            if (it.code == 200) {
                showToast("All Notifications Deleted")
                viewModel.refresh()
            }
        }

        setupObserver()
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        recyclerViewState = savedInstanceState?.getParcelable(LIST_STATE_KEY)

    }


    fun getTime(unix: String): String {

        try {
            val sdf = SimpleDateFormat("MMM dd,yyyy HH:MM")
            val netDate = Date(unix)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
    fun showHideDelete(show: Boolean){
       binding.deleteBtn.visibility = if(show) View.VISIBLE else View.GONE
    }


    private fun deleteItem() {
        //function for delete items
    }

    private fun markAsReadAll(){

    }

    override fun enableOptions() {
        showHideDelete(true)
    }

    override fun getSelectedItems(notification: List<Notifications>) {

    }

}