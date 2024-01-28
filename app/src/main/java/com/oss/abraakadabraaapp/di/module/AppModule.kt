package com.oss.abraakadabraaapp.di.module

import android.content.Context
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.retrofit.interceptor.NetworkConnectionInterceptor
import com.oss.abraakadabraaapp.retrofit.utils.NetworkHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.annotation.SuppressLint
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.retrofit.api.TokenAutheticator
import com.oss.abraakadabraaapp.retrofit.api.TokenInterceptor
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.security.cert.CertificateException

val appModule = module {
    single { provideOkHttpClient(androidContext()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
    single { provideNetworkHelper(androidContext()) }
    single { provideTokenInterceptor() }
    single { provideTokenAuthenticator(androidContext()) }
}

val trustAllCerts = arrayOf<TrustManager>(
    @SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            chain: Array<X509Certificate?>?,
            authType: String?
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            chain: Array<X509Certificate?>?,
            authType: String?
        ) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate?>? {
            return arrayOf()
        }
    }
)

private fun provideNetworkHelper(context: Context) = NetworkHelper(context)

private fun provideOkHttpClient(context: Context) = if (BuildConfig.DEBUG) {

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    val trustManagerFactory: TrustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers: Array<TrustManager> =
        trustManagerFactory.trustManagers
    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
        "Unexpected default trust managers:" + trustManagers.contentToString()
    }

    val trustManager =
        trustManagers[0] as X509TrustManager

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .sslSocketFactory(  sslContext.socketFactory, trustManager)
        .hostnameVerifier { _, _ -> true }
        .addInterceptor(loggingInterceptor)
        .addInterceptor(NetworkConnectionInterceptor(context))
        .addInterceptor(TokenInterceptor())
        .authenticator(TokenAutheticator(context))
        .build()
} else {

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())

    val trustManagerFactory: TrustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers: Array<TrustManager> =
        trustManagerFactory.trustManagers
    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
        "Unexpected default trust managers:" + trustManagers.contentToString()
    }

    val trustManager =
        trustManagers[0] as X509TrustManager

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient
        .Builder()
        .sslSocketFactory(  sslContext.socketFactory, trustManager)
        .hostnameVerifier { _, _ -> true }
        .addInterceptor(TokenInterceptor())
        .authenticator(TokenAutheticator(context))
        .addInterceptor(NetworkConnectionInterceptor(context))
        .build()
}

private fun provideRetrofit(
    okHttpClient: OkHttpClient
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(if(BuildConfig.DEBUG) BuildConfig.BASE_URL else BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): APIs = retrofit.create(APIs::class.java)

fun provideTokenInterceptor(): TokenInterceptor = TokenInterceptor()


private fun provideTokenAuthenticator(context: Context): TokenAutheticator = TokenAutheticator(context)

