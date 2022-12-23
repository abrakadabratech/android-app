package com.oss.abraakadabraaapp.retrofit.exception

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No internet connection"
}