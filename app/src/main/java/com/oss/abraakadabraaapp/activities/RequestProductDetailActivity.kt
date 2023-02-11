package com.oss.abraakadabraaapp.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.ProductFeature
import com.oss.abraakadabraaapp.adapter.ProductFeatureAdapter
import com.oss.abraakadabraaapp.adapter.ProductSliderAdapter
import com.oss.abraakadabraaapp.databinding.ActivityRequestProductDetailBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.response.mainResponse.ProductImage
import com.oss.abraakadabraaapp.response.mainResponse.RequestProductDetailData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.set

class RequestProductDetailActivity : BaseActivity(),
    ProductSliderAdapter.ProductSliderAdapterInterface {

    private lateinit var binding: ActivityRequestProductDetailBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding

    private var productImageList: ArrayList<ProductImage> = ArrayList()
    private val productImageSlider = ProductSliderAdapter(productImageList, this, this)

    private var productFeatureList: ArrayList<ProductFeature> = ArrayList()
    private val productFeatureAdapter = ProductFeatureAdapter(productFeatureList, this)

    private var currentPosition: Int = 0

    private lateinit var productId: String

    private lateinit var requestProductDetailData: RequestProductDetailData

    private val mainViewModel: MainViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(this) }

    private var status = "0"

    companion object {
        fun createIntent(context: Context, productId: String): Intent {
            val intent = Intent(
                context,
                RequestProductDetailActivity::class.java
            )
            intent.putExtra(Constants.productId, productId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestProductDetailBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text =
            applicationContext.resources.getString(R.string.request_details)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(Constants.productId)) {
            productId = intent.getStringExtra(Constants.productId)!!
        }

        setUpObserver()
        getRequestProductDetail()

        LocalBroadcastManager.getInstance(this@RequestProductDetailActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    getRequestProductDetail()
                }
            }
        }
    }


    private fun setUpObserver() {

        mainViewModel.unAuthorization.observe(this) {
            if (it) backToLogIn()
        }

        mainViewModel.requestActionSuccess.observe(this) {
            showToast(it.responseMessage)
            binding.rejectBtn.visibility = View.GONE
            binding.acceptBtn.visibility = View.GONE

            binding.disableBtn.visibility = View.VISIBLE

            val data = it.data
            when (data.requestStatus) {
                Constants.accept -> {
                    status = "1"
                    binding.tvDisableBtn.text = resources.getString(R.string.accepted)
                    binding.ivWhatsapp.visibility = View.VISIBLE
                    binding.ivWhatsapp.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RequestProductDetailActivity,
                            R.drawable.active_what_app
                        )
                    )
                }
                Constants.reject -> {
                    status = "2"
                    binding.tvDisableBtn.text = resources.getString(R.string.rejected)
                }
            }
        }

        mainViewModel.requestProductDetailSuccess.observe(this) {
            if (it.data != null) {
                requestProductDetailData = it.data
                binding.tvCancelRequest.visibility = View.GONE
                setData()
            } else {
                binding.sv.visibility = View.GONE

                if (intent.hasExtra(Constants.requestName)) {
                    val str =
                        "${intent.getStringExtra(Constants.requestName)} has cancelled request on your product."
                    binding.tvCancelRequest.text = str
                }
                binding.tvCancelRequest.visibility = View.VISIBLE
            }
        }

        mainViewModel.isLoading.observe(this) {
            loader(it)
        }

        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

    }

    private fun setData() {

        with(binding) {

            productImageList.clear()
            productImageList.addAll(requestProductDetailData.images)
            viewPager.addOnPageChangeListener(myPageChangeCallback)
            viewPager.adapter = productImageSlider

            val value = "${viewPager.currentItem + 1}/${requestProductDetailData.images.size}"
            tvImageCount.visibility = View.VISIBLE
            tvImageCount.text = value

            tvProductName.text = requestProductDetailData.title
            tvProductLocation.text = requestProductDetailData.fullAddress
            tvProductCategory.text = requestProductDetailData.categoryName
            tvProductDescription.text = requestProductDetailData.description

            productFeatureList.clear()
            productFeatureList.addAll(
                arrayListOf(
                    ProductFeature(
                        resources.getString(R.string.gender),
                        requestProductDetailData.genderFor
                    ),
                    ProductFeature(
                        resources.getString(R.string.condition),
                        requestProductDetailData.quality
                    ),
                    ProductFeature(
                        resources.getString(R.string.how_old),
                        requestProductDetailData.productAge
                    ),
                    ProductFeature(
                        resources.getString(R.string.brand),
                        requestProductDetailData.brand
                    ),
                )
            )

            rvProductFeatures.apply {
                layoutManager = GridLayoutManager(this@RequestProductDetailActivity, 2)
                adapter = productFeatureAdapter
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
            }

            val receiverData = requestProductDetailData.takerData

            val str = "${receiverData.name} has cancelled request on your product."
            tvCancelRequest.text = str

            tvReceiverName.text = receiverData.name
            tvReceiverEmail.text = receiverData.email
            tvReceiverNumber.text = receiverData.phoneNumber
            tvTime.text = receiverData.createdAt
            tvSummary.text = receiverData.message

            ImageUtils.setImage(
                this@RequestProductDetailActivity,
                ivReceiverProfile,
                receiverData.profileImage,
                progressBar,
                R.drawable.default_user_profile
            )

            if (receiverData.fullAddress != null) {
                llReceiverLocation.visibility = View.VISIBLE
                tvReceiverLocation.text = receiverData.fullAddress
            }

            ivWhatsapp.setOnClickListener {
                LaunchUtility.whatsAppIntent(
                    requestProductDetailData.takerData.phoneNumber,
                    this@RequestProductDetailActivity
                )
            }

            when (receiverData.requestStatus.toString()) {
                Constants.accept -> {
                    binding.rejectBtn.visibility = View.GONE
                    binding.acceptBtn.visibility = View.GONE

                    binding.disableBtn.visibility = View.VISIBLE
                    binding.tvDisableBtn.text = resources.getString(R.string.accepted)
                    binding.ivWhatsapp.visibility = View.VISIBLE
                    ivWhatsapp.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@RequestProductDetailActivity,
                            R.drawable.active_what_app
                        )
                    )

                }
                Constants.reject -> {
                    binding.rejectBtn.visibility = View.GONE
                    binding.acceptBtn.visibility = View.GONE

                    binding.disableBtn.visibility = View.VISIBLE
                    binding.tvDisableBtn.text = resources.getString(R.string.rejected)
                }
                Constants.pending -> {
                    rejectBtn.visibility = View.VISIBLE
                }

            }

            rejectBtn.setOnClickListener {
                requestAction(Constants.reject)
            }

            acceptBtn.setOnClickListener {
                requestAction(Constants.accept)
            }

            if (receiverData.name.isBlank() && receiverData.email.isBlank()) {

                clReceiverInfo.visibility = View.GONE
                ivWhatsapp.visibility = View.GONE
                tvReceiverInfoTitle.visibility = View.GONE

                tvUserDeleted.visibility = View.VISIBLE
                tvUserDeleted.text = resources.getString(R.string.user_does_exist_message)
            }

            sv.visibility = View.VISIBLE

        }

    }

    private fun requestAction(requestStatus: String) {
        if (isNetworkAvailable()) {

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = if (userData != null) userData!!.id.toString() else ""
            map[RequestKeys.requestId] = productId
            map[RequestKeys.requestStatus] = requestStatus
            map[RequestKeys.productId] = requestProductDetailData.id.toString()

            mainViewModel.requestAction(
                Utility.getHeaders(this@RequestProductDetailActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clRequestProductDetail,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun getRequestProductDetail() {
        if (isNetworkAvailable()) {

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = if(userData != null) userData!!.id.toString() else ""
            map[RequestKeys.requestId] = productId

            mainViewModel.getRequestProductDetail(
                Utility.getHeaders(this@RequestProductDetailActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clRequestProductDetail,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    override fun onProductImageClick(data: ArrayList<ProductImage>) {
        startActivity(MultipleImageActivity.createIntent(this@RequestProductDetailActivity, data))
    }

    private var myPageChangeCallback = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            currentPosition = position
            binding.viewPager.currentItem = position

            val value =
                "${binding.viewPager.currentItem + 1}/${requestProductDetailData.images.size}"
            binding.tvImageCount.text = value
        }

        override fun onPageScrollStateChanged(state: Int) {}

    }

    override fun onBackPressed() {
        if (intent.hasExtra(Constants.hasReceiver)) {
            val intent = Intent()
            intent.putExtra(Constants.success, status)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            super.onBackPressed()
        }
        if (intent.hasExtra(Constants.hasNotificationData)) {
//            startActivity(HomeActivity.createIntent(this@RequestProductDetailActivity))
        }
    }
}