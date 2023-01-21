package com.oss.abraakadabraaapp.datasource

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.home.NewReceiverFragment
import com.oss.abraakadabraaapp.retrofit.api.APIs

class ProductsViewModelFactory(val page:Int,
    private val api: APIs,val headers:Map<String,String>,
                               val maxDistange:Int,
                               val lat: Double,
                               val long:Double
) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductsViewModel(page,api,headers,maxDistange,lat,long) as T
    }
}