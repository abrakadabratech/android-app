package com.oss.abraakadabraaapp.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.ProductFeature
import com.oss.abraakadabraaapp.adapter.ProductFeatureAdapter
import com.oss.abraakadabraaapp.adapter.ProductSliderAdapter
import com.oss.abraakadabraaapp.databinding.ActivityProductDetailBinding
import com.oss.abraakadabraaapp.databinding.DialogRequestBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.model.EditProductData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.response.mainResponse.ProductDetailData
import com.oss.abraakadabraaapp.response.mainResponse.ProductImage
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.set

class ProductDetailActivity : BaseActivity(), ProductSliderAdapter.ProductSliderAdapterInterface {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding
    private lateinit var requestDialogBinding: DialogRequestBinding

    private var sliderList: ArrayList<ProductImage> = ArrayList()
    private val sliderAdapter = ProductSliderAdapter(sliderList, this, this)

    private var productFeatureList: ArrayList<ProductFeature> = ArrayList()
    private val productFeatureAdapter = ProductFeatureAdapter(productFeatureList, this)

    private var currentPosition: Int = 0

    private lateinit var productId: String
    private lateinit var productStatus: String

    private var isAllotted = false

    private lateinit var productDetailData: ProductDetailData

    private val mainViewModel: MainViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private var editProduct = false

    private var cancelRequest = "0"

