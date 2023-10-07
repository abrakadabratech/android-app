package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.CropActivity
import com.oss.abraakadabraaapp.activities.newflow.MyListingActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CatMainAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.CategoryDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.ConditionDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.model.AllCategoryResponse
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.adapter.ImageAdapter
import com.oss.abraakadabraaapp.databinding.NewGiverFlowFragmentBinding
import com.oss.abraakadabraaapp.model.ProductImage
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_FROM_CAMERA
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_FROM_GALLERY
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_GOTO_SETTINGS
import com.oss.abraakadabraaapp.utils.Constants.fullAddress
import com.oss.abraakadabraaapp.utils.Constants.latitude
import com.oss.abraakadabraaapp.utils.customView.ImagePickerActivity
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class NewGiverFragment : Fragment(), ImageAdapter.ImageAdapterInterface,
    CategoryDialogAdapter.CategoryDialogAdapterInterface,
CatMainAdapter.MainCategoryAdapterInterface, ConditionDialogAdapter.ConditionAdapterInterface{
    private var PROD_CATEGORY: String = ""
    private var PROD_CONDITION: String = ""
    private var PROD_USED_FOR: String = ""

    private var selectedProdCategory:String = ""
    private var selectedCondition:String = ""
    private var selectedUsedFor:String = ""

    private var LOCATION_NAME: String = ""
    private var lattitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var application: BaseActivity
    private lateinit var placesClient: PlacesClient

    lateinit var userCatData: AllCategoryResponse

    private val mainViewModel: AuthViewModel by viewModel()
    private var photoList = ArrayList<ProductImage>()
    private var serverPhotoList = ArrayList<String>()

    private lateinit var imageAdapter: ImageAdapter
    private var deleteDataString = ""
    var list = arrayListOf<CatData>()
    var listConditon = arrayListOf<CatData>()

    lateinit var userLocation: UserLocation

//    lateinit var catDialogAdapter: CategoryDialogAdapter

    private var _binding: NewGiverFlowFragmentBinding? = null

    private val binding get() = _binding!!

    lateinit var alertDialog: AlertDialog
    lateinit var alertAdaper: CategoryDialogAdapter
    lateinit var condtionAdapter: ConditionDialogAdapter
    lateinit var mainCatAdapter: CatMainAdapter
    lateinit var mainAdapterList:ArrayList<UserCatData>
    lateinit var userInfo : GetUserResponse
    var touchHelper: ItemTouchHelper? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = NewGiverFlowFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)

        if(PreferencesManagement.getUserLocation(requireContext()) != null){
            userLocation = PreferencesManagement.getUserLocation(requireContext())!!
        }else{
            application.getLastLocation()
        }
        val apiKey = BuildConfig.API_KEY

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }

        placesClient = Places.createClient(requireContext())

        try{
            userCatData = PreferencesManagement.getCategories(requireActivity())!!

            if (userCatData.data.size > 0){
                mainAdapterList = userCatData.data
                mainAdapterList[0].isSelect = true
            }
            application.postEvent(Constants.PAGE_GIVER, null)

            imageAdapter = ImageAdapter(photoList, requireContext(), this)

            initUI()
            val userInfo = PreferencesManagement.getUserInfo(requireContext())
            if (userInfo?.data?.status == "not verified"){
                //Show a pop up that is not verified yet
                showNotActivePopUp()
            }else if (userInfo?.data?.status == "pending"){
                showPendingPopUp()
            }
            loadUsedForData()
            loadConditionData()
            setUpObserver()
            clickEvents()

            getUserLocation()

            val callback: ItemTouchHelper.Callback = ItemMoveCallback(imageAdapter)
            touchHelper = ItemTouchHelper(callback)
            touchHelper!!.attachToRecyclerView(binding.rvImages)
        }catch (e:Exception){
            e.printStackTrace()
        }


        return root
    }

    private fun getUserLocation() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())
        binding.locationTxt.text = userLocation?.address
    }

    private fun clickEvents() {


        //Cat adpater
        mainCatAdapter = CatMainAdapter(requireContext(),mainAdapterList,this)

        //Used for adapter
        alertAdaper = CategoryDialogAdapter(requireContext(),list, this,"")

        //Condition adapter
        condtionAdapter = ConditionDialogAdapter(requireContext(), listConditon, this)

        binding.catgoryLinearLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_CATEGORY_SELECT)
            showCategoryFilterDialog()
        }
        binding.conditionLinearLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_CONDITION_OF_PRODUCT_SELECT)
            showConditionDialog()
        }
        binding.usedForLinearLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_USED_FOR_SELECT)
            showUsedForDialog()
        }
        binding.button.setOnClickListener {
            application.postClick(Constants.BUTTON_SUBMIT)
            val userInfo = PreferencesManagement.getUserInfo(requireContext())
            if (userInfo?.data?.status != "active"){
                //Show a pop up that is not verified yet
                showNotActivePopUp()
            }else{
                if (binding.iAgreeCheckbox.isChecked) {
                    if (isValidate()) {
                        //showSubmitCautionDialog()
                        if (userInfo.data?.status == "active"){
                            postNewProduct()
                        }else{
                            showToast("Your profile not verified yet.")
                        }
                    }
                } else {
                    showToast("Please select I Agree to continue")
                }
            }
        }

        binding.linearLayout1.setOnClickListener {
            locationPicker()
        }
        Utility.deleteRecursive(application.cacheDir)
    }

    private fun locationPicker() {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("IN")
            .build(requireActivity())
        locationLauncher.launch(intent)
    }

    private var locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK)
                if (result != null) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)

                        lattitude = place.latLng!!.latitude
                        longitude = place.latLng!!.longitude

                        fullAddress = if (place.address != null) {
                            place.address!!
                        } else {
                            "TODO geo api required"
                        }
                        Log.d("MYT", "${place.latLng!!.latitude} : latitude $lattitude")
                        Log.d("MYT", "longitude $longitude")
                        Log.d("MYT", "fullAddress $fullAddress")
                        binding.locationTxt.setText(fullAddress)
                    }
                }
        }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUI() {
        with(binding) {

            photoList.add(ProductImage(null, -2, ""))

            binding.rvImages.apply {
                isNestedScrollingEnabled = false
                layoutManager = GridLayoutManager(context, 3)
                setHasFixedSize(true)
                recycledViewPool.setMaxRecycledViews(1, 0)
                adapter = imageAdapter
            }

            mainImagePlaceholder2.visibility = View.GONE
            arrowImage2.visibility = View.GONE

        }


    }

    private fun showNotActivePopUp() {
        EventBus.getDefault().post("popup")
    }

    private fun showPendingPopUp() {
        var alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Alert!")
        alertDialog.setMessage("Your profile is pending for verification please wait till it's get verified, thank you.")
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Ok") { dialog, id ->
            //cancel the request
            EventBus.getDefault().post("clear")
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onItemRemove(position: Int, data: ProductImage) {
        if (data.id != -1) {
            deleteDataString = if (deleteDataString.isEmpty()) {
                data.id.toString()
            } else {
                deleteDataString + "," + data.id.toString()
            }
            Log.d("MYT", "id ${data.id}")
            Log.d("MYT", "deleteDataString $deleteDataString")
        }
        photoList.removeAt(position)
        if (photoList.size == 1){
            binding.mainImagePlaceholder2.visibility = View.GONE
            binding.arrowImage2.visibility = View.GONE
        }else{
            binding.mainImagePlaceholder2.visibility = View.VISIBLE
            binding.arrowImage2.visibility = View.VISIBLE
        }
        imageAdapter.notifyDataSetChanged()
    }

    override fun addProductImage() {
        if (photoList.size < 5) {
            selectImage()
        } else {
            showToast(
                "Select only 4 Images",
            )
        }
    }

    override fun sorted(list: ArrayList<ProductImage>) {
        Log.e(TAG, "Before sorted: ${Gson().toJson(photoList)}")
        Log.e(TAG, "After sorted: ${Gson().toJson(list)}")
        photoList = list
        imageAdapter.notifyDataSetChanged()
    }

    fun showToast(message: String) {
        if (message.isNotBlank()) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun selectImage() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(context)
                .withPermissions(Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            bannerOptions()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }else{
            Dexter.withContext(context)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            bannerOptions()
                        }
                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
        application.postEvent(Constants.BUTTON_UPLOAD_IMAGE, null)

    }

    private fun bannerOptions() {
        ImagePickerActivity.showImagePickerOptions(
            requireContext(),
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    application.postClick(BUTTON_FROM_CAMERA)
                    bannerCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    application.postClick(BUTTON_FROM_GALLERY)
                    openYourActivity()
                }
            })
    }

    fun showSettingsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
            application.postClick(BUTTON_GOTO_SETTINGS)
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //TODO
            }
        }

    private var businessProofImageActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                var uriList = data!!.getParcelableArrayListExtra<Uri>("imagesList") as ArrayList<Uri>
                for(uri in uriList){
                    photoList.add(ProductImage(uri, -1, ""))
                }
                if (photoList.size == 1){
                    binding.mainImagePlaceholder2.visibility = View.GONE
                    binding.arrowImage2.visibility = View.GONE
                }else{
                    binding.mainImagePlaceholder2.visibility = View.VISIBLE
                    binding.arrowImage2.visibility = View.VISIBLE
                }
                imageAdapter.notifyDataSetChanged()
            }
        }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                if (data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    if (count > 11 - photoList.size) {
                        for (i in 0 until 10 - (photoList.size - 1)) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            photoList.add(ProductImage(imageUri, -1, ""))
                        }
                    } else {
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            if (photoList.size != 11) {
                                photoList.add(ProductImage(imageUri, -1, ""))
                            }
                        }
                    }
                } else {
                    //for single image
                    val imageUri = data.data!!
                    photoList.add(ProductImage(imageUri, -1, ""))
                }
                imageAdapter.notifyDataSetChanged()
            }
        }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        resultLauncher.launch(intent)
    }

    private fun bannerCameraIntent() {
        val intent = Intent(context, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        businessProofImageActivity.launch(intent)

    }

    private fun openYourActivity() {
/*
        val intent = Intent(requireContext(), ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_GALLERY_IMAGE
        )

//        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
//        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
//        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        businessProofImageActivity.launch(intent)
*/
        val intent = Intent(requireContext(), CropActivity::class.java)
        intent.putExtra("COUNT_IMAGES",photoList.size)
        businessProofImageActivity.launch(intent)
    }

    private fun showCategoryFilterDialog() {

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        alertName.text = "Categories"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)

        catRecycler.layoutManager = LinearLayoutManager(context)
//        alertAdaper.i = loadData()
//        alertAdaper.alerttype = "category"
//        alertAdaper = alertAdaper
        catRecycler.adapter = mainCatAdapter
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
//        if (selectedProdCategory == "") selectedProdCategory = mainAdapterList[0].title?.capitalize().toString()
//        if (PROD_CATEGORY == "") PROD_CATEGORY = mainAdapterList[0].id.toString()

//        binding.categorySelectedTxt.text = selectedProdCategory
//        PROD_CATEGORY = mainAdapterList[0].id.toString()
        binding.cateogoryTxt.error = null
        alertDialog.show()
//        binding.categorySelectedTxt.visibility = View.VISIBLE
    }

    private fun showConditionDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        alertName.text = "Condition"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)
        catRecycler.layoutManager = LinearLayoutManager(context)

        // Condition adapter
        catRecycler.adapter = condtionAdapter
        condtionAdapter.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
