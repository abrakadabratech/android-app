package com.oss.abraakadabraaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityEditProfileBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private val authViewModel: AuthViewModel by viewModel()

    private  var latitude :String?= null
    private  var longitude :String?= null
    private  var fullAddress :String?= null

    private lateinit var placesClient: PlacesClient

    companion object {
        fun createIntent(context: Context) {
            val intent = Intent(context, EditProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text =
            applicationContext.resources.getString(R.string.edit_profile)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        val apiKey = getString(R.string.akd)
        if (apiKey.isEmpty()) {
            return
        }

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey)
        }

        placesClient = Places.createClient(this)

        setUpObserver()
        getUserProfileApi()
        initUI()

        with(binding) {
            etLocation.setOnClickListener {
                locationPicker()
            }

            saveBtn.setOnClickListener {
                if (isValidate()) {
                    if (isNetworkAvailable()) {

                        val map = HashMap<String, String>()
                        map[RequestKeys.userId] = userData.id.toString()
                        map[RequestKeys.name] = binding.etFullName.text.toString()
                        map[RequestKeys.phoneNumber] = binding.etPhoneNumber.text.toString()
                        map[RequestKeys.email] = binding.etEmail.text.toString()

                        map[RequestKeys.fullAddress] = fullAddress?:""
                        map[RequestKeys.lat] = latitude?:""
                        map[RequestKeys.lng] = longitude?:""

                        authViewModel.updateUserProfile(
                            Utility.getHeaders(this@EditProfileActivity),
                            map
                        )

                    } else {
                        showSnackBar(
                            binding.clEditProfile,
                            applicationContext.resources.getString(R.string.no_internet_connection_found)
                        )
                    }
                }
            }
        }
    }

    private fun getUserProfileApi() {
        if (isNetworkAvailable()) {
            val partMap = java.util.HashMap<String, String>()
            partMap[RequestKeys.userId] = userData.id.toString()
            authViewModel.getUserProfile(Utility.getHeaders(this@EditProfileActivity), partMap)
        } else {
            showSnackBar(
                binding.clEditProfile,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun initUI() {
        val userData = PreferencesManagement.getUserData(this)!!

        with(binding) {

            etFullName.filterByDataType(1)
            etPhoneNumber.filterNumber()

            etFullName.setText(userData.name)
            etPhoneNumber.setText(userData.phoneNumber)
            etEmail.setText(userData.email)

            if (userData.fullAddress != null) {
                fullAddress = userData.fullAddress!!
                etLocation.setText(userData.fullAddress!!)
            }

            if(userData.lat != null && userData.lng != null){
                latitude =userData.lat!!
                longitude =userData.lng!!
            }

            textInputLocation.visibility = View.GONE

        }
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

        authViewModel.updateUserProfileSuccess.observe(this) {
            showToast(it.responseMessage)
            userData.name = binding.etFullName.text.toString().trim()
            userData.phoneNumber = binding.etPhoneNumber.text.toString().trim()
            userData.email = binding.etEmail.text.toString().trim()
            userData.fullAddress = fullAddress
            userData.lat = latitude
            userData.lng = longitude
            PreferencesManagement.saveUserData(this@EditProfileActivity, userData)
            finish()
        }

        authViewModel.getUserProfileSuccess.observe(this) {
            val data = it.data
            userData.profileImage = data.profileImage
            userData.name = data.name
            userData.phoneNumber = data.phoneNumber
            userData.lat = data.lat
            userData.lng = data.lng
            userData.fullAddress = data.fullAddress
            userData.email = data.email
            PreferencesManagement.saveUserData(this@EditProfileActivity, userData)
            initUI()
        }
    }

    private fun locationPicker() {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("IN")
            .build(this@EditProfileActivity)
        launchSomeActivity.launch(intent)
    }

    private var launchSomeActivity =
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

    private fun isValidate(): Boolean {
        with(binding) {
            val ps: Pattern = Pattern.compile("^[a-zA-Z ]+$")
            val ms: Matcher = ps.matcher(etFullName.text.toString())

            val emailPattern: Pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
            val emailMs: Matcher = emailPattern.matcher(etEmail.text.toString())

            if (etFullName.text!!.length <= 3) {
                textInputFullName.error = "Please Enter Valid Full Name"
                return false
            } else {
                textInputFullName.isErrorEnabled = false
            }

            if (etFullName.text!!.length > 50) {
                textInputFullName.error = "Full Name must be less than 50 character"
                return false
            } else {
                textInputFullName.isErrorEnabled = false
            }

            if (!ms.matches()) {
                textInputFullName.error =
                    "Full Name does not contain any special character or numbers"
                return false
            } else {
                textInputFullName.isErrorEnabled = false
            }

            if (!emailMs.matches()) {
                textInputEmail.error = "Please Enter Valid Email Address"
                return false
            } else {
                textInputEmail.isErrorEnabled = false
            }

//            if (etLocation.text.toString().trim().isEmpty()) {
//                textInputLocation.error = "Please Enter Location"
//                return false
//            } else {
//                textInputLocation.isErrorEnabled = false
//            }

            return true
        }
    }
}