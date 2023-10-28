package com.oss.abraakadabraaapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.dialog.ProgressDialog
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.retrofit.utils.ApiConstants
import com.oss.abraakadabraaapp.retrofit.utils.NetworkHelper
import com.oss.abraakadabraaapp.utils.AppSignatureHashHelper
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*
import java.util.regex.Pattern


abstract class BaseActivity : AppCompatActivity(),LocationListener {
    override fun onLocationChanged(p0: Location) {
        TODO("Not yet implemented")
    }

    private lateinit var progressDialog: ProgressDialog

    private val locationViewModel: LocationViewModel by viewModel()

    private val networkHelper: NetworkHelper by inject()

    lateinit var lat: String
    lateinit var lng: String

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mainViewModel: MainViewModel by viewModel()

    lateinit var smsToken: String

    private var sAnalytics: GoogleAnalytics? = null
    private var sTracker: Tracker? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    @get:Synchronized
    val defaultTracker: Tracker?
        get() {
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            if (sTracker == null) {
                sTracker = sAnalytics?.newTracker(R.xml.global_tracker)
            }
            return sTracker
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sAnalytics = GoogleAnalytics.getInstance(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        progressDialog = ProgressDialog(this)

        val appSignatureHashHelper = AppSignatureHashHelper(this)
        smsToken = appSignatureHashHelper.appSignatures[0]

        setUpObserver()
    }

    public fun postClick(event_tag: String){
        val bundle = Bundle()
        bundle.putString(event_tag, "1")
        firebaseAnalytics.logEvent(event_tag, bundle)
        debugLog(event_tag)

    }

    public fun postEvent(event_tag: String, bundle: Bundle?) {
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics.setCurrentScreen(this,event_tag,null)
//        val bundle = Bundle()
//        bundle.putString(FirebaseAnalytics.Param.METHOD, "Test method")

        firebaseAnalytics.logEvent(event_tag, bundle)

        //init analytics
        /*  mTracker = application.defaultTracker
          mTracker!!.setScreenName(event_tag)
          mTracker!!.send(HitBuilders.ScreenViewBuilder().build())*/
        debugLog(event_tag)

    }

    override fun onStart() {
        super.onStart()
        //startLocationUpdates()
    }

    private fun startLocationUpdates() {
        when {
            isLocationEnabled() -> observeLocationUpdates()
            isPermissionGranted() -> observeLocationUpdates()
            else -> askLocationPermission()
        }
    }

    private fun observeLocationUpdates() {
        locationViewModel.getLocationData.observe(this) {
            val latitude = it.latitude.toString()
            val longitude = it.longitude.toString()

            Log.d("getLocationData", "latitude $latitude")
//            Log.d("getLocationData", "longitude $longitude")
//            saveLocation()

           /* val tag = "$latitude,$longitude"
            val url =
                ApiConstants.geocodeUrl + "json?latlng=" + tag + "&language=en&sensor=true&key=" + resources.getString(
                    R.string.akd
                )
            mainViewModel.getAddress(url)*/

            /*if (PreferencesManagement.getUserLocation(this@BaseActivity) != null) {
                val userLocation = PreferencesManagement.getUserLocation(this@BaseActivity)

                PreferencesManagement.saveUserLocation(
                    this@BaseActivity,
                    UserLocation(
                        latitude, longitude, getAddress(latitude.toDouble(),longitude.toDouble()),
                    )
                )
            } else {
                PreferencesManagement.saveUserLocation(
                    this@BaseActivity,
                    UserLocation(
                        latitude, longitude, getAddress(latitude.toDouble(),longitude.toDouble()),
                    )
                )
            }*/

        }
    }
    fun getAddress(lat: Double, lng: Double) :String{
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val obj = addresses!![0]
            var add = obj.getAddressLine(0)
            var string = ""
            if(obj.subLocality != null){
                string = "${obj.subLocality},${obj.locality}"
            }else{
                string = obj.locality+","+obj.adminArea
            }
            return add

        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        return ""
    }

    private fun setUpObserver() {

        mainViewModel.addressSuccess.observe(this) {
            val data = it.results[0]
            val fullAddress = data.formattedAddress

            Log.e("location_debug", "Location from maps sdk : $fullAddress" )

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
    }


    fun getLastLocation() {
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

                saveLocation()
            }
        }

    }

    fun saveLocation() {

        val latD = lat //String.format("%.6f", lat.toDouble())
        val lngD = lng //String.format("%.6f", lng.toDouble())
        val tag = "$latD,$lngD"

        Log.d("LocationCall", "getLastLocation: Called")
        val gcd = Geocoder(this, Locale.ENGLISH)
        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(latD.toDouble(), lngD.toDouble(), 1)
        } catch (e: IOException) {
            Log.d("MYT", "e ${e.localizedMessage}")
        }

