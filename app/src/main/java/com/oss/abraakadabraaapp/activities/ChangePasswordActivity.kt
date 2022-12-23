package com.oss.abraakadabraaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityChangePasswordBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var toolbarBinding: LoggedInUserToolbarBinding

    private val authViewModel: AuthViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    companion object {
        fun createIntent(context: Context) {
            val intent = Intent(context, ChangePasswordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        toolbarBinding = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        setSupportActionBar(toolbarBinding.toolbar)
        toolbarBinding.tvToolbarTitle.text =
            applicationContext.resources.getString(R.string.change_password)

        toolbarBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        setUpObserver()

        with(binding) {

            saveBtn.setOnClickListener {
                changePassword()
            }
        }

    }

    private fun changePassword() {
        if (isValidate()) {
            if (isNetworkAvailable()) {

                val map = HashMap<String, String>()
                map[RequestKeys.userId] = userData.id.toString()
                map[RequestKeys.oldPassword] = binding.etOldPassword.text.toString().trim()
                map[RequestKeys.newPassword] = binding.etConfirmPassword.text.toString().trim()

                authViewModel.changePassword(Utility.getHeaders(this@ChangePasswordActivity), map)

            } else {
                showSnackBar(
                    binding.clChangePassword,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

    private fun setUpObserver() {

        authViewModel.changePasswordSuccess.observe(this, {
showToast(it.responseMessage)
            finish()
        })

        authViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })

        authViewModel.isLoading.observe(this, { loader(it) })
    }

    private fun isValidate(): Boolean {

        with(binding) {
            if (etOldPassword.text.toString().trim().isEmpty()) {
                textInputOldPassword.error = "Please Enter Old Password"
                textInputOldPassword.errorIconDrawable = null
                return false
            } else {
                textInputOldPassword.isErrorEnabled = false
            }

//            if (!ValidatorUtils.isValidPassword(etOldPassword.text!!.toString().trim())) {
//                textInputOldPassword.error = "Use 6 or more characters with a mix of letters, numbers & symbols"
//                textInputOldPassword.errorIconDrawable = null
//                return false
//            } else {
//                textInputOldPassword.isErrorEnabled = false
//            }

            if (etNewPassword.text.toString().trim().isEmpty()) {
                textInputNewPassword.error = "Please Enter New Password"
                textInputNewPassword.errorIconDrawable = null
                return false
            } else {
                textInputNewPassword.isErrorEnabled = false
            }

//            if (!ValidatorUtils.isValidPassword(etNewPassword.text!!.toString().trim())) {
//                textInputNewPassword.error = "Use 6 or more characters with a mix of letters, numbers & symbols"
//                textInputNewPassword.errorIconDrawable = null
//                return false
//            } else {
//                textInputNewPassword.isErrorEnabled = false
//            }

            if (etConfirmPassword.text.toString().trim().isEmpty()) {
                textInputConfirmPassword.error = "Please Enter Confirm Password"
                textInputConfirmPassword.errorIconDrawable = null
                return false
            } else {
                textInputConfirmPassword.isErrorEnabled = false
            }

//            if (!ValidatorUtils.isValidPassword(etConfirmPassword.text!!.toString().trim())) {
//                textInputConfirmPassword.error = "Use 6 or more characters with a mix of letters, numbers & symbols"
//                textInputConfirmPassword.errorIconDrawable = null
//                return false
//            } else {
//                textInputConfirmPassword.isErrorEnabled = false
//            }

            if (etNewPassword.text.toString().trim() != etConfirmPassword.text.toString().trim()) {
                textInputConfirmPassword.error = "Confirm does not match"
                textInputConfirmPassword.errorIconDrawable = null
                return false
            } else {
                textInputConfirmPassword.isErrorEnabled = false
            }

            return true
        }
    }
}