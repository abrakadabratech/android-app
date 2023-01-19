package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyListingActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CategoryDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import com.oss.abraakadabraaapp.adapter.ImageAdapter
import com.oss.abraakadabraaapp.databinding.NewGiverFlowFragmentBinding
import com.oss.abraakadabraaapp.datasource.ProductsAdapter
import com.oss.abraakadabraaapp.datasource.ProductsViewModel
import com.oss.abraakadabraaapp.datasource.ProductsViewModelFactory
import com.oss.abraakadabraaapp.model.ProductImage
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.JavaUtils.prepareFilePartSingle
import com.oss.abraakadabraaapp.utils.customView.ImagePickerActivity
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class NewGiverFragment : Fragment(), ImageAdapter.ImageAdapterInterface,
    CategoryDialogAdapter.CategoryDialogAdapterInterface {
    private var PROD_CATEGORY: String = "Electronics"
    private var PROD_CONDITION: String = "Almost New"
    private var PROD_USED_FOR: String = "Less Than 6 Months"
    private var LOCATION_NAME: String = ""
    private var lattitude: Double = 0.0
    private var longitude: Double = 0.0
    lateinit var application: BaseActivity

    private val mainViewModel: AuthViewModel by viewModel()
    private var photoList = ArrayList<ProductImage>()
    private var serverPhotoList = ArrayList<String>()

    private lateinit var imageAdapter: ImageAdapter
    private var deleteDataString = ""
    var list = arrayListOf<CatData>()

    lateinit var userLocation: UserLocation

//    lateinit var catDialogAdapter: CategoryDialogAdapter

    private var _binding: NewGiverFlowFragmentBinding? = null

    private val binding get() = _binding!!

    lateinit var alertDialog: AlertDialog
    lateinit var alertAdaper: CategoryDialogAdapter



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

        userLocation = PreferencesManagement.getUserLocation(requireContext())!!

        application.postEvent(Constants.PAGE_GIVER, null)

        imageAdapter = ImageAdapter(photoList, requireContext(), this)

        initUI()
        setUpObserver()


        clickEvents()

        getUserLocation()

        return root
    }

    private fun getUserLocation() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())
        binding.locationTxt.text = userLocation?.address
    }

    private fun clickEvents() {
        alertAdaper = CategoryDialogAdapter(requireContext(), arrayListOf(), this,"")
        binding.catgoryLinearLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_CATEGORY_SELECT, null)
            showCategoryFilterDialog()
        }
        binding.conditionLinearLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_CONDITION_OF_PRODUCT_SELECT, null)
            showConditionDialog()
        }
        binding.usedForLinearLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_USED_FOR_SELECT, null)
            showUsedForDialog()
        }
        binding.button.setOnClickListener {
            application.postEvent(Constants.BUTTON_SUBMIT, null)
            if (binding.iAgreeCheckbox.isChecked) {
                if (isValidate()) {
                    showSubmitCautionDialog()
                }
            } else {
                showToast("Please select I Agree to continue")
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

    override fun addProductImage() {
        if (photoList.size < 11) {
            selectImage()
        } else {
            showToast(
                "Select only 10 Images",
            )
        }
    }

    fun showToast(message: String) {
        if (message.isNotBlank()) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun selectImage() {
        application.postEvent(Constants.BUTTON_UPLOAD_IMAGE, null)
        Dexter.withContext(context)
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
            requireContext(),
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    bannerCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    openYourActivity()
                }
            })
    }

    fun showSettingsDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
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
                val uri = data!!.getParcelableExtra<Uri>("path")!!
                photoList.add(ProductImage(uri, -1, ""))
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
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(intent)
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
        alertAdaper.i = loadData()
        alertAdaper.alerttype = "category"
        alertAdaper = alertAdaper
        catRecycler.adapter = alertAdaper
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
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
        alertAdaper.i = loadConditionData()
        alertAdaper.alerttype = "condition"
        catRecycler.adapter = alertAdaper
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
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
        alertAdaper.i = loadUsedForData()
        alertAdaper.alerttype = "used_for"

        catRecycler.adapter = alertAdaper
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }

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
            application.postEvent(Constants.BUTTON_I_ACCEPT, null)

            postNewProduct()

        }
        success_ok_btn1.setOnClickListener {
            application.postEvent(Constants.BUTTON_OK_GOT_IT, null)
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
                val map = HashMap<String, RequestBody>()
                map["name"] = JavaUtils.toRequestBody(binding.etProductName.text.toString().trim())
                map["category"] = JavaUtils.toRequestBody(PROD_CATEGORY)
                map["description"] = JavaUtils.toRequestBody(binding.descEdt.text.toString().trim())
                map["condition"] = JavaUtils.toRequestBody(PROD_CONDITION)
                map["used_for"] = JavaUtils.toRequestBody(PROD_USED_FOR)
                map["location_name"] = JavaUtils.toRequestBody(userLocation.address)
                map["latitude"] = JavaUtils.toRequestBody(userLocation.lat)
                map["longitude"] = JavaUtils.toRequestBody(userLocation.long)

                manageProduct(map)
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

        mainViewModel.postProductSuccess.observe(requireActivity()) {
            it.responseMessage?.let { it1 ->
                showToast(it1)
                alertDialog.dismiss()
                clearAll()
                startActivity(Intent(context, MyListingActivity::class.java))
            }

        }
        mainViewModel.isLoading.observe(requireActivity(), { application.loader(it) })

        mainViewModel.errorMessage.observe(
            requireActivity(),
            { if (it.isNotBlank()) showToast(it) })
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
    private fun showSubmitSuccessDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_submit_success_dialog, null)
        dialogBuilder.setView(dialogView)

        val success_ok_btn = dialogView.findViewById<TextView>(R.id.success_ok_btn)
        success_ok_btn.setOnClickListener { showToast("Under Development") }

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

    private fun loadData(): ArrayList<CatData> {
        list.clear()
        list.add(CatData("Electronics", true))
        list.add(CatData("Clothing", false))
        list.add(CatData("Book", false))
        list.add(CatData("Sports", false))
        list.add(CatData("Footwear", false))
        list.add(CatData("Fashion", false))
        list.add(CatData("Furniture", false))
        list.add(CatData("Home Decor", false))
        list.add(CatData("Kitchen & Appliance", false))
        list.add(CatData("Others", false))

        return list
    }

    private fun loadConditionData(): ArrayList<CatData> {
        list.clear()
        list.add(CatData("Almost New", true))
        list.add(CatData("Good", false))
        list.add(CatData("Average", false))
        list.add(CatData("Needs Repair", false))

        return list
    }

    private fun loadUsedForData(): ArrayList<CatData> {
        list.clear()
        list.add(CatData("Less Than 6 Months", true))
        list.add(CatData("6 Months to 1 Year", false))
        list.add(CatData("1 Year to 3 Years", false))
        list.add(CatData("More than 3 Years", false))

        return list
    }

    override fun onItemClick(position: Int, isSelect: Boolean,alerttype:String) {
//        list.get(position).isSelect = isSelect
        when(alerttype){
            "category" ->   PROD_CATEGORY = list[position].name
            "condition" ->   PROD_CONDITION = list[position].name
            "used_for" ->   PROD_USED_FOR = list[position].name
        }
        for (i in 0 until list.size) list[i].isSelect = i == position

        alertAdaper.notifyDataSetChanged()
//        alertDialog.dismiss()
    }

    private fun isValidate(): Boolean {
        with(binding) {

            if (etProductName.text!!.toString().trim().isNotBlank()) {
                textView6.isErrorEnabled = false
            }

            return when {
                etProductName.text!!.toString().trim().isBlank() -> {
                    textView6.error = "Please Enter Product Name"
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
                    descEdt.error =
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