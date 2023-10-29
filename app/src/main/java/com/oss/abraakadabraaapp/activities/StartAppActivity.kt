package com.oss.abraakadabraaapp.activities

import DataClass
import UsersUpdateData
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.App
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.auth.AuthUserDetailActivity
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.databinding.ActivityStartAppBinding
import com.oss.abraakadabraaapp.databinding.SplashContentBinding
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.Locale
import java.util.regex.Pattern


class StartAppActivity : BaseActivity(),LocationListener  {

    private lateinit var binding: ActivityStartAppBinding
    private lateinit var contentBinding: SplashContentBinding
    private val authViewModel: AuthViewModel by viewModel()

    private val mainViewModel: MainViewModel by viewModel()
    lateinit var mAuth: FirebaseAuth
    private lateinit var referrerClient: InstallReferrerClient
    private lateinit var mTracker: Tracker
    private val TAG = "StartAppActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding = ActivityStartAppBinding.inflate(layoutInflater)
        contentBinding = binding.splashContent
        val view = binding.root
        setContentView(view)

        window.statusBarColor =
            ContextCompat.getColor(
                this@StartAppActivity,
                R.color.blue_status_bar_color
            )

        if (PreferencesManagement.isFistOpen(this)){
            installReferrer()
        }
        setUpObserver() // Old Code
        // startApp() //New Code
    }

    private fun installReferrer() {
        val application = application as App
        mTracker = application.defaultTracker

        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        try {
                            val response = referrerClient.installReferrer
                            val referrerUrl = response.installReferrer
//                            showToast(referrerUrl)

                            Log.e(TAG, "onInstallReferrerSetupFinished: $referrerUrl")

                            // Pass the referrer URL to Google Analytics
                            mTracker.send(
                                HitBuilders.EventBuilder()
                                .setCampaignParamsFromUrl(referrerUrl)
                                .setCategory("install")
                                .setAction("app install")
                                .build())

                            referrerClient.endConnection()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                        Log.e(TAG, "onInstallReferrerSetupFinished: SERVICE_UNAVAILABLE")
                        showToast("SERVICE_UNAVAILABLE")
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                        showToast("FEATURE_NOT_SUPPORTED")
                        Log.e(TAG, "onInstallReferrerSetupFinished: FEATURE_NOT_SUPPORTED")
                    }
                    // Handle other response codes as needed
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Try to restart the connection if needed
            }
        })
    }

    private fun setUpObserver() {
        mainViewModel.addressSuccess.observe(this) {
            val data = it.results[0]
            val fullAddress = data.formattedAddress

            Log.e("location_debug", "Location from maps sdk : $fullAddress")

            var area = ""
            var short_name = ""
            var state = ""
            var city = ""
            var locality = ""

            val addressComponents = it.results[0].addressComponents

            for (item in addressComponents) {
                for (i in item.types) {
                    when (i) {
                        "long_name" -> area = item.longName
                        "short_name" -> short_name = item.shortName
                        "administrative_area_level_1" -> state = item.longName
                        "administrative_area_level_2" -> city = item.longName
                        "sublocality" -> locality = item.longName
                    }
                }
            }

            val p = Pattern.compile("[^a-zA-Z ]", Pattern.CASE_INSENSITIVE)
            var final_str = ""
            Log.e("location_debug", "address $fullAddress")
            val arr = fullAddress.split(",")
            for(i in 0..arr.size-2){
                var s_str_arr = arr[i].trim().split(" ")
                var s_str = ""
                for(element in s_str_arr){
                    if(!p.matcher(element).find()){
                        s_str = "$s_str$element "
                    }
                }
                if(s_str.trim() != ""){
                    final_str = "$final_str$s_str,"
                }
            }
            Log.e("location_debug",final_str.dropLast(1))
            PreferencesManagement.saveUserLocation(
                this,
                UserLocation(
                    lat = lat,
                    long = lng,
                    final_str.dropLast(1) ,
                )
            )
        }

        authViewModel.getUserSuccess.observe(this) {
            Log.d("LOGIN>>>", "Getuser: ${Gson().toJson(it)}")

            PreferencesManagement.saveUserInfo(this, it)

            if (it.responseMessage != null) {
                if (it.responseMessage == Constants.USER_NOT_FOUND) {
//                    postFCMtoken()
                    FirebaseAuth.getInstance().currentUser?.phoneNumber
                    val intent =
                        Intent(this, AuthUserDetailActivity::class.java)
                    intent.putExtra(
                        Constants.phoneNumber,
                        FirebaseAuth.getInstance().currentUser?.phoneNumber
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()

                }
            } else {
                if (mAuth.currentUser != null) {
                    generateAuthToken()
                    FirebaseMessaging.getInstance().token.addOnSuccessListener {
                        PreferencesManagement.saveFCMToken(this, it)
                        val data = UsersUpdateData(
                            fcmToken = PreferencesManagement.getFCMToken(this)!!
                        )
                        val dataClass = DataClass(data)

                        val map = java.util.HashMap<String, String>()
                        val token = PreferencesManagement.getAuthToken(this)!!
                        map[RequestKeys.authorization] = token

                        authViewModel.updateUser(map, dataClass)

                    }.addOnFailureListener {
                        loader(false)
                        if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                            "Error Please try again !"
                        )
                    }
                }
            }
        }
        authViewModel.updateUserSuccess.observe(this) {

        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()

    }

    private fun checkPermissions() {
        val listener = object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                initLocation()
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }

        val permissions =
            if (Build.VERSION.SDK_INT <= 29) {
                arrayListOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            }

        Dexter.withContext(this)
            .withPermissions(permissions)
            .withListener(listener)
            .check()
    }

    private fun initLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                getLocation()
            } else {
                startApp()
            }
        } else {
            startApp()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
            val location: Location? = task.result
            if (location == null) {
                requestNewLocationData()
            } else {
                lat = location.latitude.toString()
                lng = location.longitude.toString()
                Log.e("location_debug", "Latitude and longitude values :$lat , $lng")
                startAppSaveLocation()
            }
        }

    }

    private fun requestNewLocationData() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locationRequest = LocationRequest.create().apply {
            interval = 10000L
            fastestInterval = 0
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
            numUpdates = 1
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            if (location != null) {
                lat = location.latitude.toString()
                lng = location.longitude.toString()
                Log.e("location_debug", "Latitude and longitude values :$lat , $lng")
                startAppSaveLocation()
            }
        }
    }

    private fun startAppSaveLocation() {

        val gcd = Geocoder(this, Locale.ENGLISH)
        val latD = lat //String.format("%.6f", lat.toDouble())
        val lngD = lng //String.format("%.6f", lng.toDouble())
        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(latD.toDouble(), lngD.toDouble(), 1)
        } catch (e: IOException) {
            Log.d("MYT", "e ${e.localizedMessage}")
        }

        if (addresses != null && addresses.isNotEmpty()) {

            val tag = "$latD,$lngD"
            Log.e("location_debug", "startAppSaveLocation: $tag")
            val url =
                ApiConstants.geocodeUrl + "json?latlng=" + tag + "&language=en&sensor=true&key=" + BuildConfig.API_KEY
//            mainViewModel.getAddress(url)

            val locality = addresses[0].subLocality ?: ""
            val city = addresses[0].locality ?: ""
            val state = addresses[0].adminArea ?: ""
            val country = addresses[0].countryName ?: ""
            val pinCode = addresses[0].postalCode ?: ""
            val fullAddress = addresses[0].getAddressLine(0) ?: ""

            val featurename = addresses[0].featureName ?: ""
            val subloc = addresses[0].subLocality ?: ""
            val loca = addresses[0].locality ?: ""

            var f_address = ""
            if(featurename != ""){
                f_address = "$f_address$featurename,"
            }
            if(subloc != ""){
                f_address = "$f_address$subloc,"
            }
            if (loca != ""){
                f_address = "$f_address$loca,"
            }
            Log.e("location_update", "${f_address}")
            Log.e("location_update", "${Gson().toJson(addresses)}")

            Log.d("location_update", "$locality\n $city\n $state\n $country\n $pinCode\n $fullAddress")

            val address = "$locality, $city, $state"

            Log.d("Addresses", "address $address")

            val p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE)


            Log.d("Addresses", "address $fullAddress")

            Log.e("location_debug", "Start activity Location $locality without net: $fullAddress")
            var arr = fullAddress.split(state)
            var comArr = arr[0].split(",")
            var finalStr = ""
            if (comArr.size > 2) {
                for(i in 0..comArr.size-2){
                    var s_str_arr = comArr[i].trim().split(" ")
                    var s_str = ""
                    for(element in s_str_arr){
                        if(!p.matcher(element).find()){
                            s_str = "$s_str$element "
                        }
                    }
                    if(s_str.trim() != ""){
                        finalStr = "$finalStr$s_str,"
                    }
                }
            }
            Log.d("location_debug", "Final String ${finalStr.dropLast(1)}")
            if (f_address.dropLast(1).trim() != ""){
                PreferencesManagement.saveUserLocation(
                    this,
                    UserLocation(
                        lat = lat,
                        long = lng,
                        f_address.dropLast(1) ,
                    )
                )
            }else{
                PreferencesManagement.saveUserLocation(
                    this,
                    UserLocation(
                        lat = lat,
                        long = lng,
                        address,
                    )
                )
            }


            startApp()

        } else {
            startApp()
//            getLocationAddress()
        }
    }

    private fun startApp() {

        Handler(Looper.getMainLooper()).postDelayed({
//            if (PreferencesManagement.getUserData(this) != null) {

            if (mAuth.currentUser != null) {
                //get user
                val mUser = FirebaseAuth.getInstance().currentUser
                mUser!!.getIdToken(true)
                    .addOnCompleteListener(object : OnCompleteListener<GetTokenResult?>,
                        com.google.android.gms.tasks.OnCompleteListener<GetTokenResult> {
                        override fun onComplete(task: Task<GetTokenResult?>) {
                            loader(false)
                            if (task.isSuccessful()) {
                                val idToken: String = task.getResult().getToken()!!
                            } else {
                                // Handle error -> task.getException();
                            }
                        }

                        override fun onComplete(task: com.google.android.gms.tasks.Task<GetTokenResult>) {
                            if (task.isSuccessful()) {
                                loader(false)
                                val idToken: String = task.getResult().getToken()!!
                                val auth = "Bearer " + idToken
                                val map = HashMap<String, String>()

                                map[RequestKeys.authorization] = auth

                                // authViewModel.getUser(map)

                                /*val clipboard =
                                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(android.R.attr.label.toString(), idToken)
                                clipboard.setPrimaryClip(clip)*/

                                Log.d(
                                    "TAG", "onComplete11: " +
                                            "${
                                                PreferencesManagement.saveAuthToken(
                                                    this@StartAppActivity,
                                                    auth
                                                )
                                            }"
                                )
                                // Send token to your backend via HTTPS
                                // ...
                            } else {
                                // Handle error -> task.getException();
                            }
                        }

                    })
                generateAuthToken()
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    PreferencesManagement.saveFCMToken(this, it)
                    val data = UsersUpdateData(
                        fcmToken = PreferencesManagement.getFCMToken(this)!!
                    )
                    val dataClass = DataClass(data)

                    val map = HashMap<String, String>()
                    val token = PreferencesManagement.getAuthToken(this)!!
                    map[RequestKeys.authorization] = token
                    Log.d(
                        "TAG",
                        "Token in Accounts fragment: ${JSONObject(Gson().toJson(dataClass))}"
                    )
                    authViewModel.updateUser(map, dataClass)

                }.addOnFailureListener {
                    loader(false)
                    if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                        "Error Please try again !"
                    )
                }

                if(PreferencesManagement.getUserInfo(this@StartAppActivity) != null){
                    val u_info = PreferencesManagement.getUserInfo(this@StartAppActivity)

                    if ((u_info?.data?.name == null || u_info.data?.name == "") ||
                        (u_info.data?.email == "" || u_info.data?.email == null)
                    ) {
                        val intent = Intent(this@StartAppActivity, AuthUserDetailActivity::class.java)
                        intent.putExtra(
                            Constants.phoneNumber,
                            FirebaseAuth.getInstance().currentUser?.phoneNumber
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)

                    }
                    else {
                        startActivity(Intent(this@StartAppActivity, NewHomeActivity::class.java))
                    }
                }
            } else {

                if (PreferencesManagement.isFistOpen(this)) {
                    startActivity(Intent(this@StartAppActivity, OnBoardingActivity::class.java))

                } else {
                    startActivity(Intent(this@StartAppActivity, LoginActivity::class.java))
                }
            }

            finish()
        }, if (BuildConfig.DEBUG) 0 else 2000)
//        }, 2000)
    }

    /*private fun getLocationAddress() {
        if (BuildConfig.DEBUG) {
            showToast("lat $lat,long $lng")
        }

        val apiKey = getString(R.string.akd)
        if (apiKey.isEmpty()) {
            return
        }

        val tag = "$lat,$lng"
        val url =
            ApiConstants.geocodeUrl + "json?latlng=" + tag + "&language=en&sensor=true&key=" + apiKey
        mainViewModel.getAddress(url)
    }*/
}