package com.oss.abraakadabraaapp.activities.newflow

import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CatMainAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.CategoryDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.ConditionDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestedUsersAdapter
import com.oss.abraakadabraaapp.activities.newflow.model.AllCategoryResponse
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.databinding.ActivityMyListingDetailsBinding
import com.oss.abraakadabraaapp.datasource.MainFilterViewModel
import com.oss.abraakadabraaapp.datasource.MainViewModelFactory
import com.oss.abraakadabraaapp.datasource.ProductAdapter
import com.oss.abraakadabraaapp.datasource.RequestViewModel
import com.oss.abraakadabraaapp.datasource.RequestedUsersAdapter
import com.oss.abraakadabraaapp.datasource.RequestsViewModelFactory
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.Requests
import com.oss.abraakadabraaapp.retrofit.api.APIService
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_MYLISTING_DETAILS
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MyListingDetialActivity : BaseActivity(), RequestedUsersAdapter.OnRequestClicks,
    CategoryDialogAdapter.CategoryDialogAdapterInterface,
    CatMainAdapter.MainCategoryAdapterInterface, ConditionDialogAdapter.ConditionAdapterInterface {
    lateinit var application: BaseActivity
    private val mainViewModel: AuthViewModel by viewModel()
    var productDetails: ListingResponse? = null
    private lateinit var binding: ActivityMyListingDetailsBinding
    private lateinit var productId: String

    lateinit var alertDialog: AlertDialog
    lateinit var alertAdaper: CategoryDialogAdapter
    lateinit var condtionAdapter: ConditionDialogAdapter
    lateinit var mainCatAdapter: CatMainAdapter
    lateinit var mainAdapterList: ArrayList<UserCatData>
    var list = arrayListOf<CatData>()
    var listConditon = arrayListOf<CatData>()
    private var PROD_CATEGORY: String = ""
    private var PROD_CONDITION: String = ""
    private var PROD_USED_FOR: String = ""

    private var selectedProdCategory: String = ""
    private lateinit var placesClient: PlacesClient

    lateinit var userCatData: AllCategoryResponse
    private var requestsAdapter: RequestedUsersAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.productId)) {
            productId = intent.getStringExtra(Constants.productId)!!
        }

        application = this
        application.postEvent(Constants.PAGE_MY_LISTING_DETAIL, null)

        loadUsedForData()
        loadConditionData()

        setupList()
        loaddata()
        val apiKey = BuildConfig.API_KEY

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }

        placesClient = Places.createClient(this)

        userCatData = PreferencesManagement.getCategories(this)!!

        if (userCatData.data.size > 0) {
            mainAdapterList = userCatData.data
            mainAdapterList[0].isSelect = true
        }
        binding.locationName.setOnClickListener {
            locationPicker()
        }

        //Cat adpater
        mainCatAdapter = CatMainAdapter(this, mainAdapterList, this)

        //Used for adapter
        alertAdaper = CategoryDialogAdapter(this, list, this, "")

        //Condition adapter
        condtionAdapter = ConditionDialogAdapter(this, listConditon, this)

