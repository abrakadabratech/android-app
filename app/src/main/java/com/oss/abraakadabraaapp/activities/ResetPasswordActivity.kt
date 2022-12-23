package com.oss.abraakadabraaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityResetPasswordBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ResetPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    private val authViewModel: AuthViewModel by viewModel()

    private lateinit var email :String

    companion object {
        fun createIntent(context: Context, email :String): Intent {
            val intent = Intent(context, ResetPasswordActivity::class.java)
            intent.putExtra(Constants.email, email)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra(Constants.email)) {
            email = intent.getStringExtra(Constants.email)!!
        }

        setUpObserver()

        with(binding) {

            ivBack.setOnClickListener {
                onBackPressed()
            }

            resetPasswordBtn.setOnClickListener {
                if (isValidate()) {
                    if (isNetworkAvailable()) {
                        resetPassword()
                    } else {
                        showSnackBar(
                            binding.clResetPasswordActivity,
                            applicationContext.resources.getString(R.string.no_internet_connection_found)
                        )
                    }
                }
            }
        }

    }

    private fun setUpObserver() {

        authViewModel.resetPasswordSuccess.observe(this, {
            showToast(it.responseMessage)
            backToLogIn()
        })

        authViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })

        authViewModel.isLoading.observe(this, { loader(it) })
    }

    private fun resetPassword() {
        val map = HashMap<String, String>()

        map[RequestKeys.newPassword] = binding.etConfirmPassword.text.toString().trim()
        map[RequestKeys.email] = email
        authViewModel.resetPassword(map)
    }

    private fun isValidate(): Boolean {

        with(binding) {

            if (binding.etNewPassword.text.toString().trim().isEmpty()) {
                textInputNewPassword.error = "Please New Enter Password"
                textInputNewPassword.errorIconDrawable = null
                return false
            } else {
                textInputNewPassword.isErrorEnabled = false
            }

            if (binding.etConfirmPassword.text.toString().trim().isEmpty()) {
                textInputConfirmPassword.error = "Please Confirm Enter Password"
                textInputConfirmPassword.errorIconDrawable = null
                return false
            } else {
                textInputConfirmPassword.isErrorEnabled = false
            }

            if (binding.etConfirmPassword.text.toString().trim() != binding.etNewPassword.text.toString().trim()) {
                textInputConfirmPassword.error = "Password does not match"
                textInputConfirmPassword.errorIconDrawable = null
                return false
            } else {
                textInputConfirmPassword.isErrorEnabled = false
            }

            return true
        }
    }

}