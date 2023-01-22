package com.oss.abraakadabraaapp.activities.newflow

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.UserInfo
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
import com.oss.abraakadabraaapp.utils.Constants.facebook
import com.oss.abraakadabraaapp.utils.Constants.instagram
import com.oss.abraakadabraaapp.utils.Constants.linkedin
import com.oss.abraakadabraaapp.utils.Constants.twitter
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


class MyNewProfileActivity : BaseActivity(), SocialShareAdapter.OnSocialProfileClicked {
    private lateinit var binding: ActivityMyProfile2Binding
    var list = arrayListOf<SocialData>()
    lateinit var adapter: SocialShareAdapter
    private val authViewModel: AuthViewModel by viewModel()
    private var socialLinkType = "facebook"
    private lateinit var userInfo: GetUserResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfile2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_PROFILE, null)
        userInfo = PreferencesManagement.getUserInfo(this)!!
        setUpProfile(userInfo)
        clickeEvents()
        setUpObserver()

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("TAG::", "onCreate: $it")
            PreferencesManagement.saveFCMToken(this, it)
        }.addOnFailureListener {
            Log.d("TAG::", "onCreate: $it")
        }
    }

    private fun setUpProfile(userInfo: GetUserResponse) {
        with(binding) {
            nameEdit.setText(userInfo.data?.name)
            emailEdit.setText(userInfo.data?.email)
            phoneEdit.setText(userInfo.data?.phone)
            instaEdit.setText(userInfo.data?.socialLink)
            Glide.with(this@MyNewProfileActivity)
                .load(userInfo.data?.userAvatar)
                .placeholder(resources.getDrawable(R.drawable.ic_profile))
                .into(profilePic)
            list.clear()
            when (userInfo.data?.socialLinkType) {
                facebook -> {
                    list.add(SocialData(R.drawable.fb_icon, true))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, false))
                }
                linkedin -> {
                    list.add(SocialData(R.drawable.fb_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, true))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, false))
                }
                twitter -> {
                    list.add(SocialData(R.drawable.fb_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, true))
                    list.add(SocialData(R.drawable.insta_icon, false))
                }
                instagram -> {
                    list.add(SocialData(R.drawable.fb_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, true))
                }
                else -> {
                    list.add(SocialData(R.drawable.fb_icon, true))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, false))
                }
            }
