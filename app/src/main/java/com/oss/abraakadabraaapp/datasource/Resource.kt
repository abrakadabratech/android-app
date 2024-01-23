package com.oss.abraakadabraaapp.datasource

sealed class Resource {
//    val data: GetProducts = GetProducts(200,  Product(),0)
    class Failure : Resource()
    class Loading : Resource()
    class Success : Resource()
}