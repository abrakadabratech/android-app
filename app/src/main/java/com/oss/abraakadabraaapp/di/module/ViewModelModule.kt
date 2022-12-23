package com.oss.abraakadabraaapp.di.module

import com.oss.abraakadabraaapp.viewModel.*
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { LocationViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { ContentManagementViewModel(get()) }
    viewModel { NotificationViewModel(get()) }
}