//            val parsedText = getUserNameFromSocialLink(userInfo.data?.socialLink,
//                userInfo.data?.socialLinkType)
//            Log.d("TAG::", "setUpProfile: Parsed Text $parsedText")
//            binding.socialProfileEdt.setText(parsedText)
            setUpRecyclerView()
        }
    }

    private fun getUserNameFromSocialLink(socialLink: String?, socialLinkType: String?): String? {
        var result = ""
        if (!socialLink.equals("")){
            when(socialLinkType){
                facebook -> result = socialLink?.split("https://www.facebook.com/")?.get(1).toString()
                linkedin -> result = socialLink?.split("https://www.instagram.com/")?.get(1).toString()
                twitter -> result = socialLink?.split("https://www.linkedin.com/in/")?.get(1).toString()
                instagram -> result = socialLink?.split("https://twitter.com/")?.get(1).toString()
            }
        }
        return result
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }
        authViewModel.updateUserSuccess.observe(this) {
//            showToast(it.responseMessage.toString())
            if (it.code == 500){
                showToast(it.responseMessage.toString())
            }else {
                PreferencesManagement.saveUserInfo(this, it)
                setUpProfile(it)
            }
        }
        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        authViewModel.userProfilePicSuccess.observe(this) {
            if (it.code == 200){
                showToast(it.responseMessage.toString())

                val userInfo = PreferencesManagement.getUserInfo(this)!!
                val userData = UsersData(phone = userInfo.data?.phone,
                    socialLinkType = userInfo.data?.socialLinkType,
                    socialLink = userInfo.data?.socialLink,
                    name = userInfo.data?.name,
                    userAvatar = it.data,
                    email = userInfo.data?.email,
                    uid = userInfo.data?.uid,
                    fcmToken = userInfo.data?.fcmToken
                )
                val newUserInfo = GetUserResponse(
                    code = userInfo.code,
                    responseMessage = userInfo.responseMessage,
                    status = userInfo.status,
                    data = userData
                )
                PreferencesManagement.saveUserInfo(this,newUserInfo)
//            getUserProfileApi()
            }

        }
        authViewModel.postSocialProfileSuccess.observe(this) {

            val userInfo = PreferencesManagement.getUserInfo(this)!!
            val userData = UsersData(phone = userInfo.data?.phone,
                socialLinkType = it.data?.socialLinkType,
                socialLink = it.data?.socialLink,
                name = userInfo.data?.name,
                userAvatar = userInfo.data?.userAvatar,
                email = userInfo.data?.email,
                uid = userInfo.data?.uid,
                fcmToken = userInfo.data?.fcmToken
            )
            val newUserInfo = GetUserResponse(
                code = userInfo.code,
                responseMessage = userInfo.responseMessage,
                status = userInfo.status,
                data = userData
            )
            PreferencesManagement.saveUserInfo(this,newUserInfo)
//            setUpProfile(newUserInfo)
            Log.d(API_TAG, "postSocialProfileSuccess: ${Gson().toJson(newUserInfo)}")
            binding.socialProfileLayout.visibility = View.GONE
            binding.successLayout.visibility = View.VISIBLE
            binding.instaEdit.text = it.data?.socialLink
            binding.socialProfilePopUPLayout.visibility = View.GONE
            editMode(false)
        }
    }

    private fun clickeEvents() {
        binding.uploadImage.isEnabled = false
        binding.editProfile.setOnClickListener {
            postEvent(Constants.BUTTON_EDIT_PROFILE, null)

            editMode(true)
        }
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.saveBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SAVE_PROFILE, null)
            postUserData()
        }
        binding.instaEdit.setOnClickListener {
            userInfo.data?.socialLinkType
            binding.socialProfileLayout.visibility = View.VISIBLE
        }
        binding.socialProfileLayout.setOnClickListener {
            binding.socialProfileLayout.visibility = View.GONE
        }
        binding.submitBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SUBMIT_SOCIAL_PROFILE, null)

        }
        binding.bottomSheet.setOnClickListener {
            binding.socialProfileLayout.visibility = View.VISIBLE

        }
        binding.okGotItBtn.setOnClickListener {
            postEvent(Constants.BUTTON_OK_GOT_IT_SOCIAL_PROFILE, null)
            binding.successLayout.visibility = View.GONE
//            showToast("Under development")
        }
        binding.successLayout.setOnClickListener {
            binding.successLayout.visibility = View.GONE
        }
        binding.successDialog.setOnClickListener {
            binding.successLayout.visibility = View.VISIBLE
        }
        binding.uploadImage.setOnClickListener {
            selectImage()
        }
        binding.socialProfilePopUPLayout.setOnClickListener {
            binding.socialProfilePopUPLayout.visibility = View.GONE
            binding.socialProfileLayout.visibility = View.VISIBLE
        }
        binding.successDialog1.setOnClickListener {
            binding.socialProfilePopUPLayout.visibility = View.VISIBLE
        }
        binding.okGotItBtn1.setOnClickListener {
            postEvent(Constants.BUTTON_LETS_START_SOCIAL_PROFILE,null)
            postUserProfile()
        }
    }

    private fun editMode(isEditMode: Boolean) {

        binding.nameEdit.isEnabled = isEditMode
        binding.uploadImage.isEnabled = isEditMode
        binding.emailEdit.isEnabled = isEditMode
//        binding.phoneEdit.isEnabled = isEditMode
//        binding.locationEdit.isEnabled = isEditMode
        binding.instaEdit.isEnabled = isEditMode
        if (isEditMode) {
            binding.nameEdit.requestFocus()
            binding.nameEdit.setSelection(binding.nameEdit.text.toString().length)
            binding.saveBtn.visibility = View.VISIBLE
            binding.editProfile.visibility = View.INVISIBLE
        } else {
            binding.saveBtn.visibility = View.GONE
            binding.editProfile.visibility = View.VISIBLE
        }

    }

    private fun postUserProfile() {
        if (isUserProfileValidate()) {
            if (isNetworkAvailable()) {
                generateAuthToken()
                val mapAuth = HashMap<String, String>()
                /*if (PreferencesManagement.getAuthToken(this@AuthUserDetailActivity) != null){
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }else{
                    generateAuthToken()
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }*/
                mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

                val map = HashMap<String, String>()

                map[RequestKeys.social_link_type] = socialLinkType
                map[RequestKeys.social_link] = binding.socialProfileHeader.text.toString().trim()+
                        binding.profileLink.text.toString().trim()

                authViewModel.postUserSocialProfile(mapAuth, map)
            }
        }
    }

    private fun isUserProfileValidate(): Boolean {
        with(binding) {
            if (socialProfileEdt.text!!.length <= 3) {
                showToast("Please Enter Valid Full Name")
                return false
            }

            if (socialProfileEdt.text!!.length > 50) {
                showToast("Full Name must be less than 50 character")
                return false
            }

            return true
        }
    }

    private fun postUserData() {
        if (isValidate()) {
            generateAuthToken()
            //getFCMToken()
            val data = UsersData(
                name = binding.nameEdit.text.toString(),
                email = binding.emailEdit.text.toString(),
                fcmToken = PreferencesManagement.getFCMToken(this)!!
            )
            val dataClass = DataClass(data)

            val map = HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token
            Log.d(
                NewHomeActivity.TAG,
                "Token in Accounts fragment: ${JSONObject(Gson().toJson(dataClass))}"
            )
            authViewModel.updateUser(map, dataClass)
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
        adapter = SocialShareAdapter(this, list, this)
        val layoutManager = GridLayoutManager(this, 4)

        binding.rvSocialLinks.layoutManager = layoutManager
        binding.rvSocialLinks.adapter = adapter
    }

    override fun onSocialIconClick(position: Int, status: Boolean) {
        when (position) {
            0 -> {
                binding.socialProfileHeader.text = Constants.FB_URL
                socialLinkType = "facebook"
            }

            1 -> {
                binding.socialProfileHeader.text = Constants.LINKED_IN_URL
                socialLinkType = "linkedin"
            }
            2 -> {
                binding.socialProfileHeader.text = Constants.TWITTER_URL
                socialLinkType = "twitter"
            }
            3 -> {
                binding.socialProfileHeader.text = Constants.INSTA_URL
                socialLinkType = "instagram"
            }
        }
        for (i in 0 until list.size) list[i].status = i == position

        adapter.notifyDataSetChanged()
        binding.socialProfileLayout.visibility = View.GONE
        binding.socialProfilePopUPLayout.visibility = View.VISIBLE

    }

    private fun selectImage() {
        postEvent(Constants.BUTTON_UPLOAD_IMAGE, null)
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

        val authMap = HashMap<String, String>()
        authMap[RequestKeys.authorization] = authToken

        val partMap = HashMap<String, RequestBody>()
        val fbody: RequestBody = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            path
        )
//        partMap[RequestKeys.image] = JavaUtils.profileImagePrepareFilePart(path.absolutePath)

        Log.d(
            API_TAG,
            "updatePhoto: ${JavaUtils.profileImagePrepareFilePart1(path.absolutePath)} \n ${
                Gson().toJson(authMap)
            }"
        )
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

                    // updatePhoto(selectedImage)
                    binding.profilePic.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        }
}