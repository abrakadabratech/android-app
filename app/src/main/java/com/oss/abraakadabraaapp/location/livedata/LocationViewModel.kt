package com.oss.abraakadabraaapp.location.livedata

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)

    val getLocationData: LiveData<Location> = locationData

}