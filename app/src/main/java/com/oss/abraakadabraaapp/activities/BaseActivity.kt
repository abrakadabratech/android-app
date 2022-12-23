package com.oss.abraakadabraaapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.dialog.ProgressDialog
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.model.UserData
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


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var progressDialog: ProgressDialog

    private val locationViewModel: LocationViewModel by viewModel()

    private val networkHelper: NetworkHelper by inject()

    lateinit var lat: String
    lateinit var lng: String

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mainViewModel: MainViewModel by viewModel()

    lateinit var smsToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        progressDialog = ProgressDialog(this)

        val appSignatureHashHelper = AppSignatureHashHelper(this)
        smsToken = appSignatureHashHelper.appSignatures[0]

        setUpObserver()
    }

    override fun onStart() {
        super.onStart()
//        startLocationUpdates()
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
            Log.d("getLocationData", "longitude $longitude")

            if (PreferencesManagement.getUserLocation(this@BaseActivity) != null) {
                val userLocation = PreferencesManagement.getUserLocation(this@BaseActivity)

                PreferencesManagement.saveUserLocation(
                    this@BaseActivity,
                    UserLocation(
                        latitude, longitude, userLocation?.address ?: "",
                    )
                )
            } else {
                PreferencesManagement.saveUserLocation(
                    this@BaseActivity,
                    UserLocation(
                        latitude, longitude, "",
                    )
                )
            }

        }
    }

    private fun setUpObserver() {

        mainViewModel.addressSuccess.observe(this, {
            val data = it.results[0]
            val fullAddress = data.formattedAddress
            var pinCode = ""
            var country = ""
            var state = ""
            var city = ""
            var locality = ""

            val addressComponents = it.results[0].addressComponents

            for (item in addressComponents) {
                for (i in item.types) {
                    when (i) {
                        "postal_code" -> pinCode = item.longName
                        "country" -> country = item.longName
                        "administrative_area_level_1" -> state = item.longName
                        "administrative_area_level_2" -> city = item.longName
                        "sublocality" -> locality = item.longName
                    }
                }
            }

            val address = "$locality, $city, $state"

            Log.d("Addresses", "address $address")

            PreferencesManagement.saveUserLocation(
                this,
                UserLocation(
                    lat = lat,
                    long = lng,
                    address,
                )
            )

        })
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

        val gcd = Geocoder(this, Locale.ENGLISH)

        var addresses: List<Address>? = null
        try {
            addresses = gcd.getFromLocation(lat.toDouble(), lng.toDouble(), 1)
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

            Log.d("Addresses", "$locality\n $city\n $state\n $country\n $pinCode\n $fullAddress")

            val address = "$locality, $city, $state"

            Log.d("Addresses", "address $address")

            PreferencesManagement.saveUserLocation(
                this,
                UserLocation(
                    lat = lat,
                    long = lng,
                    address,
                )
            )
        } else {
            getLocationAddress()
        }
    }

    private fun getLocationAddress() {
        if (BuildConfig.DEBUG) {
            showToast("lat $lat,long $lng")
        }
        val tag = "$lat,$lng"
        val url =
            ApiConstants.geocodeUrl + "json?latlng=" + tag + "&language=en&sensor=true&key=" + resources.getString(
                R.string.akd
            )
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

    fun loginInUser(userData: UserData) {
        PreferencesManagement.saveUserData(this, userData)
        startActivity(HomeActivity.createIntent(this))
    }

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
}