    override fun onBackPressed() {
        if (editProduct) {
            val intent = Intent()
            intent.putExtra(Constants.success, Constants.success)
            intent.putExtra(
                Constants.editProduct, LatestProductData(
                    0.0,
                    productDetailData.fullAddress,
                    productDetailData.userId,
                    productDetailData.images[0].image,
                    productDetailData.id,
                    productDetailData.id,
                    productDetailData.title,
                    0
                )
            )
            setResult(RESULT_OK, intent)
            finish()
        } else {
            if (intent.hasExtra(Constants.hasReceiver)) {
                val intent = Intent()
                intent.putExtra(Constants.success, cancelRequest)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                super.onBackPressed()
            }
            if (intent.hasExtra(Constants.hasNotificationData)) {
//                startActivity(HomeActivity.createIntent(this@ProductDetailActivity))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text =
            if (intent.hasExtra(Constants.titleStatus)) applicationContext.resources.getString(R.string.request_details) else applicationContext.resources.getString(
                R.string.product_details
            )

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(Constants.productId)) {
            productId = intent.getStringExtra(Constants.productId)!!
        }

        if (intent.hasExtra(Constants.productStatus)) {
            productStatus = intent.getStringExtra(Constants.productStatus)!!
        }

        setUpObserver()
        getProductDetail()

        LocalBroadcastManager.getInstance(this@ProductDetailActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    getProductDetail()
                }
            }
        }
    }


    private fun setUpObserver() {

        mainViewModel.cancelProductSuccess.observe(this) {
            showToast(it.responseMessage)
            cancelRequest = "1"
            enableBtn()
        }

        mainViewModel.deleteProductSuccess.observe(this) {
            showToast(it.responseMessage)
            val intent = Intent()
            intent.putExtra(Constants.success, Constants.failure)
            setResult(RESULT_OK, intent)
            finish()
        }

        mainViewModel.makeARequestSuccess.observe(this) {
            showToast(it.responseMessage)
            binding.btnText.text = applicationContext.resources.getString(R.string.requested)
            disableBtn()
        }

        mainViewModel.getProductDetailSuccess.observe(this) {
            if (it.data != null) {
                productDetailData = it.data
                setData()
            } else {
                showToast("Product is Deleted.")
            }

        }

        mainViewModel.isLoading.observe(this) {
            loader(it)
        }

        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

    }

    private fun setData() {

        with(binding) {

            sliderList.clear()
            sliderList.addAll(productDetailData.images)
            viewPager.addOnPageChangeListener(myPageChangeCallback)
            viewPager.adapter = sliderAdapter

            val value = "${viewPager.currentItem + 1}/${productDetailData.images.size}"
            tvImageCount.visibility = View.VISIBLE
            tvImageCount.text = value

            tvProductName.text = productDetailData.title
            tvProductLocation.text = productDetailData.fullAddress
            tvProductCategory.text = productDetailData.categoryName
            tvProductDescription.text = productDetailData.description

            productFeatureList.clear()
            productFeatureList.addAll(
                arrayListOf(
                    ProductFeature(
                        resources.getString(R.string.gender),
                        productDetailData.genderFor
                    ),
                    ProductFeature(
                        resources.getString(R.string.condition),
                        productDetailData.quality
                    ),
                    ProductFeature(
                        resources.getString(R.string.how_old),
                        productDetailData.productAge
                    ),
                    ProductFeature(resources.getString(R.string.brand), productDetailData.brand),
                )
            )

            rvProductFeatures.apply {
                layoutManager = GridLayoutManager(this@ProductDetailActivity, 2)
                adapter = productFeatureAdapter
                isNestedScrollingEnabled = false
                setHasFixedSize(false)
            }

            if (userData.id != productDetailData.userId) {
                requestBtn.visibility = View.VISIBLE
                ivWhatsapp.visibility = View.VISIBLE
            }

            requestBtn.setOnClickListener {
                requestDialog()
            }

            if (productDetailData.userId == userData.id) {
                includeToolbar.ivMenu.visibility = View.VISIBLE
            }

            includeToolbar.ivMenu.setOnClickListener {
                menuDropDown(includeToolbar.ivMenu)
            }

            val requestData = productDetailData.requestData

            if (intent.hasExtra(Constants.productStatus)) {
                if (requestData.isRequested == 1) {
                    btnStatus(productStatus)
                } else {
                    if (requestData.isRequested == 0) {
                        if (requestData.isAllotted == 1) {
                            isAllotted = true
                            btnText.text = resources.getString(R.string.given)
                        }
                    }
                }
            } else {
                if (requestData.isRequested == 1) {
                    btnStatus(requestData.requestStatus.toString())
                } else {
                    if (requestData.isRequested == 0) {
                        if (requestData.isAllotted == 1) {
                            isAllotted = true
                            btnText.text = resources.getString(R.string.given)
                        }
                    }
                }
            }

            sv.visibility = View.VISIBLE

            if (intent.hasExtra(Constants.titleStatus)) {
                tvGiverInfoTitle.visibility = View.VISIBLE
                clGiverInfo.visibility = View.VISIBLE



                if (productDetailData.profileImage != null) {
                    ImageUtils.setImage(
                        this@ProductDetailActivity,
                        ivGiverProfile,
                        productDetailData.profileImage ?: "",
                        progressBar,
                        R.drawable.default_user_profile
                    )
                }

                tvGiverName.text = productDetailData.userName ?: ""

                // Giver info should be invisible if rejects the order

                // Fixed-2: Only giver accepts the order then only requester can able to see the givers information
                if(productStatus.equals(Constants.accept)){
                    tvGiverEmail.text = productDetailData.userEmail ?: ""
                    imageView2.visibility = View.VISIBLE
                    imageView3.visibility = View.VISIBLE

                    if (productDetailData.userFullAddress != null) {
                        llGiverLocation.visibility = View.VISIBLE
                        tvGiverLocation.text = productDetailData.userFullAddress
                    }

                    if (productDetailData.phoneNumber != null) {
                        clGiverPhoneNumber.visibility = View.VISIBLE
                        tvGiverNumber.text = productDetailData.phoneNumber
                    }

                    if (productDetailData.userName == null && productDetailData.userEmail == null) {
                        clGiverInfo.visibility = View.GONE
                        ivWhatsapp.visibility = View.GONE
                        tvGiverInfoTitle.visibility = View.GONE
                        tvUserDeleted.visibility = View.VISIBLE
                        tvUserDeleted.text = resources.getString(R.string.user_does_exist_message)
                    }
                }
            }

            cancelBtn.setOnClickListener {
                cancelDialog()
            }

        }

    }

    private fun btnStatus(productStatus: String) {

        with(binding) {
            when (productStatus) {
                Constants.reject -> {
                    // write logic for rejected items...
                    if (intent.hasExtra(Constants.newRequest)) {
                        enableBtn()
                        btnText.text = resources.getString(R.string.request)
                    } else {
                        enableBtn()
                        btnText.text = resources.getString(R.string.rejected)
                    }
                }
                Constants.pending -> {
                    disableBtn()
                    btnText.text = resources.getString(R.string.requested)
                }
                Constants.accept -> {
                    disableBtn()
                    btnText.text = resources.getString(R.string.accepted)
                    ivWhatsapp.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@ProductDetailActivity,
                            R.drawable.active_what_app
                        )
                    )
                    ivWhatsapp.setOnClickListener {
                        if (productDetailData.phoneNumber != null) {
                            LaunchUtility.whatsAppIntent(
                                productDetailData.phoneNumber!!,
                                this@ProductDetailActivity
                            )
                        } else {
                            showToast("Number not Provided")
                        }
                    }
                }
            }
        }
    }

    private fun menuDropDown(
        ivMenu: ImageView,
    ) {
        val popup = PopupMenu(this@ProductDetailActivity, ivMenu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> editProduct()
                R.id.delete -> deleteDialog()
            }
            popup.dismiss()
            true
        }

        popup.inflate(R.menu.item_product_menu)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.d("MenuError", e.localizedMessage!!)
        } finally {
            popup.show()
        }

    }

    private fun editProduct() {

        val editProductData = EditProductData(
            productDetailData.id.toString(),
            productDetailData.title,
            productDetailData.genderFor,
            productDetailData.categoryId.toString(),
            productDetailData.brand,
            productDetailData.productAge,
            productDetailData.quality,
            productDetailData.fullAddress,
            productDetailData.lat,
            productDetailData.lng,
            productDetailData.description,
            productDetailData.images,
        )

        val intent = Intent(this@ProductDetailActivity, AddProductActivity::class.java)
        intent.putExtra(Constants.editProduct, editProductData)
        launchAddProductActivity.launch(intent)

    }

    private var launchAddProductActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: String = data!!.getStringExtra(Constants.success)!!
                if (intentData == Constants.success) {
                    editProduct = true
                    getProductDetail()
                }
            }
        }

    private fun cancelDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setTitle(R.string.cancel)
            .setMessage(R.string.cancel_product_message)
            .setCancelable(false)

        dialogBuilder.setPositiveButton(resources.getString(R.string.yes_string), null)

        dialogBuilder.setNegativeButton(resources.getString(R.string.no_string), null)

        val alertDialog = dialogBuilder.create()

        alertDialog.setOnShowListener {

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                val map = HashMap<String, String>()
                map[RequestKeys.userId] = userData.id.toString()
                map[RequestKeys.productId] = productDetailData.id.toString()

                mainViewModel.cancelProduct(Utility.getHeaders(this), map)

                alertDialog.dismiss()
            }
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun deleteDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setTitle(R.string.delete_str)
            .setMessage(R.string.delete_product_message)
            .setCancelable(false)

        dialogBuilder.setPositiveButton(resources.getString(R.string.yes_string), null)

        dialogBuilder.setNegativeButton(resources.getString(R.string.no_string), null)

        val alertDialog = dialogBuilder.create()

        alertDialog.setOnShowListener {

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                val map = HashMap<String, String>()
                map[RequestKeys.userId] = userData.id.toString()
                map[RequestKeys.productId] = productDetailData.id.toString()

                mainViewModel.deleteProduct(Utility.getHeaders(this), map)

                alertDialog.dismiss()
            }
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun enableBtn() {
        Log.d("MYT", "else enableBtn")
        with(binding) {
            ivWhatsapp.setImageDrawable(
                ContextCompat.getDrawable(
                    this@ProductDetailActivity,
                    R.drawable.in_active_what_app
                )
            )
            ivWhatsapp.setOnClickListener {}
            cancelBtn.visibility = View.GONE
            requestBtn.isEnabled = true
            btnText.text = resources.getText(R.string.request)
            btnText.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailActivity,
                    R.color.white
                )
            )
            llRequestedBtn.background =
                ContextCompat.getDrawable(this@ProductDetailActivity, R.drawable.bg_btn)
        }
    }

    private fun disableBtn() {
        with(binding) {
            cancelBtn.visibility = View.VISIBLE
            requestBtn.isEnabled = false
            btnText.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailActivity,
                    R.color.theme_color
                )
            )
            llRequestedBtn.background =
                ContextCompat.getDrawable(this@ProductDetailActivity, R.drawable.bg_disable_btn)
        }
    }

    private fun getProductDetail() {
        if (isNetworkAvailable()) {

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.productId] = productId

            mainViewModel.getProductDetail(
                Utility.getHeaders(this@ProductDetailActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clProductDetail,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun requestDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        requestDialogBinding = DialogRequestBinding.inflate(layoutInflater)
        val view = requestDialogBinding.root

        dialogBuilder
            .setView(view)
            .setCancelable(false)

        val alertDialog = dialogBuilder.create()

        with(requestDialogBinding) {

            etRequestMessage.filterEmoji()

            if (isAllotted) {
                clProductAllotted.visibility = View.VISIBLE
                clMain.visibility = View.GONE
            } else {
                clProductAllotted.visibility = View.GONE
                clMain.visibility = View.VISIBLE
            }

            cancelBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            allottedBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            requestBtn.setOnClickListener {

                if (etRequestMessage.text.toString().trim().isEmpty()) {
                    textInputRequestMessage.error = "Please Request Message"
                } else {
                    textInputRequestMessage.isErrorEnabled = false
                    if (isNetworkAvailable()) {
                        makeRequest()
                        alertDialog.dismiss()
                    } else {
                        textInputRequestMessage.error =
                            applicationContext.resources.getString(R.string.no_internet_connection_found)

                    }
                }
            }

        }

        alertDialog.window?.setBackgroundDrawableResource(R.color.loadingDialogBackgroundColor)
        alertDialog.show()
    }

    private fun makeRequest() {
        if (isNetworkAvailable()) {
            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.productId] = productId
            map[RequestKeys.message] = requestDialogBinding.etRequestMessage.text.toString().trim()

            mainViewModel.makeARequest(
                Utility.getHeaders(this@ProductDetailActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clProductDetail,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    override fun onProductImageClick(data: ArrayList<ProductImage>) {
        startActivity(MultipleImageActivity.createIntent(this@ProductDetailActivity, data))
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

            val value = "${binding.viewPager.currentItem + 1}/${productDetailData.images.size}"
            binding.tvImageCount.text = value
        }

        override fun onPageScrollStateChanged(state: Int) {}

    }

}