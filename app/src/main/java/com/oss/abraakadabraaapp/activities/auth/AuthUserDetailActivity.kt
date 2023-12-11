package com.oss.abraakadabraaapp.activities.auth

import DataClass
import Location
import UsersUpdateData
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.SocialShareAdapter
import com.oss.abraakadabraaapp.activities.newflow.apimodels.FCMData
import com.oss.abraakadabraaapp.activities.newflow.apimodels.FcmRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UserStats
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.model.SocialData
import com.oss.abraakadabraaapp.databinding.ActivityAuthUserDetailBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_LETS_START_SOCIAL_PROFILE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_LETS_START_USER_PROFILE
import com.oss.abraakadabraaapp.utils.Constants.PAGE_ADD_SOCIAL_PROFILE_ONBOARDING
import com.oss.abraakadabraaapp.utils.Constants.SIGN_IN_METHOD_GOOGLE
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.log

class AuthUserDetailActivity : BaseActivity(), SocialShareAdapter.OnSocialProfileClicked {

    private lateinit var binding: ActivityAuthUserDetailBinding

    private lateinit var phoneNumber: String

    private val authViewModel: AuthViewModel by viewModel()

    private var latitude = ""
    private var longitude = ""
    private var address = ""
    private var socialLinkType = "facebook"
    private var socialLink = "facebook"
    private var name = ""
    private var email = ""
    private var phone = ""

    var list = arrayListOf<SocialData>()
    lateinit var adapter: SocialShareAdapter
    var from = ""
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthUserDetailBinding.inflate(layoutInflater)
        val view = binding.root
        mAuth = FirebaseAuth.getInstance()
        setContentView(view)

