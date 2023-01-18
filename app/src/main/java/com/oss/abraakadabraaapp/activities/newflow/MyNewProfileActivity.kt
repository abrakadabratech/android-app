package com.oss.abraakadabraaapp.activities.newflow

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.SocialShareAdapter
import com.oss.abraakadabraaapp.activities.newflow.apimodels.DataClass
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.model.SocialData
import com.oss.abraakadabraaapp.databinding.ActivityMyProfile2Binding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.customView.ImagePickerActivity
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class MyNewProfileActivity : BaseActivity(),SocialShareAdapter.OnSocialProfileClicked {
    private lateinit var binding: ActivityMyProfile2Binding
    var list = arrayListOf<SocialData>()
    lateinit var adapter:SocialShareAdapter
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfile2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_PROFILE,null)

        setUpProfile(PreferencesManagement.getUserInfo(this)!!)

        clickeEvents()

        setUpRecyclerView()
        setUpObserver()

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("TAG::", "onCreate: $it")
            PreferencesManagement.saveFCMToken(this,it)
        }.addOnFailureListener {
            Log.d("TAG::", "onCreate: $it")
        }
    }

    private fun setUpProfile(userInfo: GetUserResponse) {
        with(binding){
            nameEdit.setText(userInfo.data?.name)
            emailEdit.setText(userInfo.data?.email)
            phoneEdit.setText(userInfo.data?.phone)
            instaEdit.setText(userInfo.data?.socialLink)
            Glide.with(this@MyNewProfileActivity)
                .load(userInfo.data?.userAvatar)
                .into(profilePic)
        }
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }
        authViewModel.updateUserSuccess.observe(this) {
//            showToast(it.responseMessage.toString())
            PreferencesManagement.saveUserInfo(this,it)
            setUpProfile(it)
        }
        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        authViewModel.userProfilePicSuccess.observe(this) {
            showToast(it.responseMessage.toString())
//            getUserProfileApi()
        }
    }

    private fun clickeEvents() {
        binding.editProfile.setOnClickListener {
            postEvent(Constants.BUTTON_EDIT_PROFILE,null)
            binding.nameEdit.isEnabled = true
            binding.emailEdit.isEnabled = true
            binding.phoneEdit.isEnabled = true
            binding.locationEdit.isEnabled = true
            binding.instaEdit.isEnabled = true
            binding.nameEdit.requestFocus()
            binding.saveBtn.visibility = View.VISIBLE
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.saveBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SAVE_PROFILE,null)
            postUserData()
        }
        binding.instaEdit.setOnClickListener {
            //show profile popup

        }
        binding.instaEdit.setOnClickListener {
            binding.socialProfileLayout.visibility = View.VISIBLE
        }
        binding.socialProfileLayout.setOnClickListener {
            binding.socialProfileLayout.visibility = View.GONE
        }
        binding.submitBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SUBMIT_SOCIAL_PROFILE,null)
            binding.socialProfileLayout.visibility = View.GONE
            binding.successLayout.visibility = View.VISIBLE
        }
        binding.bottomSheet.setOnClickListener {
            binding.socialProfileLayout.visibility = View.VISIBLE
        }
        binding.okGotItBtn.setOnClickListener {
            postEvent(Constants.BUTTON_OK_GOT_IT_SOCIAL_PROFILE,null)
            binding.successLayout.visibility = View.GONE
            showToast("Under development")
        }
        binding.successLayout.setOnClickListener {
            binding.successLayout.visibility = View.GONE
        }
        binding.successDialog.setOnClickListener{
            binding.successLayout.visibility = View.VISIBLE
        }
        binding.uploadImage.setOnClickListener{
            selectImage()
        }
    }

    private fun postUserData() {
        if (isValidate()) {
            generateAuthToken()
            //getFCMToken()
            val data = UsersData(name = binding.nameEdit.text.toString(),
                    email = binding.emailEdit.text.toString(),
                fcmToken = PreferencesManagement.getFCMToken(this)!!)
            val dataClass = DataClass(data)

            val map = HashMap<String,String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token
            Log.d(NewHomeActivity.TAG, "Token in Accounts fragment: ${JSONObject(Gson().toJson(dataClass))}")
            authViewModel.updateUser(map,dataClass)
        }
    }
    private fun isValidate(): Boolean {
        return if (binding.nameEdit.text.toString().isNotBlank() &&
            binding.emailEdit.text.toString().isNotBlank()
        ) {
            true
        } else {
            showToast("Enter Name and Email ID")
            false
        }
    }

    private fun setUpRecyclerView() {
        var images: Array<Int> = arrayOf(
            R.drawable.fb_icon, R.drawable.linked_in_icon, R.drawable.twitter_icon,
            R.drawable.insta_icon
        )
        list.add(SocialData(R.drawable.fb_icon,true))
        list.add(SocialData(R.drawable.linked_in_icon,false))
        list.add(SocialData(R.drawable.twitter_icon,false))
        list.add(SocialData(R.drawable.insta_icon,false))

        adapter = SocialShareAdapter(this, list,this)
        val layoutManager = GridLayoutManager(this, 4)

        binding.rvSocialLinks.layoutManager = layoutManager
        binding.rvSocialLinks.adapter = adapter
    }

    override fun onSocialIconClick(position: Int,status:Boolean) {
        when(position){
            0 -> binding.socialProfileHeader.text = Constants.FB_URL
            1 -> binding.socialProfileHeader.text = Constants.LINKED_IN_URL
            2 -> binding.socialProfileHeader.text = Constants.TWITTER_URL
            3 -> binding.socialProfileHeader.text = Constants.INSTA_URL
        }
        for (i in 0 until list.size) list[i].status = i == position

        adapter.notifyDataSetChanged()
    }
    private fun selectImage() {
        postEvent(Constants.BUTTON_UPLOAD_IMAGE,null)
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        bannerOptions()
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
    private fun bannerOptions() {
        ImagePickerActivity.showImagePickerOptions(
            this,
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() {
                    bannerCameraIntent()
                }

                override fun onChooseGallerySelected() {
                    openYourActivity()
                }
            })
    }

    private fun bannerCameraIntent() {
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        businessProofImageActivity.launch(intent)

    }

    private fun openYourActivity() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(intent)
    }
    private var businessProofImageActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = data!!.getParcelableExtra<Uri>("path")!!

                try {
                    val bitmap = ImageUtils.uriToBitMap(uri, this@MyNewProfileActivity)

                    val imageFile =
                        ImageUtils.bitmapToFile(bitmap, this@MyNewProfileActivity, "profile.jpg")

                    lifecycleScope.launch {
                        val compressedImageFile =
                            Compressor.compress(this@MyNewProfileActivity, imageFile)
                        updatePhoto(imageFile)
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                binding.profilePic.setImageURI(uri)
            }
        }

    private fun updatePhoto(path: File) {
        Log.d(API_TAG, "updatePhoto: file path internally:${path.path}")
        val authToken = PreferencesManagement.getAuthToken(this)!!

        val authMap = HashMap<String,String>()
        authMap[RequestKeys.authorization] = authToken

        val partMap = HashMap<String, RequestBody>()
        val fbody: RequestBody = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            path
        )
//        partMap[RequestKeys.image] = JavaUtils.profileImagePrepareFilePart(path.absolutePath)

        Log.d(API_TAG, "updatePhoto: ${JavaUtils.profileImagePrepareFilePart1(path.absolutePath)} \n ${Gson().toJson(authMap)}")
        authViewModel.updateProfilePic(
            authMap,
            JavaUtils.profileImagePrepareFilePart1(path.absolutePath)
        )
    }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode === RESULT_OK) {
                try {
                    val imageUri = data!!.data
                    val imageStream: InputStream? = contentResolver.openInputStream(imageUri!!)
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    binding.profilePic.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
}