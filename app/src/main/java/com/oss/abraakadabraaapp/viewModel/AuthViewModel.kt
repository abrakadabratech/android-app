package com.oss.abraakadabraaapp.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import com.oss.abraakadabraaapp.response.authResponse.*
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.response.commonResponse.HttpErrorResponse
import com.oss.abraakadabraaapp.retrofit.api.CallHelper
import com.oss.abraakadabraaapp.retrofit.api.callApi
import com.oss.abraakadabraaapp.retrofit.repository.AuthRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var errorMessage = MutableLiveData("")
    var isLoading = MutableLiveData(false)
    var unAuthorization = MutableLiveData(false)

    var loginWithPhoneNumberSuccess = MutableLiveData<SignInResponse>()

    fun loginWithPhoneNumber(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.loginWithPhoneNumber(map)

            callApi(::call, object : CallHelper<SignInResponse> {
                override fun onSuccessful(data: SignInResponse) {
                    loginWithPhoneNumberSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var changePasswordSuccess = MutableLiveData<CommonResponse>()

    fun changePassword(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.changePassword(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    changePasswordSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var getUserProfileSuccess = MutableLiveData<GetUserProfile>()

    fun getUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.getUserProfile(headerMap, map)

            callApi(::call, object : CallHelper<GetUserProfile> {
                override fun onSuccessful(data: GetUserProfile) {
                    getUserProfileSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                    if(errorResponse.code != null && errorResponse.code == 422){
                        unAuthorization.value = true
                    }
                }
            })

            isLoading.value = false

        }
    }

    var updateProfileImageSuccess = MutableLiveData<CommonResponse>()

    fun updateProfileImage(
        headerMap: HashMap<String, String>,
        map: HashMap<String, RequestBody>,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateProfileImage(headerMap, map,file)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    updateProfileImageSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var updateUserProfileSuccess = MutableLiveData<CommonResponse>()

    fun updateUserProfile(
        headerMap: HashMap<String, String>,
        map: HashMap<String, String>
    ) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.updateUserProfile(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    updateUserProfileSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var logoutSuccess = MutableLiveData<CommonResponse>()

    fun logout(headerMap: HashMap<String, String>, map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.logout(headerMap, map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    logoutSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var resetPasswordSuccess = MutableLiveData<CommonResponse>()

    fun resetPassword(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.resetPassword(map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    resetPasswordSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var signInSuccess = MutableLiveData<SignInResponse>()

    fun signIn(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.signIn(map)

            callApi(::call, object : CallHelper<SignInResponse> {
                override fun onSuccessful(data: SignInResponse) {
                    signInSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = "Status = ${errorResponse.status} Message = ${errorResponse.responseMessage} Code = ${errorResponse.code}"

                    FirebaseCrashlytics.getInstance().log("${errorMessage.value}")

                }
            })

            isLoading.value = false

        }
    }

    var signUpSuccess = MutableLiveData<SignUpResponse>()

    fun signUp(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.signUp(map)

            callApi(::call, object : CallHelper<SignUpResponse> {
                override fun onSuccessful(data: SignUpResponse) {
                    signUpSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var otpVerificationSuccess = MutableLiveData<CommonResponse>()

    fun otpVerification(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.otpVerification(map)

            callApi(::call, object : CallHelper<CommonResponse> {
                override fun onSuccessful(data: CommonResponse) {
                    otpVerificationSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

    var sendOtpSuccess = MutableLiveData<SendOtpResponse>()

    fun sendOtp(map: HashMap<String, String>) {
        viewModelScope.launch {

            isLoading.value = true

            suspend fun call() = repository.sendOtp(map)

            callApi(::call, object : CallHelper<SendOtpResponse> {
                override fun onSuccessful(data: SendOtpResponse) {
                    sendOtpSuccess.value = data
                }

                override fun onError(errorResponse: HttpErrorResponse) {
                    errorMessage.value = errorResponse.responseMessage
                }
            })

            isLoading.value = false

        }
    }

}