//        tempData()

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_MYLISTING_DETAILS)
            onBackPressed()
        }

        binding.ivMenu.setOnClickListener {
            postClick(Constants.BUTTON_MENU_IN_DETAILS_PAGE)
            binding.editMenuDialog.visibility = View.VISIBLE
        }

        binding.editProduct.setOnClickListener {
            postClick(Constants.BUTTON_EDIT_PRODUCT)
            if (productDetails?.product?.status == "active") {
                val intent = Intent(this, EditProductActivity::class.java)
                intent.putExtra("data_from_listing", Gson().toJson(productDetails))
                startActivity(intent)
                binding.editMenuDialog.visibility = View.GONE
            } else if (productDetails?.product?.status == "given") {
                binding.editMenuDialog.visibility = View.GONE
                showToast("Your product is given")
            }else{
                showToast("Edit not allowed!")
            }
        }

        binding.conditionTxt.setOnClickListener {
            //condition popup
            for (i in listConditon) {
                i.isSelect =
                    i.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } == productDetails?.product?.condition.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
            showConditionDialog()
        }
        binding.usedForTxt.setOnClickListener {
            //used for popup
            for (i in list) {
                i.isSelect =
                    i.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } == productDetails?.product?.usedFor.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
            showUsedForDialog()
        }

        binding.categoryTxt.setOnClickListener {
            //category pop up
            for (i in mainAdapterList) {
                i.isSelect =
                    i.title?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } == productDetails?.product?.category.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
            showCategoryFilterDialog()
        }

        binding.locationName.setOnClickListener {
            //Location should be shown here!!!

        }
        binding.editMenuDialog.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
        }

        binding.editCardview.setOnClickListener {
            binding.editMenuDialog.visibility = View.VISIBLE
        }


        binding.shareProduct.setOnClickListener {
            postClick(Constants.BUTTON_SHARE_PRODUCT)
            binding.editMenuDialog.visibility = View.GONE
            loadShareData()
        }

        binding.deleteProduct.setOnClickListener {
            postClick(Constants.BUTTON_DELETE_PRODUCT)


            if (productDetails?.product?.status == "given") {
                binding.editMenuDialog.visibility = View.GONE
                showToast("Your product is given")
            } else {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Alert!")
                alertDialog.setMessage("Are you sure you want to delete your listing?")

                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    if (isNetworkAvailable()) {
                        mainViewModel.deleteProduct(productId)
                    }
                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
                alertDialog.show()
            }
        }

        setUpObserver()

        LocalBroadcastManager.getInstance(this@MyListingDetialActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))


    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    loaddata()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.editMenuDialog.visibility = View.GONE
    }

    private fun locationPicker() {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("IN")
            .build(this)
        locationLauncher.launch(intent)
    }

    private var locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK)
                if (result != null) {
                    val data: Intent? = result.data
                    if (data != null) {

                        val place = Autocomplete.getPlaceFromIntent(data)

//                        latitude = place.latLng!!.latitude.toString()
//                        longitude = place.latLng!!.longitude.toString()
                        Constants.fullAddress = if (place.address != null) {
                            place.address!!
                        } else {
                            "TODO geo api required"
                        }

                        binding.locationName.setText(Constants.fullAddress)
                    }
                }
        }

    private fun loadConditionData(): ArrayList<CatData> {
        listConditon.clear()
        listConditon.add(CatData("Almost New", true))
        listConditon.add(CatData("Good", false))
        listConditon.add(CatData("Average", false))
        listConditon.add(CatData("Needs Repair", false))

        return listConditon
    }

    private fun loadUsedForData(): ArrayList<CatData> {
        list.clear()
        list.add(CatData("Less Than 6 Months", true))
        list.add(CatData("6 Months to 1 Year", false))
        list.add(CatData("1 Year to 3 Years", false))
        list.add(CatData("More than 3 Years", false))

        return list
    }

    private fun loaddata() {
        binding.shimmerLayout.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token

        mainViewModel.getListingDetailsV2(map, productId)

        val viewModel =
            ViewModelProvider(
                this,
                RequestsViewModelFactory(
                    APIService.getApiService(),
                    map,productId)
            )[RequestViewModel::class.java]
        requestsAdapter!!.submitData(lifecycle, PagingData.empty())
        lifecycleScope.launch {
            viewModel.listRequests.collect{
                launch(Dispatchers.Main){
                    requestsAdapter!!.loadStateFlow.collectLatest { loadState->
                        if (loadState.refresh is LoadState.Loading) {
//                                    application.loader(true)
                            binding.shimmerLayout.visibility = View.VISIBLE
                            binding.shimmerLayout.startShimmer()
                        } else {
                            binding.shimmerLayout.visibility = View.GONE
                            binding.shimmerLayout.stopShimmer()
                        }
                    }
                }
                requestsAdapter!!.submitData(lifecycle,PagingData.empty())
                requestsAdapter!!.submitData(it)
            }
        }
    }

    private fun setUpObserver() {
        mainViewModel.updateProductSuccess.observe(this) {
            if (it.code == 201) {
                showToast(it.responseMessage.toString())
                binding.productName.setText(it.data?.name)
                binding.descriptionTxt.setText(it.data?.description)
//                binding.productName.isEnabled = false
//                binding.descriptionTxt.isEnabled = false
            }
        }
        mainViewModel.deleteProductSuccess.observe(this) {
            if (it.code == 200) {
                finish()
            }
        }
        mainViewModel.listingDetailV2success.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
                productDetails = it!!
                setUpProductDetails(it)
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.reportProductSuccess.observe(this) {
            if (it.code == 201) {
                showToast("Product reported")
            } else {
                showToast(it.responseMessage.toString())
            }
        }
        mainViewModel.errorMessage.observe(this) { /*if (it.isNotBlank()) showToast(it)*/ }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }
    private fun setupList() {
        requestsAdapter = RequestedUsersAdapter(this,this)
        val lm = LinearLayoutManager(this)
        binding.rvRequestedUsers.apply {
            //            layoutManager = LinearLayoutManager(requireContext())
            layoutManager = lm

            adapter = requestsAdapter
        }
    }
    private fun setUpProductDetails(it: ListingResponse) {
//        PROD_CATEGORY = it.product!!.category.toString()

        selectedProdCategory = it.product!!.category.toString()
        PROD_CONDITION = it.product!!.condition.toString()
        PROD_USED_FOR = it.product!!.usedFor.toString()

        val imageList = ArrayList<SlideModel>()
        for (i in it.product?.images!!) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        val data = it.product!!
        binding.imageSlider.setImageList(imageList)

        binding.categoryTxt.text =
            data.category?.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        binding.conditionTxt.text = data.condition
        binding.productName.setText(data.name?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        })
        binding.usedForTxt.text = data.usedFor
        binding.costSavingTxt.text = "Rs ${data.costSaving}"
        binding.textView23.text = "Rs ${data.energySaving}"
        if (data.brand == null || data.brand == "No Brand" || data.brand == "") {
            binding.brandTxt.visibility = View.GONE
            binding.some111.visibility = View.GONE
        } else {
            binding.brandTxt.text = (data.brand.toString())
        }
