package com.oss.abraakadabraaapp.activities.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.HomeActivity
import com.oss.abraakadabraaapp.activities.VerifyOtpActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.databinding.ActivityAuthUserDetailBinding
import com.oss.abraakadabraaapp.databinding.ActivityLoginBinding
import com.oss.abraakadabraaapp.model.UserData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class AuthUserDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityAuthUserDetailBinding

    private lateinit var phoneNumber: String

    private val authViewModel: AuthViewModel by viewModel()

    private var latitude = ""
    private var longitude = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthUserDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        phoneNumber = intent.getStringExtra(Constants.phoneNumber) ?: "1234567890"

        window.statusBarColor =
            ContextCompat.getColor(
                this@AuthUserDetailActivity,
                R.color.blue_status_bar_color
            )

//        setUpObserver()

        with(binding) {

            etName.filterByDataType(1)
            etEmail.filterEmoji()

            letsGetStartedBtn.setOnClickListener {
                hideSoftKeyboard()

                startActivity(Intent(applicationContext,NewHomeActivity::class.java))
                //Old Code
                //registerUser()
            }
        }

    }

    private fun registerUser() {
        if (isValidate()) {
            if (isNetworkAvailable()) {
                if (isLocationEnabled()) getLastLocation()

                val map = HashMap<String, String>()

                map[RequestKeys.name] = binding.etName.text.toString().trim()
                map[RequestKeys.email] = binding.etEmail.text.toString().trim()
                map[RequestKeys.phoneNumber] = phoneNumber.trim()
                map[RequestKeys.deviceId] = getDeviceId()
                map[RequestKeys.deviceType] = ApiConstants.deviceType

                if (PreferencesManagement.getUserLocation(this@AuthUserDetailActivity) != null) {
                    val userLocation =
                        PreferencesManagement.getUserLocation(this@AuthUserDetailActivity)!!
                    latitude = userLocation.lat
                    longitude = userLocation.long
                    address = userLocation.address ?: ""
                }

                map[RequestKeys.lat] = latitude
                map[RequestKeys.lng] = longitude
                map[RequestKeys.fullAddress] = address

                getFCMToken(map)

            } else {
                showSnackBar(
                    binding.clAuthUserDetails,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }

        authViewModel.signUpSuccess.observe(this) {
            showToast(it.responseMessage)
            loginInUser(it.data)
        }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
    }

    private fun getFCMToken(map: HashMap<String, String>) {

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            map[RequestKeys.firebaseToken] = it
            authViewModel.signUp(map)
        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }

    private fun isValidate(): Boolean {
        with(binding) {
            val ps: Pattern = Pattern.compile("^[a-zA-Z ]+$")
            val ms: Matcher = ps.matcher(etName.text.toString().trim())

            val emailPattern: Pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
            val emailMs: Matcher = emailPattern.matcher(etEmail.text.toString().trim())

            if (etName.text!!.length <= 3) {
                showToast("Please Enter Valid Full Name")
                return false
            }

            if (etName.text!!.length > 50) {
                showToast("Full Name must be less than 50 character")
                return false
            }

            if (!ms.matches()) {
                showToast("Full Name does not contain any special character or numbers")
                return false
            }

            if (!emailMs.matches()) {
                showToast("Please Enter Valid Email Address")
                return false
            }

            return true
        }
    }

}