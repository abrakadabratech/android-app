package com.oss.abraakadabraaapp.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.gms.location.*
import com.google.firebase.messaging.FirebaseMessaging
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivitySignUpBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val authViewModel: AuthViewModel by viewModel()

    private var latitude = ""
    private var longitude = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        setUpObserver()

        with(binding) {

            etFullName.filterByDataType(1)
            etPhoneNumber.filterNumber()

            llLoginAccount.setOnClickListener {
                onBackPressed()
            }

            signUpBtn.setOnClickListener {
                hideSoftKeyboard()
                registerUser()
//                if (checkPermission()) {
//                    if (isLocationEnabled()) {
//                        registerUser()
//                    } else {
//                        showGpsEnableSnackBar(binding.clSignUpActivity)
//                    }
//                } else {
//                    showPermissionSnackBar(binding.clSignUpActivity)
//                }
            }
        }
    }

    private fun registerUser() {
        if (isValidate()) {
            if (isNetworkAvailable()) {
//                getLastLocation()

                val map = HashMap<String, String>()

                map[RequestKeys.name] = binding.etFullName.text.toString().trim()
                map[RequestKeys.email] = binding.etEmail.text.toString().trim()
                map[RequestKeys.phoneNumber] =
                    binding.etPhoneNumber.text.toString().trim()
                map[RequestKeys.password] = binding.etPassword.text.toString().trim()
                map[RequestKeys.deviceId] = getDeviceId()
                map[RequestKeys.deviceType] = ApiConstants.deviceType

                if (PreferencesManagement.getUserLocation(this@SignUpActivity) != null) {
                    val userLocation = PreferencesManagement.getUserLocation(this@SignUpActivity)!!
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
                    binding.clSignUpActivity,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this, { loader(it) })

        authViewModel.signUpSuccess.observe(this, {
            val intent = Intent(this@SignUpActivity, VerifyOtpActivity::class.java)
            intent.putExtra(Constants.email, binding.etEmail.text.toString())
            intent.putExtra(Constants.userData, it.data)
            startActivity(intent)
        })

        authViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })
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
            val ms: Matcher = ps.matcher(etFullName.text.toString().trim())

            val emailPattern: Pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
            val emailMs: Matcher = emailPattern.matcher(etEmail.text.toString().trim())

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

            if (!ValidatorUtils.isMobileValidate(binding.etPhoneNumber.text.toString().trim())) {
                textInputPhoneNumber.error = "Please Enter Valid Mobile Number"
                return false
            } else {
                textInputPhoneNumber.isErrorEnabled = false
            }

            if (etPassword.text.toString().trim().isEmpty()) {
                textInputPassword.error = "Please Enter Password"
                textInputPassword.errorIconDrawable = null
                return false
            } else {
                textInputPassword.isErrorEnabled = false
            }

//            if (!ValidatorUtils.isValidPassword(etPassword.text!!.toString().trim())) {
//                textInputPassword.error = "Use 6 or more characters with a mix of letters, numbers & symbols"
//                textInputPassword.errorIconDrawable = null
//                return false
//            } else {
//                textInputPassword.isErrorEnabled = false
//            }

            return true
        }
    }

}