//        binding.responsesOne.text = (data.postedBy.toString())
//        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.setText(data.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }.toString())
        binding.locationName.setText(data.locationName.toString())

        if (it.product!!.type == "free"){
            binding.priceAmount.visibility = View.GONE
            binding.priceTxt.visibility = View.GONE
        }else{
            binding.priceAmount.visibility = View.VISIBLE
            binding.priceTxt.visibility = View.VISIBLE
            binding.priceAmount.text = it.product!!.price.toString()
        }
        //Alert messages
        when (it.alertMessage.type) {
            Constants.WARNING -> {
                binding.statusLayout.visibility = View.VISIBLE
                binding.statusLayout.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.product_alert_warning
                    )
                )
                binding.statusIcon.background =
                    ContextCompat.getDrawable(this, R.drawable.status_pending_icon)
                binding.statusText.text = it.alertMessage.message.toString()
                binding.statusText.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_pending
                    )
                )
            }

            Constants.SUCCESS -> {
                binding.statusLayout.visibility = View.VISIBLE
                binding.statusLayout.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.product_status_success_color
                    )
                )
                binding.statusIcon.background =
                    ContextCompat.getDrawable(this, R.drawable.status_accepted)
                binding.statusText.text = it.alertMessage.message.toString()
                binding.statusText.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_accepted
                    )
                )
            }

            Constants.DANGER -> {
                binding.statusLayout.visibility = View.VISIBLE
                binding.statusLayout.setCardBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.product_status_danger_color
                    )
                )
                binding.statusIcon.background =
                    ContextCompat.getDrawable(this, R.drawable.status_declined_icon)
                binding.statusText.text = it.alertMessage.message.toString()
                binding.statusText.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.status_declined
                    )
                )
            }

            else -> binding.statusLayout.visibility = View.GONE

        }
        binding.responsesOne.text =
            if (it.total_requests == 1) "${it.total_requests} Response" else "${it.total_requests} Responses"
        binding.responsesTwo.text =
            if (it.total_requests == 1) "${it.total_requests} Response" else "${it.total_requests} Responses"


    }

    private fun loadShareData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Share Product")
        i.putExtra(
            Intent.EXTRA_TEXT,
            "Check out the product I have listed on this great app Abra Ka Dabra where we can share second hand products with others for free: https://play.google.com/store/apps/details?id=com.oss.abraakadabraaapp"
        )
        startActivity(Intent.createChooser(i, "Share"))
    }

    override fun onClick(request: Requests) {
        if (productDetails != null) {
            val intent = Intent(this, RequesterActivity::class.java)
            intent.putExtra(Constants.productId, request.requestId)
            intent.putExtra(Constants.productStatus, productDetails!!.product?.status)
            startActivity(intent)

        }
    }

    private fun showCategoryFilterDialog() {

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        alertName.text = "Categories"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)

        catRecycler.layoutManager = LinearLayoutManager(this)
