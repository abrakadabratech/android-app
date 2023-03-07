package com.oss.abraakadabraaapp.activities.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.SocialShareAdapter
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
import com.oss.abraakadabraaapp.utils.Constants.USER_CREATED
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.log

class AuthUserDetailActivity : BaseActivity(),SocialShareAdapter.OnSocialProfileClicked {

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
    lateinit var adapter:SocialShareAdapter
    var from = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthUserDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (PreferencesManagement.getUserInfoFlag(this)!! &&
            PreferencesManagement.getUserProfileFlag(this)!!){
            val intent =
                Intent(this@AuthUserDetailActivity, NewHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.putExtra(Constants.phoneNumber,phoneNumber)
            startActivity(intent)
            finish()
        }

        if(PreferencesManagement.getUserInfoFlag(this)!!) {
            binding.userDetailsLayout.visibility = View.GONE
            binding.socialProfileLayout.visibility = View.VISIBLE
        }else{
            binding.userDetailsLayout.visibility = View.VISIBLE
            binding.socialProfileLayout.visibility = View.GONE
        }

        postEvent(Constants.PAGE_ADD_USER_PROFILE_ONBOARDING,null)

        phoneNumber = intent.getStringExtra(Constants.phoneNumber) ?: "1234567890"

        window.statusBarColor =
            ContextCompat.getColor(
                this@AuthUserDetailActivity,
                R.color.blue_status_bar_color
            )

        setUpObserver()

        with(binding) {

            etName.filterByDataType(1)
            etEmail.filterEmoji()

            letsGetStartedBtn.setOnClickListener {
                hideSoftKeyboard()
                postEvent(BUTTON_LETS_START_USER_PROFILE,null)
                postEvent(PAGE_ADD_SOCIAL_PROFILE_ONBOARDING,null)
            //Old Code
                registerUser()
            }
        }

        binding.okGotItBtn.setOnClickListener {
            postEvent(BUTTON_LETS_START_SOCIAL_PROFILE,null)
            postUserProfile()
        }

        setUpRecyclerView()

        binding.skipTxt.setOnClickListener {
            PreferencesManagement.saveUserProfileFlag(this,true)

            val intent =
                Intent(this@AuthUserDetailActivity, NewHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.putExtra(Constants.phoneNumber,phoneNumber)
            startActivity(intent)
            finish()
        }

        binding.imageView11.setOnClickListener {
            binding.socialProfilePopUPLayout.visibility = View.GONE
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (shouldAllowBack()) {
            super.onBackPressed();
        } else {
//            showToast("Do not press back. Please complete your profile")
        }
    }

    private fun shouldAllowBack(): Boolean {
        return false
    }

    private fun postUserProfile() {
        if (isUserProfileValidate()){
            if (isNetworkAvailable()){
                val mapAuth = HashMap<String,String>()
                /*if (PreferencesManagement.getAuthToken(this@AuthUserDetailActivity) != null){
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }else{
                    generateAuthToken()
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }*/
                mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

                val map = HashMap<String, String>()

                map[RequestKeys.social_link_type] = socialLinkType
                socialLink = binding.socialProfileHeader.text.toString().trim()+
                        binding.profileLink.text.toString().trim()
                map[RequestKeys.social_link] = binding.socialProfileHeader.text.toString().trim()+
                        binding.profileLink.text.toString().trim()

                authViewModel.postUserSocialProfile(mapAuth,map)
            }
        }
    }

    private fun isUserProfileValidate(): Boolean {
        with(binding) {
            if (profileLink.text!!.length <= 3) {
                showToast("Please Enter Valid Profile ID")
                return false
            }

            if (profileLink.text!!.length > 20) {
                showToast("Profile ID must be less than 20 character")
                return false
            }

            return true
        }
    }

    private fun setUpRecyclerView() {
        var images: Array<Int> = arrayOf(
            R.drawable.fb_icon, R.drawable.linked_in_icon, R.drawable.twitter_icon,
            R.drawable.insta_icon
        )
        list.add(SocialData(R.drawable.fb_icon,true))
        list.add(SocialData(R.drawable.insta_icon,false))
        list.add(SocialData(R.drawable.twitter_icon,false))
        list.add(SocialData(R.drawable.linked_in_icon,false))

        adapter = SocialShareAdapter(this, list,this)
        val layoutManager = GridLayoutManager(this, 4)

        binding.rvSocialLinks.layoutManager = layoutManager
        binding.rvSocialLinks.adapter = adapter
    }


    private fun registerUser() {
        if (isValidate()) {
            if (isNetworkAvailable()) {
                if (isLocationEnabled()) getLastLocation()

                val mapAuth = HashMap<String,String>()
               /* if (PreferencesManagement.getAuthToken(this@AuthUserDetailActivity) != null){
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!
                }else{
                    generateAuthToken()
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }*/
                mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

                val map = HashMap<String, String>()
                name=binding.etName.text.toString().trim()
                email=binding.etEmail.text.toString().trim()
                phone=phoneNumber.trim()
                map[RequestKeys.name] = name
                map[RequestKeys.email] = email
                map[RequestKeys.phoneNumber] = phone

                /*if (PreferencesManagement.getUserLocation(this@AuthUserDetailActivity) != null) {
                    val userLocation =
                        PreferencesManagement.getUserLocation(this@AuthUserDetailActivity)!!
                    latitude = userLocation.lat
                    longitude = userLocation.long
                    address = userLocation.address ?: ""
                }

                map[RequestKeys.lat] = latitude
                map[RequestKeys.lng] = longitude
                map[RequestKeys.fullAddress] = address*/
                Log.d(API_TAG, "registerUser: ${Gson().toJson(mapAuth)}")
                authViewModel.postUser(mapAuth,map)

//                getFCMToken(map)

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

        authViewModel.postUserSuccess.observe(this) {
            Log.d(API_TAG, "postUserSuccess: ${Gson().toJson(it)}")

            if (it.responseMessage == USER_CREATED){
                binding.userDetailsLayout.visibility = View.GONE
                binding.socialProfileLayout.visibility = View.VISIBLE
                PreferencesManagement.saveUserFlag(this,true)

                /*if (PreferencesManagement.getAuthToken(this@AuthUserDetailActivity) != null){
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }else{
                    generateAuthToken()
                    mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                }*/

                val mapAuth = HashMap<String,String>()
                mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

                authViewModel.getUserSocialProfile(mapAuth)
            }
//            showToast(it.responseMessage)
//            loginInUser(it.data)
        }
        authViewModel.getSocialProfileSuccess.observe(this){
            Log.d(API_TAG, "getSocialProfileSuccess: ${Gson().toJson(it)}")
        }

        authViewModel.postSocialProfileSuccess.observe(this){
            Log.d(API_TAG, "postSocialProfileSuccess: ${Gson().toJson(it)}")

            showToast("Social link submitted.")
            if (it.code == 200){
                PreferencesManagement.saveUserProfileFlag(this,true)
                startActivity(Intent(applicationContext,NewHomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()
//                successDialog()
            }
//            if (it.responseMessage == "")
        }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
    }

    private fun successDialog(){
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
        when(position){
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