        if (PreferencesManagement.getUserInfoFlag(this)!!/* &&
            PreferencesManagement.getUserProfileFlag(this)!!*/) {
            val intent =
                Intent(this@AuthUserDetailActivity, NewHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.putExtra(Constants.phoneNumber,phoneNumber)
            startActivity(intent)
            finish()
        }else{
            //Get user
            generateAuthToken()
            if (PreferencesManagement.getAuthToken(this) != null){
                val map = java.util.HashMap<String, String>()
                map[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!
                authViewModel.getUser(map)
            }
        }
        postEvent(Constants.PAGE_ADD_USER_PROFILE_ONBOARDING, null)

        phoneNumber = intent.getStringExtra(Constants.phoneNumber) ?: "1234567890"

        window.statusBarColor =
            ContextCompat.getColor(
                this@AuthUserDetailActivity,
                R.color.blue_status_bar_color
            )

        setUpObserver()

        if (PreferencesManagement.getSignInMethod(this) == SIGN_IN_METHOD_GOOGLE){
            binding.etEmail.hint = "Mobile Number"
            binding.skipTxt2.visibility = View.VISIBLE
        }else{
            binding.etEmail.hint = "Email"
            binding.skipTxt2.visibility = View.GONE
        }

        with(binding) {

            etName.filterByDataType(1)
            etEmail.filterEmoji()

            letsGetStartedBtn.setOnClickListener {
                hideSoftKeyboard()
                postEvent(BUTTON_LETS_START_USER_PROFILE, null)
                postEvent(PAGE_ADD_SOCIAL_PROFILE_ONBOARDING, null)
                //Old Code
                registerUser()
            }

            skipTxt2.setOnClickListener {
                goHome()
            }
        }

        binding.okGotItBtn.setOnClickListener {
            postEvent(BUTTON_LETS_START_SOCIAL_PROFILE, null)
        }

        setUpRecyclerView()

        binding.skipTxt.setOnClickListener {
            goHome()
        }

        binding.imageView11.setOnClickListener {
            binding.socialProfilePopUPLayout.visibility = View.GONE
        }

    }

    private fun goHome() {
        PreferencesManagement.saveUserProfileFlag(this, true)

        val intent =
            Intent(this@AuthUserDetailActivity, NewHomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
//            showToast("Do not press back. Please complete your profile")
        }
    }

    private fun shouldAllowBack(): Boolean {
        return false
    }


    private fun setUpRecyclerView() {
        var images: Array<Int> = arrayOf(
            R.drawable.fb_icon, R.drawable.linked_in_icon, R.drawable.twitter_icon,
            R.drawable.insta_icon
        )
        list.add(SocialData(R.drawable.fb_icon, true))
        list.add(SocialData(R.drawable.insta_icon, false))
        list.add(SocialData(R.drawable.twitter_icon, false))
        list.add(SocialData(R.drawable.linked_in_icon, false))

        adapter = SocialShareAdapter(this, list, this)
        val layoutManager = GridLayoutManager(this, 4)

        binding.rvSocialLinks.layoutManager = layoutManager
        binding.rvSocialLinks.adapter = adapter
    }


    private fun registerUser() {
        if (isValidate()) {
            if (isNetworkAvailable()) {
                if (isLocationEnabled()) getLastLocation()

                val mapAuth = HashMap<String, String>()
                mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

                val body = HashMap<String,String>()
                body["name"] = binding.etName.text.toString()
                if (PreferencesManagement.getSignInMethod(this) == SIGN_IN_METHOD_GOOGLE){
                    body["phone"] = binding.etEmail.text.toString()
                }else{
                    body["email"] = binding.etEmail.text.toString()
                }
                body["signin_method"] = PreferencesManagement.getSignInMethod(this)

                generateAuthToken()
                val map = java.util.HashMap<String, String>()
                val token = PreferencesManagement.getAuthToken(this)!!
                map[RequestKeys.authorization] = token

                authViewModel.onBoardUser(map,body)

            } else {
                showSnackBar(
                    binding.clAuthUserDetails,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }

        authViewModel.onBoardingResponse.observe(this){
            if (it.code == 200){
                val map = java.util.HashMap<String, String>()
                map[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!
                authViewModel.getUser(map)
            }
        }
        authViewModel.getUserSuccess.observe(this) {
            PreferencesManagement.saveUserInfo(this, it)
            val u_info = PreferencesManagement.getUserInfo(this@AuthUserDetailActivity)!!
            if ((u_info.data?.name == null || u_info.data?.name == "") ||
                (u_info.data?.email == "" || u_info.data?.email == null)
            ) {
                if (PreferencesManagement.getUserInfoFlag(this)!!) {
                    startActivity(Intent(this@AuthUserDetailActivity, NewHomeActivity::class.java))
                    finish()
                } else {
                    binding.userDetailsLayout.visibility = View.VISIBLE
                    binding.etName.setText(PreferencesManagement.getUserName(this).toString())
                    binding.etEmail.setText(PreferencesManagement.getUserEmail(this).toString())
                }

            }
            else {
                startActivity(Intent(this@AuthUserDetailActivity, NewHomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()
            }
            if (mAuth.currentUser != null) {

                generateAuthToken()
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    PreferencesManagement.saveFCMToken(this, it)
                    val data = FcmRequest(
                        data = FCMData(
                            fcmToken = PreferencesManagement.getFCMToken(this)!!
                        )
                    )

                    val map = java.util.HashMap<String, String>()
                    val token = PreferencesManagement.getAuthToken(this)!!
                    map[RequestKeys.authorization] = token

                    authViewModel.postFCMToken(map, data)

                }.addOnFailureListener {
                    loader(false)
                    if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                        "Error Please try again !"
                    )
                }
            }
        }

        authViewModel.updateUserSuccess.observe(this) {
            Log.d(API_TAG, "postUserSuccess: ${Gson().toJson(it)}")

            if (it.code == 200 || it.code == 201) {
                PreferencesManagement.saveUserFlag(this, true)
                generateAuthToken()
                val map = java.util.HashMap<String, String>()
                map[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!
                authViewModel.getUser(map)

                if (mAuth.currentUser != null) {
                    generateAuthToken()
                    FirebaseMessaging.getInstance().token.addOnSuccessListener {
                        PreferencesManagement.saveFCMToken(this, it)
                        val data = FcmRequest(
                            data = FCMData(
                                fcmToken = PreferencesManagement.getFCMToken(this)!!
                            )
                        )

                        val map = java.util.HashMap<String, String>()
                        val token = PreferencesManagement.getAuthToken(this)!!
                        map[RequestKeys.authorization] = token

                        authViewModel.postFCMToken(map, data)

                    }.addOnFailureListener {
                        loader(false)
                        if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                            "Error Please try again !"
                        )
                    }
                }

            }
//            showToast(it.responseMessage)
//            loginInUser(it.data)
        }
        authViewModel.getSocialProfileSuccess.observe(this) {
            Log.d(API_TAG, "getSocialProfileSuccess: ${Gson().toJson(it)}")
        }

        authViewModel.postSocialProfileSuccess.observe(this) {
            Log.d(API_TAG, "postSocialProfileSuccess: ${Gson().toJson(it)}")

            showToast("Social link submitted.")
            if (it.code == 200) {
                PreferencesManagement.saveUserProfileFlag(this, true)
                startActivity(
                    Intent(applicationContext, NewHomeActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                finish()
            }
        }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
    }

    private fun successDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_submit_success_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.success_ok_btn)
        alertName.setOnClickListener {

        }

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
            val ms: Matcher = ps.matcher(etName.text.toString().trim())

            val emailPattern: Pattern = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
            val emailMs: Matcher = emailPattern.matcher(etEmail.text.toString().trim())

            if (etName.text!!.length <= 3) {
//                etName.error = "Name should be minimum 4 characters"
                showToast("Please Enter Valid Full Name")
                return false
            }

            if (etName.text!!.length > 25) {
                showToast("Full Name must be less than 50 character")
                return false
            }

            if (!ms.matches()) {
                showToast("Full Name does not contain any special character or numbers")
                return false
            }

            if (!emailMs.matches()) {
                showToast("Please Enter Valid Email Address")
                return false
            }

            return true
        }
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
        binding.socialProfilePopUPLayout.visibility = View.VISIBLE
    }

}