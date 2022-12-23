package com.oss.abraakadabraaapp.di.module

import com.oss.abraakadabraaapp.retrofit.repository.AuthRepository
import com.oss.abraakadabraaapp.retrofit.repository.MainRepository
import org.koin.dsl.module


val repoModule = module {
    single {
        MainRepository(get())
    }
    single {
        AuthRepository(get())
    }
}