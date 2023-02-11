package com.oss.abraakadabraaapp.activities

import android.content.Intent
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityVerifyOtpBinding
import com.oss.abraakadabraaapp.model.UserData
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.GenericKeyEvent
import com.oss.abraakadabraaapp.utils.GenericTextWatcher
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class VerifyOtpActivity : BaseActivity() {

    private lateinit var binding: ActivityVerifyOtpBinding

    private var email = ""
    private var businessId = ""
    private lateinit var userData: UserData

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.verifyBtn.setOnClickListener {
//            startActivity(Intent(this, HomeActivity::class.java))
        }

        if (intent.hasExtra(Constants.email)) {
            email = intent.getStringExtra(Constants.email)!!
        }

        if (intent.hasExtra(Constants.userData)) {
            userData = intent.getParcelableExtra(Constants.userData)!!
        }

        initEditText()
        setUpObserver()

        with(binding) {

            llResendOtp.setOnClickListener {

                firstEdit.setText("")
                secondEdit.setText("")
                thirdEdit.setText("")
                fourthEdit.setText("")

                firstEdit.requestFocus()

                if (isNetworkAvailable()) {

                    val map = HashMap<String, String>()
                    map[RequestKeys.email] = email

                    resendOtp(map)

                } else {
                    showSnackBar(
                        clVerifyOtpActivity,
                        applicationContext.resources.getString(R.string.no_internet_connection_found)
                    )
                }

            }

            verifyBtn.setOnClickListener {
                verifyOtp()
            }
        }

    }

    private fun verifyOtp() {
        if (isValidate()) {
            if (isNetworkAvailable()) {

                val otpString = binding.firstEdit.text.toString() +
                        binding.secondEdit.text.toString() +
                        binding.thirdEdit.text.toString() +
                        binding.fourthEdit.text.toString()

                val map = HashMap<String, String>()

                map[RequestKeys.otp] = otpString
                map[RequestKeys.email] = email

                authViewModel.otpVerification(map)

            } else {
                showSnackBar(
                    binding.clVerifyOtpActivity,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }

    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this, { loader(it) })

        authViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })

        authViewModel.otpVerificationSuccess.observe(this, {
            otpVerification(it!!)
        })

        authViewModel.sendOtpSuccess.observe(this, {
            showToast(it.responseMessage)
        })
    }

    private fun otpVerification(it: CommonResponse) {
       /* when{
            intent.hasExtra(Constants.emailVerification)-> {
                showToast(it.responseMessage)
                loginInUser(intent.getParcelableExtra(Constants.userData)!!)
            }
            intent.hasExtra(Constants.resetPassword)-> {
                showToast(it.responseMessage)
                startActivity(ResetPasswordActivity.createIntent(this@VerifyOtpActivity, email))
                finish()
            }
            else -> {
                showToast("Signup Successfully")
                loginInUser(intent.getParcelableExtra(Constants.userData)!!)
            }
        }*/
    }

    private fun resendOtp(map: HashMap<String, String>) {
        authViewModel.sendOtp(map)
    }

    private fun isValidate(): Boolean {
        return if (binding.firstEdit.text.toString().isNotBlank() &&
            binding.secondEdit.text.toString().isNotBlank() &&
            binding.thirdEdit.text.toString().isNotBlank() &&
            binding.fourthEdit.text.toString().isNotBlank()
        ) {
            true
        } else {
            showToast(resources.getString(R.string.enter_valid_otp))
            false
        }
    }

    private fun initEditText() {

        binding.firstEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.firstEdit,
                binding.secondEdit,
                this
            )
        )
        binding.secondEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.secondEdit,
                binding.thirdEdit,
                this
            )
        )
        binding.thirdEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.thirdEdit,
                binding.fourthEdit,
                this
            )
        )

        binding.fourthEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.fourthEdit,
                null,
                this
            )
        )

        binding.firstEdit.setOnKeyListener(GenericKeyEvent(binding.firstEdit, null))
        binding.secondEdit.setOnKeyListener(GenericKeyEvent(binding.secondEdit, binding.firstEdit))
        binding.thirdEdit.setOnKeyListener(GenericKeyEvent(binding.thirdEdit, binding.secondEdit))
        binding.fourthEdit.setOnKeyListener(GenericKeyEvent(binding.fourthEdit, binding.thirdEdit))

    }

}