        if (addresses != null && addresses.isNotEmpty()) {

            val locality = addresses[0].subLocality ?: ""
            val city = addresses[0].locality ?: ""
            val state = addresses[0].adminArea ?: ""
            val country = addresses[0].countryName ?: ""
            val pinCode = addresses[0].postalCode ?: ""

            val fullAddress = addresses[0].getAddressLine(0) ?: ""
            Log.e("location_update", "${Gson().toJson(addresses)}", )

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
            Log.e("location_update", "${f_address}", )

            Log.d("location_update", "$locality\n $city\n $state\n $country\n $pinCode\n $fullAddress")


            val address = "$locality, $city, $state"
            val p = Pattern.compile("[^a-zA-Z]", Pattern.CASE_INSENSITIVE)


            Log.e("location_debug", "Base activity Location $locality without net: $fullAddress")
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
        } else {
            getLocationAddress()
        }
    }

    fun getLocationAddress() {

        if (BuildConfig.DEBUG) {
            showToast("lat $lat,long $lng")
        }
        val tag = "$lat,$lng"
        val url =
            ApiConstants.geocodeUrl + "json?latlng=" + tag + "&language=en&sensor=true&key=" +
                BuildConfig.API_KEY

        mainViewModel.getAddress(url)
    }


    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000L
            fastestInterval = 0
            priority = Priority.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
            numUpdates = 1
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        mFusedLocationClient.requestLocationUpdates(
            locationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location = locationResult.lastLocation
            Log.d("LocationCall", "onLocationResult: $location")
            if (location != null) {
                lat = location.latitude.toString()
                lng = location.longitude.toString()
                saveLocation()
            }
        }
    }

    fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //TODO
            }
        }

    fun askLocationPermission() {}

    fun showPermanentlyDeniedDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.title_permission_permanently_denied))
        dialog.setMessage(getString(R.string.message_permission_permanently_denied))
        dialog.setNegativeButton(getString(R.string.not_now)) { _, _ -> }
        dialog.setPositiveButton(getString(R.string.settings)) { _, _ ->
            locationPermissionIntent()
        }
        dialog.setOnCancelListener { } //important
        dialog.show()
    }

    fun requestCameraPermissions() {
        requestMultiplePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
            resultsMap.forEach {
                Log.d("MYT", "Permission: ${it.key}, granted: ${it.value}")
            }
        }

    fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    fun hasContactPermission() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun backToLogIn() {
        PreferencesManagement.saveUserData(this, null)

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("EXIT", true)
        startActivity(intent)
    }

    /*fun loginInUser(userData: UserData) {
        PreferencesManagement.saveUserData(this, userData)
        startActivity(HomeActivity.createIntent(this))
    }*/

    fun showPermissionSnackBar(view: View) {
        val snack = Snackbar.make(
            view,
            "Permission required to get your location",
            Snackbar.LENGTH_LONG
        )
        snack.setAction("Give Permission") {
            locationPermissionIntent()
        }
        snack.show()
    }

    private fun locationPermissionIntent() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    fun showGpsEnableSnackBar(view: View) {
        val snack = Snackbar.make(
            view,
            "Please Enable GPS",
            Snackbar.LENGTH_LONG
        )
        snack.setAction("Enable") {
            gpsIntent()
        }
        snack.show()
    }

    fun gpsIntent() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    fun isPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun loader(show: Boolean) {
        if (show) progressDialog.showDialog()
        else progressDialog.hideDialog()
    }

    fun isNetworkAvailable() = networkHelper.isNetworkConnected()

    fun showSnackBar(view: View, message: String) {
        hideSoftKeyboard()
        val snackBar = Snackbar.make(
            view, message,
            Snackbar.LENGTH_LONG

        )
        snackBar.show()
    }

    fun showToast(message: String) {
        if (message.isNotBlank()) Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun hideSoftKeyboard() {
        val inputMethodManager = getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken, 0
        )
    }

    fun debugLog(eventTag: String) {
        Log.d("GA4", "debugLog: $eventTag")
    }

    fun generateAuthToken():String{
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val idToken = it.result.token
                    val auth = "Bearer $idToken"

                   /* val clipboard =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(android.R.attr.label.toString(), idToken)
                    clipboard.setPrimaryClip(clip)*/
                    if(PreferencesManagement.saveAuthToken(this@BaseActivity,auth))
                    {
                        Log.d("akd_debug", "generateAuthToken: Data saved in preferences.")
                    }
                }
            }

        /*mUser!!.getIdToken(true)
            .addOnCompleteListener(object : OnCompleteListener<GetTokenResult?>,
                com.google.android.gms.tasks.OnCompleteListener<GetTokenResult> {
                override fun onComplete(task: Task<GetTokenResult?>) {
                }

                override fun onComplete(task: com.google.android.gms.tasks.Task<GetTokenResult>) {
                    if (task.isSuccessful) {
                        val idToken: String = task.getResult().getToken()!!
                        val auth = "Bearer "+idToken

                        val clipboard =
                            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(android.R.attr.label.toString(), idToken)
                        clipboard.setPrimaryClip(clip)

                    } else {

                    }
                }

            })*/

        return "authToken"
    }

    fun getFCMToken() {

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            PreferencesManagement.saveFCMToken(this,it)
        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }
}