//        alertAdaper.i = loadData()
//        alertAdaper.alerttype = "category"
//        alertAdaper = alertAdaper
        catRecycler.adapter = mainCatAdapter
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        if (selectedProdCategory == "") PROD_CATEGORY = mainAdapterList[0].id.toString()
        else PROD_CATEGORY = PROD_CATEGORY
        if (selectedProdCategory == "") selectedProdCategory =
            mainAdapterList[0].title?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                .toString()

        binding.categoryTxt.text = selectedProdCategory

//        binding.cateogoryTxt.error = null
        alertDialog.show()
//        binding.categorySelectedTxt.visibility = View.VISIBLE
    }

    private fun showConditionDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        alertName.text = "Condition"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)
        catRecycler.layoutManager = LinearLayoutManager(this)

        // Condition adapter
        catRecycler.adapter = condtionAdapter
        condtionAdapter.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        if (PROD_CONDITION == "") PROD_CONDITION = listConditon[0].name.toString()
        else PROD_CONDITION = PROD_CONDITION

        binding.conditionTxt.text = PROD_CONDITION
//        binding.conditionTxt.error = null
        alertDialog.show()
//        binding.conditionSelectedTxt.visibility = View.VISIBLE
    }

    private fun showUsedForDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        alertName.text = "Used For"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)
        catRecycler.layoutManager = LinearLayoutManager(this)
//        alertAdaper.i = loadUsedForData()
//        alertAdaper.alerttype = "used_for"

        catRecycler.adapter = alertAdaper
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        if (PROD_USED_FOR == "") PROD_USED_FOR = list[0].name.toString()
        else PROD_USED_FOR = PROD_USED_FOR

        binding.usedForTxt.text = PROD_USED_FOR
//        binding.usedForSelectedTxt.visibility = View.VISIBLE

//        binding.usedForTxt.error = null

        alertDialog.show()
    }

    override fun onMainItemClick(position: Int, isSelect: Boolean) {
        PROD_CATEGORY = mainAdapterList[position].id.toString()
        selectedProdCategory = mainAdapterList[position].title?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }.toString()
        binding.categoryTxt.text = selectedProdCategory

        for (i in 0 until mainAdapterList.size) mainAdapterList[i].isSelect = i == position

        mainCatAdapter.notifyDataSetChanged()
        alertDialog.dismiss()
    }

    override fun onConditionItemClick(position: Int, isSelect: Boolean) {
        for (i in 0 until listConditon.size) listConditon[i].isSelect = i == position
        PROD_CONDITION = listConditon[position].name.toString()

        binding.conditionTxt.text = PROD_CONDITION
//        binding.conditionSelectedTxt.visibility = View.VISIBLE

        condtionAdapter.notifyDataSetChanged()
        alertDialog.dismiss()
    }

    override fun onItemClick(position: Int, isSelect: Boolean, alerttype: String) {
//        list.get(position).isSelect = isSelect
        for (i in 0 until list.size) list[i].isSelect = i == position
        PROD_USED_FOR = list[position].name.toString()

        binding.usedForTxt.text = PROD_USED_FOR

        alertAdaper.notifyDataSetChanged()
        alertDialog.dismiss()
    }
}