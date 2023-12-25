package com.oss.abraakadabraaapp.activities.newflow

import DataClass
import UsersUpdateData
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.SocialShareAdapter
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.Timestamp
import com.oss.abraakadabraaapp.activities.newflow.apimodels.User_Stats
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.model.SocialData
import com.oss.abraakadabraaapp.databinding.ActivityMyProfile2Binding
import com.oss.abraakadabraaapp.model.ProductImage
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_PROFILE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SOCIAL_PROFILE_CHANGE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_UPLOAD_PROFILE_PIC
import com.oss.abraakadabraaapp.utils.Constants.SIGN_IN_METHOD_GOOGLE
import com.oss.abraakadabraaapp.utils.Constants.SIGN_IN_METHOD_PHONE
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
import java.util.ArrayList
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern


class MyNewProfileActivity : BaseActivity(), SocialShareAdapter.OnSocialProfileClicked {
    private lateinit var binding: ActivityMyProfile2Binding
    var list = arrayListOf<SocialData>()
    lateinit var adapter: SocialShareAdapter
    private val authViewModel: AuthViewModel by viewModel()
    private var socialLinkType = "facebook"
    private lateinit var userInfo: GetUserResponse
    private var profileLink = ""
    var from = "fragment"
    var status = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfile2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_PROFILE, null)

        from = intent.extras?.getString("from")!!
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

        if (from == "activity"){
            editMode(true)
            userInfo.data?.socialLinkType
            binding.socialProfileLayout.visibility = View.VISIBLE
        }

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("users")
            .document(currentUserId!!).addSnapshotListener { value, error ->
                status = value?.getString("status").toString()

                Log.d("Log-error", "onCreate: error"+error)
                setProfileStatus(status)
            }
    }

    private fun setProfileStatus(status: String) {
        var userInfo = PreferencesManagement.getUserInfo(this)
        userInfo?.data?.status = status
        PreferencesManagement.saveUserInfo(this,userInfo)
        with(binding){
            if (status == "pending" || status == "not verified"){
                profileStatus.setText(status!!.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                })
                profileStatus.setTextColor(resources.getColor(R.color.status_pending))
                profileStatusImage.setImageResource(R.drawable.status_pending)

            }else if (status == "declined"){
                profileStatus.setText(status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                })
                profileStatus.setTextColor(resources.getColor(R.color.status_declined))
                profileStatusImage.setImageResource(R.drawable.status_declined)
            }
            else if (status == "suspended"){
                profileStatus.setText(status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                })
                profileStatus.setTextColor(resources.getColor(R.color.status_declined))
                profileStatusImage.setImageResource(R.drawable.status_declined)
            }else{
                profileStatus.setText(status.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                })
                profileStatus.setTextColor(resources.getColor(R.color.status_accepted))
                profileStatusImage.setImageResource(R.drawable.status_accepted)
            }
        }
    }

    private fun setUpProfile(userInfo: GetUserResponse) {
        with(binding) {
            nameEdit.setText(userInfo.data?.name)
            emailEdit.setText(userInfo.data?.email)
            phoneEdit.setText(userInfo.data?.phone)
            val userlocation = PreferencesManagement.getUserLocation(this@MyNewProfileActivity)
            locationEdit.setText(userlocation?.address)

            instaEdit.setText(if(userInfo.data?.socialLink == "" ||userInfo.data?.socialLink == null) "No profile submitted"
            else userInfo.data?.socialLink)
            Glide.with(this@MyNewProfileActivity)
                .load(userInfo.data?.userAvatar)
                .placeholder(resources.getDrawable(R.drawable.user))
                .into(profilePic)
            list.clear()
            when (userInfo.data?.socialLinkType) {
                facebook -> {
                    list.add(SocialData(R.drawable.fb_icon, true))
                    list.add(SocialData(R.drawable.insta_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                }
                linkedin -> {
                    list.add(SocialData(R.drawable.fb_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, true))
                }
                twitter -> {
                    list.add(SocialData(R.drawable.fb_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, true))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                }
                instagram -> {
                    list.add(SocialData(R.drawable.fb_icon, false))
                    list.add(SocialData(R.drawable.insta_icon, true))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                }
                else -> {
                    list.add(SocialData(R.drawable.fb_icon, true))
                    list.add(SocialData(R.drawable.insta_icon, false))
                    list.add(SocialData(R.drawable.twitter_icon, false))
                    list.add(SocialData(R.drawable.linked_in_icon, false))
                }
            }
//            val parsedText = getUserNameFromSocialLink(userInfo.data?.socialLink,
//                userInfo.data?.socialLinkType)
//            Log.d("TAG::", "setUpProfile: Parsed Text $parsedText")
//            binding.socialProfileEdt.setText(parsedText)
            setUpRecyclerView()
        }
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }

        authViewModel.getUserSuccess.observe(this){
            if (it.code == 200){
                setUpProfile(it)
            }
        }
        authViewModel.updateUserSuccess.observe(this) {
//            showToast(it.responseMessage.toString())
            if (it.code == 500){
                showToast(it.responseMessage.toString())
            }else {
                editMode(false)
                setUpProfile(it)
                PreferencesManagement.saveUserInfo(this, it)
            }
        }
        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        authViewModel.userProfilePicSuccess.observe(this) {

            if (it.code == 200){
                showToast(it.responseMessage.toString())

                val userInfo = PreferencesManagement.getUserInfo(this)!!
                val userData = UsersData(phone = userInfo.data?.phone,
                    socialLinkType = socialLinkType,
                    socialLink = profileLink,
                    name = userInfo.data?.name,
                    userAvatar = it.data.userAvatar,
                    email = userInfo.data?.email,
                    uid = userInfo.data?.uid,
                    fcmToken = userInfo.data?.fcmToken,
                    location = userInfo.data?.location,
                    updated_at = userInfo.data?.updated_at,
                )
                val newUserInfo = GetUserResponse(
                    code = userInfo.code,
                    responseMessage = userInfo.responseMessage,
                    status = userInfo.status,
                    data = userData
                )
                PreferencesManagement.saveUserInfo(this,newUserInfo)


//            setUpProfile(it)
//            getUserProfileApi()
            }

        }
        authViewModel.postSocialProfileSuccess.observe(this) {

            binding.instaEdit.setText(profileLink)

            /*val userInfo = PreferencesManagement.getUserInfo(this)!!
            val userData = UsersData(phone = userInfo.data?.phone,
                status = userInfo.data?.status,
                socialLinkType = it.data?.socialLinkType,
                socialLink = it.data?.socialLink,
                userAvatar = it.data?.userAvatar,
                email = it.data?.email,
                name = it.data?.name,
                userStats = it.data?.userStats,
                uid = it.data?.uid,
                location = it.data?.location,
                location = userInfo.data?.location,
                updated_at = userInfo.data?.updated_at,
                signinMethod = userInfo.data?.signinMethod,
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
            binding.socialProfilePopUPLayout.visibility = View.GONE
            editMode(false)
            setProfileStatus(userInfo.data?.status.toString())*/
        }
    }

    private fun clickeEvents() {
        binding.uploadImage.isEnabled = false
        binding.editProfile.setOnClickListener {
            postClick(Constants.BUTTON_EDIT_PROFILE)

            editMode(true)
        }
        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_PROFILE)
            onBackPressed()
        }
        binding.saveBtn.setOnClickListener {
            postClick(Constants.BUTTON_SAVE_PROFILE)
            postUserData()
        }
        binding.instaEdit.setOnClickListener {
            postClick(BUTTON_SOCIAL_PROFILE_CHANGE)
            if (status == "declined" || status == "not verified"){
                userInfo.data?.socialLinkType
                binding.socialProfileLayout.visibility = View.VISIBLE
            }else{
                when(status){
                    "active" -> showToast("Profile already verified.")
                    "pending" -> showToast("Profile verification is pending.")
                    "suspended" -> showToast("Your profile is suspended.")
                }
            }

        }
        binding.socialProfileLayout.setOnClickListener {
            binding.socialProfileLayout.visibility = View.GONE
        }
        binding.submitBtn.setOnClickListener {
            postClick(Constants.BUTTON_SUBMIT_SOCIAL_PROFILE)

        }
        binding.bottomSheet.setOnClickListener {
            binding.socialProfileLayout.visibility = View.VISIBLE

        }
        binding.okGotItBtn.setOnClickListener {
            postClick(Constants.BUTTON_OK_GOT_IT_SOCIAL_PROFILE)
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
            postClick(BUTTON_UPLOAD_PROFILE_PIC)
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
            postClick(Constants.BUTTON_LETS_START_SOCIAL_PROFILE)
            postUserProfile()
        }
        binding.imageView11.setOnClickListener {
            binding.socialProfilePopUPLayout.visibility = View.GONE
        }

    }

    private fun editMode(isEditMode: Boolean) {

        binding.nameEdit.isEnabled = isEditMode
        binding.uploadImage.isEnabled = isEditMode
        if(PreferencesManagement.getSignInMethod(this) == SIGN_IN_METHOD_PHONE){
            binding.emailEdit.isEnabled = isEditMode
        }else{
            binding.phoneEdit.isEnabled = isEditMode
        }
//        binding.phoneEdit.isEnabled = isEditMode
//        binding.locationEdit.isEnabled = isEditMode
        binding.instaEdit.isEnabled = isEditMode
        if (isEditMode) {
//            binding.nameEdit.requestFocus()
            binding.nameEdit.setSelection(binding.nameEdit.text.toString().length)
            binding.saveBtn.visibility = View.VISIBLE
            binding.editProfile.visibility = View.INVISIBLE
//            binding.instaEdit.setText(if(userInfo.data?.socialLink == "")
//                "Update your profile here" else userInfo.data?.socialLink)

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

                profileLink = binding.socialProfileHeader.text.toString().trim()+
                        binding.profileLink.text.toString().trim()
                map[RequestKeys.social_link] = binding.socialProfileHeader.text.toString().trim()+
                        binding.profileLink.text.toString().trim()


//                authViewModel.postUserSocialProfile(mapAuth, map)
            }
        }

    }

    private fun isUserProfileValidate(): Boolean {
        with(binding) {
            if (profileLink.text!!.toString() == "") {
                showToast("Please Enter your profile ID")
                return false
            }

            if (profileLink.text!!.length > 30) {
                showToast("Profile ID must be less than 30 character")
                return false
            }

            return true
        }
    }

    private fun postUserData() {
        if (isValidate()) {
            generateAuthToken()
            //getFCMToken
            if (PreferencesManagement.getSignInMethod(this) == SIGN_IN_METHOD_GOOGLE){

                val body = HashMap<String,String>()
                body["name"] = binding.nameEdit.text.toString()
                body["phone"] = binding.phoneEdit.text.toString()

                val map = HashMap<String, String>()
                val token = PreferencesManagement.getAuthToken(this)!!
                map[RequestKeys.authorization] = token

                authViewModel.updateUserV2(map, body)
            }else{
                val body = HashMap<String,String>()
                body["name"] = binding.nameEdit.text.toString()
                body["email"] = binding.emailEdit.text.toString()

                val map = HashMap<String, String>()
                val token = PreferencesManagement.getAuthToken(this)!!
                map[RequestKeys.authorization] = token

                authViewModel.updateUserV2(map, body)
            }
        }
    }

    private fun isValidate(): Boolean {
//        val emailPattern: Pattern = Pattern.compile("^[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+$")
        val emailPattern: Pattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        val emailMs: Matcher = emailPattern.matcher(binding.emailEdit.text.toString().trim())

        if (!emailMs.matches()) {
            showToast("Please Enter Valid Email Address")
            return false
        }
        if (binding.nameEdit.text.toString().isEmpty()){
            showToast("Enter Name")
            return false
        }
        return true
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

            3 -> {
                binding.socialProfileHeader.text = Constants.LINKED_IN_URL
                socialLinkType = "linkedin"
            }
            2 -> {
                binding.socialProfileHeader.text = Constants.TWITTER_URL
                socialLinkType = "twitter"
            }
            1 -> {
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO)
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
        }else{
            Dexter.withContext(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA)
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
        val intent = Intent(this, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_GALLERY_IMAGE
        )

        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        businessProofImageActivity.launch(intent)

//        val intent = Intent()
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        intent.action = Intent.ACTION_GET_CONTENT
//        launchSomeActivity.launch(intent)
    }

    private var businessProofImageActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val uriList = data!!.getParcelableArrayListExtra<Uri>("imagesList") as ArrayList<Uri>

                try {
                    if (uriList.size != 0){
                        val bitmap = ImageUtils.uriToBitMap(uriList[0], this@MyNewProfileActivity)

                        val imageFile =
                            ImageUtils.bitmapToFile(bitmap, this@MyNewProfileActivity, "profile.jpg")

                        lifecycleScope.launch {
                            val compressedImageFile =
                                Compressor.compress(this@MyNewProfileActivity, imageFile)
                            updatePhoto(compressedImageFile)
                        }

                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

    private fun updatePhoto(path: File) {
        Log.d(API_TAG, "updatePhoto: file path internally:${path.path}")
        val authToken = PreferencesManagement.getAuthToken(this)!!
        binding.profilePic.setImageURI(Uri.fromFile(path))
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
            "updatePhoto: ${JavaUtils.profileImagePrepareFilePart1(path.absolutePath,"image")} \n ${
                Gson().toJson(authMap)
            }"
        )
        authViewModel.updateProfilePic(
            authMap,
            JavaUtils.profileImagePrepareFilePart1(path.absolutePath,"image")
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