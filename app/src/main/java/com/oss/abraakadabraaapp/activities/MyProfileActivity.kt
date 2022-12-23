package com.oss.abraakadabraaapp.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityMyProfileBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.ImageUtils
import com.oss.abraakadabraaapp.utils.JavaUtils
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.utils.customView.ImagePickerActivity
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*

class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    private val authViewModel: AuthViewModel by viewModel()

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MyProfileActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        hideSoftKeyboard()

        setUpObserver()

        with(binding) {

            ivBack.setOnClickListener {
                onBackPressed()
            }

            tvEditProfile.setOnClickListener {
                EditProfileActivity.createIntent(this@MyProfileActivity)
            }

            changePasswordBtn.setOnClickListener {
                ChangePasswordActivity.createIntent(this@MyProfileActivity)
            }

            addProfileImage.setOnClickListener {
                selectPhoto()
            }

        }

    }

    override fun onResume() {
        super.onResume()
        getUserProfileApi()
    }

    private fun getUserData() {
        val userData = PreferencesManagement.getUserData(this)!!

        with(binding) {
            tvHeaderName.text = userData.name
            tvHeaderNumber.text = userData.phoneNumber

            etFullName.setText(userData.name)
            etPhoneNumber.setText(userData.phoneNumber)
            etEmail.setText(userData.email)

            if (userData.fullAddress != null) {
                etLocation.setText(userData.fullAddress!!)
            } else {
                etLocation.setText(resources.getText(R.string.location_add_message))
            }

            textInputLocation.visibility = View.GONE

            if (userData.profileImage != null) {
                ImageUtils.setImage(
                    this@MyProfileActivity,
                    cvUserProfile,
                    userData.profileImage!!,
                    progressBar,
                    R.drawable.default_user_profile,
                )
            }
        }
    }

    private fun selectPhoto() {

        val permissions =   if (Build.VERSION.SDK_INT <= 29) {
            arrayListOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        } else {
            arrayListOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }


        Dexter.withContext(this)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
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

    private fun showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(
            this,
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    launchGalleryIntent()
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        launchSomeActivity.launch(intent)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(this@MyProfileActivity, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_GALLERY_IMAGE
        )
        launchSomeActivity.launch(intent)
    }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")!!

                try {
                    val bitmap = ImageUtils.uriToBitMap(uri, this@MyProfileActivity)

                    val imageFile =
                        ImageUtils.bitmapToFile(bitmap, this@MyProfileActivity, "profile.jpg")

                    lifecycleScope.launch {
                        val compressedImageFile =
                            Compressor.compress(this@MyProfileActivity, imageFile)
                        updatePhoto(compressedImageFile.path)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

    private fun updatePhoto(imagePath: String) {
        val userData = PreferencesManagement.getUserData(this)!!
        val partMap = HashMap<String, RequestBody>()
        partMap[RequestKeys.userId] = JavaUtils.toRequestBody(userData.id.toString())
        authViewModel.updateProfileImage(
            Utility.getHeaders(this),
            partMap,
            JavaUtils.profileImagePrepareFilePart(imagePath)
        )
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }
        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        authViewModel.updateProfileImageSuccess.observe(this) {
            showToast(it.responseMessage)
            getUserProfileApi()
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
            PreferencesManagement.saveUserData(this@MyProfileActivity, userData)
            getUserData()
        }

    }

    private fun getUserProfileApi() {
        if (isNetworkAvailable()) {
            val partMap = HashMap<String, String>()
            partMap[RequestKeys.userId] = userData.id.toString()
            authViewModel.getUserProfile(Utility.getHeaders(this@MyProfileActivity), partMap)
        } else {
            showSnackBar(
                binding.clMyProfile,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }
}