//        if(PROD_CONDITION == "") PROD_CONDITION = listConditon[0].name.toString()
//        else PROD_CONDITION = PROD_CONDITION
//
//        binding.conditionSelectedTxt.text = PROD_CONDITION
//        binding.conditionTxt.error = null
        alertDialog.show()
//        binding.conditionSelectedTxt.visibility = View.VISIBLE
    }

    private fun showUsedForDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        alertName.text = "Used For"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)
        catRecycler.layoutManager = LinearLayoutManager(context)
//        alertAdaper.i = loadUsedForData()
//        alertAdaper.alerttype = "used_for"

        catRecycler.adapter = alertAdaper
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
//        if(PROD_USED_FOR == "") PROD_USED_FOR = list[0].name.toString()
//
//        binding.usedForSelectedTxt.text = PROD_USED_FOR
//        binding.usedForSelectedTxt.visibility = View.VISIBLE
//
//        binding.usedForTxt.error = null

        alertDialog.show()
    }

    private fun showSubmitCautionDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_submit_caution_dialog, null)
        dialogBuilder.setView(dialogView)

        val success_ok_btn = dialogView.findViewById<TextView>(R.id.success_ok_btn)
        val success_ok_btn1 = dialogView.findViewById<TextView>(R.id.success_ok_btn1)
        val cautionDialog = dialogView.findViewById<CardView>(R.id.cautionDialog)
        val successDialog = dialogView.findViewById<CardView>(R.id.successDialog)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        success_ok_btn.setOnClickListener {
            application.postClick(Constants.BUTTON_I_ACCEPT)

            postNewProduct()

        }
        success_ok_btn1.setOnClickListener {
            application.postClick(Constants.BUTTON_OK_GOT_IT)
            showToast("Under Development")
            alertDialog.dismiss()
            //showSubmitSuccessDialog()
        }
        closeBtn.setOnClickListener { alertDialog.dismiss() }


        alertDialog.show()
    }

    private fun postNewProduct() {
        if (isValidate()) {
            addImage()
            if (application.isNetworkAvailable()) {
                application.generateAuthToken()

                Log.d("ok", "postNewProduct: $lattitude")
                Log.d("ok", "postNewProduct: $longitude")
                if (userLocation != null){
                    val map = HashMap<String, RequestBody>()
                    map["name"] = JavaUtils.toRequestBody(binding.etProductName.text.toString().trim())
                    map["category"] = JavaUtils.toRequestBody(PROD_CATEGORY)
                    map["description"] = JavaUtils.toRequestBody(binding.descEdt.text.toString().trim())
                    map["condition"] = JavaUtils.toRequestBody(PROD_CONDITION)
                    map["used_for"] = JavaUtils.toRequestBody(PROD_USED_FOR)
                    map["location_name"] = JavaUtils.toRequestBody(binding.locationTxt.text.toString())
                    map["latitude"] = JavaUtils.toRequestBody(if(lattitude == 0.0) userLocation.lat else lattitude.toString())
                    map["longitude"] = JavaUtils.toRequestBody(if(longitude == 0.0) userLocation.long else longitude.toString())
                    Log.d("MYT", "latitude $latitude")
                    Log.d("MYT", "longitude $longitude")
                    Log.d("MYT", "fullAddress $fullAddress")
                    map["price"] = JavaUtils.toRequestBody("0")
                    map["brand"] = JavaUtils.toRequestBody(binding.etProductBrand.text.toString().trim())

                    manageProduct(map)
                }else{
                    showToast("Please turn on your location")
                }
            }
        }
    }

    private fun addImage() {
        serverPhotoList.clear()
        for (item in photoList) {
            if (item.uri != null) {
                if (item.id != -2) {
                    serverPhotoList.add(item.uri.toString())
                }
            }
        }
    }

    private fun setUpObserver() {
        val activity: Activity? = activity
        if (activity != null) {
            mainViewModel.postProductSuccess.observe(requireActivity()) {
                it.responseMessage?.let { it1 ->
                    showToast(it1)
                    alertDialog.dismiss()
                    clearAll()
                    EventBus.getDefault().post("clear")
//                requireActivity().finish()
                    val intent = Intent(context, MyListingActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

            }
            mainViewModel.isLoading.observe(requireActivity(), { application.loader(it) })

            mainViewModel.errorMessage.observe(
                requireActivity(),
                { if (it.isNotBlank()) showToast(it) })
        }

    }

    private fun clearAll() {
        with(binding){
            etProductName.setText("")
            descEdt.setText("")
            etProductBrand.setText("")
            serverPhotoList.clear()
            iAgreeCheckbox.isChecked = false
        }
    }

    private fun manageProduct(body: Map<String, RequestBody>) {
        lifecycleScope.launch {

            val imagePathList = ArrayList<String>()

            serverPhotoList.forEachIndexed { index, it ->
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        requireContext().contentResolver,
                        Uri.parse(it)
                    )
                } else {
                    val source =
                        ImageDecoder.createSource(requireContext().contentResolver, Uri.parse(it))
                    ImageDecoder.decodeBitmap(source)
                }

                val imageFile = ImageUtils.bitmapToFile(
                    bitmap,
                    requireContext(),
                    "product_name$index.jpg"
                )
                val compressedImage = async {
                    Compressor.compress(requireContext(), imageFile)
                }

                imagePathList.add(compressedImage.await().path)
            }

            val map = HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(requireContext())!!
            map[RequestKeys.authorization] = token

            Log.d(
                API_TAG,
                "manageProduct: ${
                    JavaUtils.prepareFilePart(
                        imagePathList,
                        RequestKeys.productImages
                    )
                }"
            )
            mainViewModel.postProduct(
                map,
                body,
                JavaUtils.prepareFilePart(imagePathList, RequestKeys.productImages)
            )
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



    private fun isValidate(): Boolean {
        with(binding) {

          /*  if (etProductName.text!!.toString().trim().isNotBlank()) {
                textView6.isErrorEnabled = false
            }*/
           /* if (PROD_CONDITION == ""){
                showToast("Please select a condition of product")
                conditionTxt.error = "Please select a condition of product"
            }
*/
            return when {
                etProductName.text!!.toString().trim().isBlank() -> {
                    application.showToast("Please Enter Product Name")
                    false
                }
                PROD_CATEGORY == "" -> {
//                    cateogoryTxt.error = "Please select category of product"
                    showToast("Please select category of product!")
                    false
                }
                PROD_CONDITION == "" -> {
//                    conditionTxt.error = "Please Select condition of product"
                    showToast("Please select condition of product!")
                    false
                }
                PROD_USED_FOR == "" -> {
//                    usedForTxt.error = "Please select used for"
                    showToast("Please select used for!")
                    false
                }
                etProductBrand1.text.toString() == "" -> {
//                    etProductBrand1.error = "Please select used for"
                    showToast("Please price of the product!")
                    false
                }
                descEdt.text.toString() == "" -> {
//                    etProductBrand1.error = "Please select used for"
                    showToast("Please enter decription!")
                    false
                }

                /*genderSpinner.selectedItem.toString() == resources.getString(R.string.select_gender) -> {
                    showToast("Please Select Gender")
                    false
                }
                categorySpinner.selectedItem.toString() == resources.getString(R.string.select_category) -> {
                    showToast("Please Select Category")
                    false
                }
                etBrand.text!!.toString().trim().isBlank() -> {
                    textInputBrand.error = "Please Enter Brand Name"
                    false
                }
                productAgeSpinner.selectedItem.toString() == resources.getString(R.string.select_age) -> {
                    showToast("Please Select Age")
                    false
                }
                productConditionSpinner.selectedItem.toString() == resources.getString(R.string.select_condition) -> {
                    showToast("Please Select Condition")
                    false
                }
                etLocation.text!!.toString().trim().isBlank() -> {
                    textInputLocation.error = "Please Enter Product Location"
                    false
                }
                etDescription.text!!.toString().trim().isBlank() -> {
                    textInputDescription.error = "Please Enter Product Description"
                    false
                }*/

                descEdt.text!!.toString().trim().length > 300 -> {
//                    descEdt.error =
                        application.showToast("Please Enter Product Description less than 300 characters")
                    false
                }
                photoList.size <= 2 -> {
                    showToast("Please add at least 2 images")
                    false
                }
                photoList.size > 5 -> {
                    showToast("Maximum photo are 4.Please remove some")
                    false
                }
                else -> true
            }
        }
    }

    override fun onMainItemClick(position: Int, isSelect: Boolean) {
        PROD_CATEGORY = mainAdapterList[position].id.toString()
        selectedProdCategory = mainAdapterList[position].title?.capitalize().toString()

        binding.categorySelectedTxt.text = selectedProdCategory
        binding.categorySelectedTxt.visibility = View.VISIBLE

        for (i in 0 until mainAdapterList.size) mainAdapterList[i].isSelect = i == position

        mainCatAdapter.notifyDataSetChanged()
        alertDialog.dismiss()
    }

    override fun onConditionItemClick(position: Int, isSelect: Boolean) {
        for (i in 0 until listConditon.size) listConditon[i].isSelect = i == position
        PROD_CONDITION = listConditon[position].name.toString()

        binding.conditionSelectedTxt.text = listConditon[position].name
        binding.conditionSelectedTxt.visibility = View.VISIBLE

        condtionAdapter.notifyDataSetChanged()
        alertDialog.dismiss()
    }
    override fun onItemClick(position: Int, isSelect: Boolean,alerttype:String) {
//        list.get(position).isSelect = isSelect
        for (i in 0 until list.size) list[i].isSelect = i == position
        PROD_USED_FOR = list[position].name.toString()

        binding.usedForSelectedTxt.text = PROD_USED_FOR
        binding.usedForSelectedTxt.visibility = View.VISIBLE

        alertAdaper.notifyDataSetChanged()
        alertDialog.dismiss()
    }

}