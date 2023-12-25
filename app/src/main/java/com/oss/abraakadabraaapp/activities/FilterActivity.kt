package com.oss.abraakadabraaapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityFilterBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterActivity : BaseActivity() {

    private lateinit var binding: ActivityFilterBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

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
    private lateinit var categoryId: String
    private lateinit var categoryName: String
    private lateinit var productAge: String
    private lateinit var productCondition: String
    private lateinit var productGender: String

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        latitude =   if (intent.hasExtra(Constants.latitude) && intent.getStringExtra(Constants.latitude) != null) {
            intent.getStringExtra(Constants.latitude)!!
        } else {
            ""
        }
        longitude =   if (intent.hasExtra(Constants.longitude) && intent.getStringExtra(Constants.longitude) != null) {
            intent.getStringExtra(Constants.longitude)!!
        } else {
            ""
        }
        fullAddress = intent.getStringExtra(Constants.fullAddress)!!
        categoryId = intent.getStringExtra(Constants.categoryId)!!
        categoryName = intent.getStringExtra(Constants.categoryName)!!
        productAge = intent.getStringExtra(Constants.productAge)!!
        productCondition = intent.getStringExtra(Constants.productCondition)!!
        productGender = intent.getStringExtra(Constants.productGender)!!

        Log.d(
            "MYT",
            "onCreate $latitude $longitude $fullAddress $categoryId $productAge $productCondition $productGender"
        )

        with(includeToolbar) {

            tvEditProfile.setTextColor(ContextCompat.getColor(this@FilterActivity, R.color.black))
            tvEditProfile.visibility = View.VISIBLE
            tvEditProfile.text = "All Clear"

            tvEditProfile.setOnClickListener {
                clearFilterData()
            }

            setSupportActionBar(toolbar)
            tvToolbarTitle.text =
                applicationContext.resources.getString(R.string.filter)

            ivBack.setOnClickListener {
                onBackPressed()
            }
        }
        val apiKey = BuildConfig.API_KEY

        if (apiKey.isEmpty()) {
            return
        }

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }

        placesClient = Places.createClient(this)

        initUI()
        setUpObserver()
        getCategory()
    }

    private fun clearFilterData() {
        categoryId = ""
        categoryName = ""
        productAge = ""
        productCondition = ""
        productGender = ""
        fullAddress = ""

        with(binding) {
            etLocation.setText("")
            categorySpinner.setSelection(0)
            genderSpinner.setSelection(0)
            productAgeSpinner.setSelection(0)
            productConditionSpinner.setSelection(0)
        }
    }

    private fun setUpObserver() {

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

                setData()

            }
        })

        mainViewModel.isLoading.observe(this, { loader(it) })

        mainViewModel.errorMessage.observe(this, { /*if (it.isNotBlank()) showToast(it)*/ })
    }

    private fun setData() {
        with(binding) {
            var count = 0
            if (categoryId.isNotBlank()) {
                categoryList.forEach {
                    if (it.id == categoryId.toInt()) {
                        categorySpinner.setSelection(count)
                    }
                    count++
                }
                categoryAdapter.notifyDataSetChanged()
            }

            if (productGender.isNotBlank()) {
                count = 0
                genderList.forEach {
                    if (it == productGender) {
                        genderSpinner.setSelection(count)
                    }
                    count++
                }
                genderAdapter.notifyDataSetChanged()
            }

            if (productAge.isNotBlank()) {
                count = 0
                productAgeList.forEach {
                    if (it == productAge) {
                        productAgeSpinner.setSelection(count)
                    }
                    count++
                }
                productAgeAdapter.notifyDataSetChanged()
            }

            if (productCondition.isNotBlank()) {
                count = 0
                productConditionList.forEach {
                    if (it == productCondition) {
                        productConditionSpinner.setSelection(count)
                    }
                    count++
                }
                productConditionAdapter.notifyDataSetChanged()
            }
            etLocation.setText(fullAddress)

        }


    }

    private fun getCategory() {
        if (isNetworkAvailable()) {
            mainViewModel.getCategory(Utility.getHeaders(this@FilterActivity))
        } else {
            showSnackBar(
                binding.clFilterActivity,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun initUI() {
        with(binding) {

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

            genderSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    val tv: TextView = genderSpinner.findViewById(R.id.spinner_text)
                    val selectedGender = genderSpinner.selectedItem.toString()
                    if (selectedGender == resources.getString(R.string.select_gender)) {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.hint_color
                            )
                        )
                    } else {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.black
                            )
                        )
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            productAgeSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    val tv: TextView = productAgeSpinner.findViewById(R.id.spinner_text)
                    val selectedGender = productAgeSpinner.selectedItem.toString()
                    if (selectedGender == resources.getString(R.string.select_age)) {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.hint_color
                            )
                        )
                    } else {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.black
                            )
                        )
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            productConditionSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    val tv: TextView = productConditionSpinner.findViewById(R.id.spinner_text)
                    val selectedGender = productConditionSpinner.selectedItem.toString()
                    if (selectedGender == resources.getString(R.string.select_condition)) {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.hint_color
                            )
                        )
                    } else {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.black
                            )
                        )
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            categorySpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View,
                    position: Int,
                    id: Long
                ) {
                    val tv: TextView = categorySpinner.findViewById(R.id.spinner_text)
                    val selectedState = categorySpinner.selectedItem as CategoryData
                    if (selectedState.id == 0) {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.hint_color
                            )
                        )
                    } else {
                        tv.setTextColor(
                            ContextCompat.getColor(
                                this@FilterActivity,
                                R.color.black
                            )
                        )
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            ivGender.setOnClickListener {
                genderSpinner.performClick()
            }

            ivCategory.setOnClickListener {
                categorySpinner.performClick()
            }

            ivProductAge.setOnClickListener {
                productAgeSpinner.performClick()
            }

            ivProductCondition.setOnClickListener {
                productConditionSpinner.performClick()
            }

            etLocation.setOnClickListener {
                locationPicker()
            }

            continueBtn.setOnClickListener {

                val selectedCategory = categorySpinner.selectedItem as CategoryData

                if (selectedCategory.id != 0) {
                    categoryId = selectedCategory.id.toString()
                    categoryName = selectedCategory.categoryName
                } else {
                    categoryId = ""
                    categoryName = ""
                }

                productAge =
                    if (productAgeSpinner.selectedItem.toString() != resources.getString(R.string.select_age)) productAgeSpinner.selectedItem.toString() else ""
                productCondition =
                    if (productConditionSpinner.selectedItem.toString() != resources.getString(R.string.select_condition)) productConditionSpinner.selectedItem.toString() else ""
                productGender =
                    if (genderSpinner.selectedItem.toString() != resources.getString(R.string.select_gender)) genderSpinner.selectedItem.toString() else ""

                val intent = Intent()
                intent.putExtra(Constants.success, Constants.success)
                intent.putExtra(Constants.categoryName, categoryName)
                intent.putExtra(Constants.categoryId, categoryId)
                intent.putExtra(Constants.productAge, productAge)
                intent.putExtra(Constants.productCondition, productCondition)
                intent.putExtra(Constants.productGender, productGender)
                intent.putExtra(Constants.fullAddress, fullAddress)
                intent.putExtra(Constants.latitude, latitude)
                intent.putExtra(Constants.longitude, longitude)
                setResult(RESULT_OK, intent)
                finish()
            }

        }
    }

    private fun locationPicker() {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("IN")
            .build(this@FilterActivity)
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

                        binding.etLocation.setText(fullAddress)
                    }
                }
        }
}