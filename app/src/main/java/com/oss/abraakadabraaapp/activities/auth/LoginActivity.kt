package com.oss.abraakadabraaapp.activities.auth

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.VerifyOtpActivity
import com.oss.abraakadabraaapp.databinding.ActivityLoginBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.customView.CustomTypefaceSpan
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModel()

    private var latitude = ""
    private var longitude = ""
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.statusBarColor =
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.blue_status_bar_color
            )

        setMessage()
        //setUpObserver()

        with(binding) {
            generateOtpBtn.setOnClickListener { loginUser()

                /*if (isValidate()) {
                    loginUser()
                }*/
            }
        }

    }

    private fun setUpObserver() {

        authViewModel.loginWithPhoneNumberSuccess.observe(this) {
//            showToast(it.responseMessage)
            val data = it.data
            val intent = Intent(this@LoginActivity, OtpVerificationActivity::class.java)
            if (data != null) {
                intent.putExtra(Constants.userData, data)
            }
            intent.putExtra(Constants.phoneNumber, binding.etPhoneNumber.text.toString().trim())
            startActivity(intent)
        }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

        authViewModel.isLoading.observe(this) { loader(it) }
    }

    private fun loginUser() {

        // New Code
        val intent = Intent(this@LoginActivity, OtpVerificationActivity::class.java)
        startActivity(intent)


        //Old Code
        /*if (isValidate()) {
            if (isNetworkAvailable()) {
                if (isLocationEnabled()) getLastLocation()

                val map = HashMap<String, String>()

                map[RequestKeys.phoneNumber] = binding.etPhoneNumber.text.toString().trim()
                map[RequestKeys.deviceId] = getDeviceId()
                map[RequestKeys.deviceType] = ApiConstants.deviceType

                if (PreferencesManagement.getUserLocation(this@LoginActivity) != null) {
                    val userLocation = PreferencesManagement.getUserLocation(this@LoginActivity)!!
                    latitude = userLocation.lat
                    longitude = userLocation.long
                    address = userLocation.address ?: ""
                }

                map[RequestKeys.lat] = latitude
                map[RequestKeys.lng] = longitude
                map[RequestKeys.fullAddress] = address
                map[RequestKeys.smsKey] = smsToken

                getFCMToken(map)

            } else {
                showSnackBar(
                    binding.clLogin,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }*/
    }

    private fun getFCMToken(map: HashMap<String, String>) {

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            map[RequestKeys.firebaseToken] = it
            authViewModel.loginWithPhoneNumber(map)
        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }

    private fun isValidate(): Boolean {
        with(binding) {
            if (!ValidatorUtils.isMobileValidate(etPhoneNumber.text.toString().trim())) {
                showToast("Please Enter Valid Mobile Number")
                return false
            }
            return true
        }
    }

    private fun setMessage() {
        val str = resources.getString(R.string.login_str)

        val regular = ResourcesCompat.getFont(this@LoginActivity, R.font.montserrat_regular)
        val bold = ResourcesCompat.getFont(this@LoginActivity, R.font.montserrat_bold)

        val span = SpannableStringBuilder(str)
        span.setSpan(CustomTypefaceSpan("", regular), 0, 44, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        span.setSpan(CustomTypefaceSpan("", bold), 44, str.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.tvMessage.text = span

        binding.tvTermsConditions.makeLinks(
            Pair("Privacy Policy", View.OnClickListener {
                LaunchUtility.launchUrl(
                    "https://abrakadabraapp.app/app/privacy-policy",
                    this@LoginActivity
                )
            })
        )

    }
}