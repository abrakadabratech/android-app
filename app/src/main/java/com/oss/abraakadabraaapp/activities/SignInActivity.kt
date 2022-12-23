package com.oss.abraakadabraaapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessaging
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivitySignInBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern


class SignInActivity : BaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    private val authViewModel: AuthViewModel by viewModel()

    private var latitude = ""
    private var longitude = ""
    private var address = ""

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUpObserver()

        with(binding) {

//            if (BuildConfig.DEBUG) {
//                tvTitle.text = "Hello 2.1"
//            }

            ivBack.setOnClickListener {
                onBackPressed()
            }

            llResetPasswordContent.setOnClickListener {
                if (isResetPasswordValidate()) {
                    if (isNetworkAvailable()) {

                        val map = HashMap<String, String>()

                        map[RequestKeys.email] = binding.etEmail.text.toString().trim()

                        sendOtp(map)

                    } else {
                        showSnackBar(
                            binding.clSignInActivity,
                            applicationContext.resources.getString(R.string.no_internet_connection_found)
                        )
                    }
                }
            }

            clSignUpAccount.setOnClickListener {
                startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            }

            signInBtn.setOnClickListener {
                hideSoftKeyboard()
                loginUser()
//                if (checkPermission()) {
//                    if (isLocationEnabled()) {
//                        loginUser()
//                    } else {
//                        showGpsEnableSnackBar(binding.clSignInActivity)
//                    }
//                } else {
//                    showPermissionSnackBar(binding.clSignInActivity)
//                }
            }
        }
    }

    private fun loginUser() {
        if (isValidate()) {
            if (isNetworkAvailable()) {
                // getLastLocation()

                val map = HashMap<String, String>()

                map[RequestKeys.email] = binding.etEmail.text.toString().trim()
                map[RequestKeys.password] = binding.etPassword.text.toString().trim()
                map[RequestKeys.deviceId] = getDeviceId()
                map[RequestKeys.deviceType] = ApiConstants.deviceType

                if (PreferencesManagement.getUserLocation(this@SignInActivity) != null) {
                    val userLocation = PreferencesManagement.getUserLocation(this@SignInActivity)!!
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
                    binding.clSignInActivity,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

    private fun setUpObserver() {

        authViewModel.sendOtpSuccess.observe(this, {
            showToast(it.responseMessage)
            val intent = Intent(this@SignInActivity, VerifyOtpActivity::class.java)
            intent.putExtra(Constants.email, binding.etEmail.text.toString())
            intent.putExtra(Constants.resetPassword, Constants.resetPassword)
            startActivity(intent)
        })

        authViewModel.signInSuccess.observe(this, {
            showToast(it.responseMessage)
            if (it.data != null) {
//                if (it.data.isEmailVerified == 0) {
                    val intent = Intent(this@SignInActivity, VerifyOtpActivity::class.java)
                    intent.putExtra(Constants.email, binding.etEmail.text.toString())
                    intent.putExtra(Constants.emailVerification, Constants.emailVerification)
                    intent.putExtra(Constants.userData, it.data)
                    startActivity(intent)
//                } else {
//                    loginInUser(it.data)
//                }
            }
        })

        authViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })

        authViewModel.isLoading.observe(this, { loader(it) })
    }

    private fun sendOtp(map: HashMap<String, String>) {
        authViewModel.sendOtp(map)
    }

    private fun getFCMToken(map: HashMap<String, String>) {

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            map[RequestKeys.firebaseToken] = it
            authViewModel.signIn(map)
        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }

    private fun isValidate(): Boolean {

        with(binding) {

            val emailPattern: Pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
            val emailMs: Matcher = emailPattern.matcher(etEmail.text.toString().trim())

            if (!emailMs.matches()) {
                textInputEmail.error = "Please Enter Valid Email Address"
                return false
            } else {
                textInputEmail.isErrorEnabled = false
            }

            if (binding.etPassword.text.toString().trim().isEmpty()) {
                textInputPassword.error = "Please Enter Password"
                textInputPassword.errorIconDrawable = null
                return false
            } else {
                textInputPassword.isErrorEnabled = false
            }

            return true
        }
    }


    private fun isResetPasswordValidate(): Boolean {

        with(binding) {
            val emailPattern: Pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
            val emailMs: Matcher = emailPattern.matcher(etEmail.text.toString().trim())

            if (!emailMs.matches()) {
                textInputEmail.error = "Please Enter Valid Email Address"
                return false
            } else {
                textInputEmail.isErrorEnabled = false
            }

            return true
        }
    }

}