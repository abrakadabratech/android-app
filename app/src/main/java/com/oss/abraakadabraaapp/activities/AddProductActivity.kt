package com.oss.abraakadabraaapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.ImageAdapter
import com.oss.abraakadabraaapp.databinding.ActivityAddProductBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.model.EditProductData
import com.oss.abraakadabraaapp.model.ProductImage
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.customView.ImagePickerActivity
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import id.zelory.compressor.Compressor
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.*


class AddProductActivity : BaseActivity(),
    ImageAdapter.ImageAdapterInterface {

    //New Line added. check

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private var photoList = ArrayList<ProductImage>()
    private val imageAdapter = ImageAdapter(photoList, this, this)

    private val categoryList = ArrayList<CategoryData>()
    private val genderList = ArrayList<String>()
    private val productAgeList = ArrayList<String>()
    private val productConditionList = ArrayList<String>()

    private val categoryAdapter: ArrayAdapter<CategoryData> by lazy {
        ArrayAdapter(this, R.layout.item_spinner, categoryList)
    }

    private val genderAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(this, R.layout.item_spinner, genderList)
    }

    private val productAgeAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(this, R.layout.item_spinner, productAgeList)
    }

    private val productConditionAdapter: ArrayAdapter<String> by lazy {
        ArrayAdapter(this, R.layout.item_spinner, productConditionList)
    }

    private lateinit var placesClient: PlacesClient

    private lateinit var latitude: String
    private lateinit var longitude: String
    private lateinit var fullAddress: String

    private val mainViewModel: MainViewModel by viewModel()

    private lateinit var editProductData: EditProductData

    private var deleteDataString = ""
    private var serverPhotoList = ArrayList<String>()

    companion object {
        fun createIntent(context: Context, editProductData: EditProductData?): Intent {
            val intent = Intent(context, AddProductActivity::class.java)
            if (editProductData != null) {
                intent.putExtra(Constants.editProduct, editProductData)
            }
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        setSupportActionBar(includeToolbar.toolbar)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        if (!Places.isInitialized()) {
            Places.initialize(this, resources.getString(R.string.akd))
        }

        placesClient = Places.createClient(this)

        if (intent.hasExtra(Constants.editProduct)) {
            editProductData = intent.getParcelableExtra(Constants.editProduct)!!
            includeToolbar.tvToolbarTitle.text =
                applicationContext.resources.getString(R.string.edit_product)
        } else {
            includeToolbar.tvToolbarTitle.text =
                applicationContext.resources.getString(R.string.add_product)
        }

        initUI()
        setUpObserver()
        getCategory()

    }

    private fun setUpObserver() {

        mainViewModel.manageProductSuccess.observe(this, {
            showToast(it.responseMessage)
            val intent = Intent()
            intent.putExtra(Constants.success, Constants.success)
            setResult(RESULT_OK, intent)
            finish()
        })

        mainViewModel.getCategorySuccess.observe(this, {
            val data = it.data
            if (data.isNotEmpty()) {

                categoryList.add(
                    CategoryData(
                        "0",
                        resources.getString(R.string.select_category),
                        0,
                        "",
                        0,
                        ""
                    )
                )

                categoryList.addAll(data)
                categoryAdapter.notifyDataSetChanged()

                binding.categoryView.visibility = View.VISIBLE
                binding.llCategorySelection.visibility = View.VISIBLE

                if (intent.hasExtra(Constants.editProduct)) {
                    setData()
                }
            }
        })

        mainViewModel.isLoading.observe(this, { loader(it) })

        mainViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })
    }

    private fun setData() {
        with(binding) {
            var count = 0
            categoryList.forEach {
                if (it.id == editProductData.categoryId.toInt()) {
                    categorySpinner.setSelection(count)
                }
                count++
            }
            count = 0
            genderList.forEach {
                if (it == editProductData.gender) {
                    genderSpinner.setSelection(count)
                }
                count++
            }

            count = 0

            productAgeList.forEach {
                if (it == editProductData.productAge) {
                    productAgeSpinner.setSelection(count)
                }
                count++
            }

            count = 0

            productConditionList.forEach {
                if (it == editProductData.productCondition) {
                    productConditionSpinner.setSelection(count)
                }
                count++
            }

            categoryAdapter.notifyDataSetChanged()
            genderAdapter.notifyDataSetChanged()
            productAgeAdapter.notifyDataSetChanged()
            productConditionAdapter.notifyDataSetChanged()

            etProductName.setText(editProductData.productName)
            etBrand.setText(editProductData.brand)
            etDescription.setText(editProductData.productDescription)

            etLocation.setText(editProductData.productLocation)
            fullAddress = editProductData.productLocation
            latitude = editProductData.lat
            longitude = editProductData.lng

            if (editProductData.productImages.isNotEmpty()) {
                for (item in editProductData.productImages) {
                    photoList.add(ProductImage(null, item.id, item.image))
                }
                imageAdapter.notifyDataSetChanged()
            }

        }


    }

    private fun getCategory() {
        if (isNetworkAvailable()) {
            mainViewModel.getCategory(Utility.getHeaders(this@AddProductActivity))
        } else {
            showSnackBar(
                binding.clAddProduct,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUI() {
        with(binding) {
            etProductName.filterEmoji()
            etDescription.filterEmojiWithCharacterLimit(300)

            etDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (etDescription.text.toString().isNotBlank()) {
                        textInputDescription.hint = "${resources.getString(R.string.description)} ${
                            etDescription.text.toString().trim().length
                        }/300"
                        if (etDescription.text?.length == 300) {
                            textInputDescription.error =
                                "Description max length reached (300 characters)"
                            textInputDescription.errorIconDrawable = null
                        } else {
                            textInputDescription.isErrorEnabled = false
                        }
                    } else {
                        textInputDescription.hint = resources.getString(R.string.description)
                    }
                }
            })

            if (PreferencesManagement.getUserLocation(this@AddProductActivity) != null) {
                val userLocation = PreferencesManagement.getUserLocation(this@AddProductActivity)!!
                latitude = userLocation.lat
                longitude = userLocation.long
                fullAddress = userLocation.address ?: ""
                etLocation.setText(fullAddress)
            }

            photoList.add(ProductImage(null, -2, ""))

            binding.rvImages.apply {
                isNestedScrollingEnabled = false
                layoutManager = GridLayoutManager(this@AddProductActivity, 3)
                setHasFixedSize(true)
                recycledViewPool.setMaxRecycledViews(1, 0)
                adapter = imageAdapter
            }

            genderList.addAll(resources.getStringArray(R.array.gender_value))
            productAgeList.addAll(resources.getStringArray(R.array.age_value))
            productConditionList.addAll(resources.getStringArray(R.array.condition_value))

            genderAdapter.setDropDownViewResource(R.layout.item_spinner)
            genderSpinner.adapter = genderAdapter

            categoryAdapter.setDropDownViewResource(R.layout.item_spinner)
            categorySpinner.adapter = categoryAdapter

            productAgeAdapter.setDropDownViewResource(R.layout.item_spinner)
            productAgeSpinner.adapter = productAgeAdapter

            productConditionAdapter.setDropDownViewResource(R.layout.item_spinner)
            productConditionSpinner.adapter = productConditionAdapter

            genderSpinner.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }

            productAgeSpinner.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }
            productConditionSpinner.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }
            categorySpinner.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }

            genderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    if (genderSpinner.findViewById<TextView>(R.id.spinner_text) != null) {
                        val tv: TextView = genderSpinner.findViewById(R.id.spinner_text)
                        val selectedGender = genderSpinner.selectedItem.toString()
                        if (selectedGender == resources.getString(R.string.select_gender)) {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.hint_color
                                )
                            )
                        } else {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.black
                                )
                            )
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            productAgeSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    hideSoftKeyboard()
                    if (productAgeSpinner.findViewById<TextView>(R.id.spinner_text) != null) {
                        val tv: TextView = productAgeSpinner.findViewById(R.id.spinner_text)
                        val selectedGender = productAgeSpinner.selectedItem.toString()
                        if (selectedGender == resources.getString(R.string.select_age)) {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.hint_color
                                )
                            )
                        } else {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.black
                                )
                            )
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            productConditionSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    hideSoftKeyboard()

                    if (productConditionSpinner.findViewById<TextView>(R.id.spinner_text) != null) {
                        val tv: TextView = productConditionSpinner.findViewById(R.id.spinner_text)

                        val selectedGender = productConditionSpinner.selectedItem.toString()
                        if (selectedGender == resources.getString(R.string.select_condition)) {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.hint_color
                                )
                            )
                        } else {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.black
                                )
                            )
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            categorySpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    hideSoftKeyboard()
                    if (categorySpinner.findViewById<TextView>(R.id.spinner_text) != null) {
                        val tv: TextView = categorySpinner.findViewById(R.id.spinner_text)
                        val selectedState = categorySpinner.selectedItem as CategoryData
                        if (selectedState.id == 0) {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.hint_color
                                )
                            )
                        } else {
                            tv.setTextColor(
                                ContextCompat.getColor(
                                    this@AddProductActivity,
                                    R.color.black
                                )
                            )
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            ivGender.setOnClickListener {
                hideSoftKeyboard()
                genderSpinner.performClick()
            }

            ivCategory.setOnClickListener {
                hideSoftKeyboard()
                categorySpinner.performClick()
            }

            ivProductAge.setOnClickListener {
                hideSoftKeyboard()
                productAgeSpinner.performClick()
            }

            ivProductCondition.setOnClickListener {
                hideSoftKeyboard()
                productConditionSpinner.performClick()
            }

            etLocation.setOnClickListener {
                hideSoftKeyboard()
                locationPicker()
            }

            saveBtn.setOnClickListener {
                if (isValidate()) {
                    addImage()
                    if (isNetworkAvailable()) {

                        saveBtn.isEnabled = false
                        saveBtn.isClickable = false
                        saveBtn.isFocusable = false

                        loader(true)

                        val map = HashMap<String, RequestBody>()

                        val selectedCategory = categorySpinner.selectedItem as CategoryData

                        map[RequestKeys.userId] = JavaUtils.toRequestBody(userData.id.toString())
                        map[RequestKeys.title] =
                            JavaUtils.toRequestBody(etProductName.text.toString().trim())
                        map[RequestKeys.genderFor] =
                            JavaUtils.toRequestBody(genderSpinner.selectedItem.toString())
                        map[RequestKeys.categoryId] =
                            JavaUtils.toRequestBody(selectedCategory.id.toString())
                        map[RequestKeys.brand] =
                            JavaUtils.toRequestBody(etBrand.text.toString().trim())
                        map[RequestKeys.fullAddress] = JavaUtils.toRequestBody(fullAddress)
                        map[RequestKeys.lat] = JavaUtils.toRequestBody(latitude)
                        map[RequestKeys.lng] = JavaUtils.toRequestBody(longitude)
                        map[RequestKeys.quality] =
                            JavaUtils.toRequestBody(productConditionSpinner.selectedItem.toString())
                        map[RequestKeys.productAge] =
                            JavaUtils.toRequestBody(productAgeSpinner.selectedItem.toString())
                        map[RequestKeys.description] =
                            JavaUtils.toRequestBody(etDescription.text.toString().trim())

                        map[RequestKeys.productId] = if (intent.hasExtra(Constants.editProduct)) {
                            JavaUtils.toRequestBody(editProductData.productId)
                        } else {
                            JavaUtils.toRequestBody("")
                        }

                        map[RequestKeys.productImagesIds] =
                            JavaUtils.toRequestBody(deleteDataString)

                        manageProduct(map)

                    } else {
                        showSnackBar(
                            binding.clAddProduct,
                            applicationContext.resources.getString(R.string.no_internet_connection_found)
                        )
                    }
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

    private fun manageProduct(map: HashMap<String, RequestBody>) {
        lifecycleScope.launch {

            val imagePathList = ArrayList<String>()

            serverPhotoList.forEachIndexed { index, it ->
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        Uri.parse(it)
                    )
                } else {
                    val source = ImageDecoder.createSource(contentResolver, Uri.parse(it))
                    ImageDecoder.decodeBitmap(source)
                }

                val imageFile = ImageUtils.bitmapToFile(
                    bitmap,
                    this@AddProductActivity,
                    "product_name$index.jpg"
                )
                val compressedImage = async {
                    Compressor.compress(this@AddProductActivity, imageFile)
                }

                imagePathList.add(compressedImage.await().path)
            }

            mainViewModel.manageProduct(
                Utility.getHeaders(this@AddProductActivity),
                map,
                JavaUtils.prepareFilePart(imagePathList, RequestKeys.productImages)
            )


        }
    }

    private fun locationPicker() {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("IN")
            .build(this@AddProductActivity)
        locationLauncher.launch(intent)
    }

    private var locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK)
                if (result != null) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)

                        latitude = place.latLng!!.latitude.toString()
                        longitude = place.latLng!!.longitude.toString()
                        fullAddress = if (place.address != null) {
                            place.address!!
                        } else {
                            "TODO geo api required"
                        }

                        Log.d("MYT", "latitude $latitude")
                        Log.d("MYT", "longitude $longitude")
                        Log.d("MYT", "fullAddress $fullAddress")

                        binding.etLocation.setText(fullAddress)
                    }
                }
        }

    private fun selectImage() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    private fun bannerOptions() {
        ImagePickerActivity.showImagePickerOptions(
            this,
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    bannerCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    openYourActivity()
                }
            })
    }

    private fun openYourActivity() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(intent)
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

    private fun bannerCameraIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        businessProofImageActivity.launch(intent)

    }

    private var businessProofImageActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")!!
                photoList.add(ProductImage(uri, -1, ""))
                imageAdapter.notifyDataSetChanged()
            }
        }

    override fun addProductImage() {
        if (photoList.size < 11) {
            selectImage()
        } else {
            showToast(
                "Select only 10 Images",
            )
        }
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
        imageAdapter.notifyDataSetChanged()
    }

    private fun isValidate(): Boolean {
        with(binding) {

            if (etProductName.text!!.toString().trim().isNotBlank()) {
                textInputProductName.isErrorEnabled = false
            }
            if (etBrand.text!!.toString().trim().isNotBlank()) {
                textInputBrand.isErrorEnabled = false
            }
            if (etLocation.text!!.toString().trim().isNotBlank()) {
                textInputLocation.isErrorEnabled = false
            }
            if (etDescription.text!!.toString().trim().isNotBlank()) {
                textInputDescription.isErrorEnabled = false
            }
            return when {
                etProductName.text!!.toString().trim().isBlank() -> {
                    textInputProductName.error = "Please Enter Product Name"
                    false
                }
                genderSpinner.selectedItem.toString() == resources.getString(R.string.select_gender) -> {
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
                }
                etDescription.text!!.toString().trim().length > 300 -> {
                    textInputDescription.error =
                        "Please Enter Product Description less than 300 characters"
                    false
                }
                photoList.size < 2 -> {
                    showToast("Please add at least 1 image")
                    false
                }
                else -> true
            }
        }
    }
}