package com.oss.abraakadabraaapp.activities.auth

import DataClass
import Location
import UsersUpdateData
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.TermsAndConditionsActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.CreateUserRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.FCMData
import com.oss.abraakadabraaapp.activities.newflow.apimodels.FcmRequest
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.databinding.ActiviyLogin2Binding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.SIGN_IN_METHOD_GOOGLE
import com.oss.abraakadabraaapp.utils.Constants.USER_NOT_FOUND
import com.oss.abraakadabraaapp.utils.DateTimeUtils
import com.oss.abraakadabraaapp.utils.GenericKeyEvent
import com.oss.abraakadabraaapp.utils.GenericTextWatcher
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.ValidatorUtils
import com.oss.abraakadabraaapp.utils.customView.CustomTypefaceSpan
import com.oss.abraakadabraaapp.utils.makeLinks
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import io.reactivex.rxjava3.annotations.NonNull
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity() {

    private var SIGNIN_METHOD = "phone"
    private lateinit var countDownTimer: CountDownTimer
    private var isOtpSend = false
    private var editMode = false
    private var verificationId: String? = null

    private lateinit var phoneNumber: String

    private lateinit var binding: ActiviyLogin2Binding

    private val authViewModel: AuthViewModel by viewModel()
    lateinit var alertDialog: AlertDialog

    private var latitude = ""
    private var longitude = ""
    var onBack = false
    private var address = ""
//    private val authToken by lazy { PreferencesManagement.getAuthToken(this)!! }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {

            val map = HashMap<String, String>()
            map[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
//            authViewModel.getUser(map)

            /*val intent =
                Intent(this@LoginActivity, NewHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {

                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

            Log.w("TAG_Google_sigxnin", "signInResult:failed code=" + Gson().toJson(account))


            // Signed in successfully, show authenticated UI.

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(@NonNull task: Task<AuthResult?>) {
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG_Google_sigxnin", "signInWithCredential:success")
                            val user = task.result?.user
                            user?.getIdToken(true)?.addOnCompleteListener { idTokenTask ->
                                if (idTokenTask.isSuccessful) {
                                    val idToken = idTokenTask.result?.token
                                    // Send this token to the server
                                    val clipboard =
                                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(
                                        android.R.attr.label.toString(),
                                        idToken
                                    )
//                                    clipboard.setPrimaryClip(clip)
                                    if (PreferencesManagement.saveAuthToken(
                                            this@LoginActivity,
                                            "Bearer $idToken"
                                        )
                                    ) {
                                        Log.d(
                                            "akd_debug",
                                            "generateAuthToken: Data saved in preferences."
                                        )
                                    }
                                    SIGNIN_METHOD = "google"
                                    val request = CreateUserRequest(
                                        method = SIGNIN_METHOD,
                                        idToken = idToken.toString()
                                    )
                                    authViewModel.postUserV2(request)
                                }
                            }
                        } else {
                            loader(false)
                            Log.w(
                                "TAG_Google_sigxnin",
                                "signInWithCredential:failure",
                                task.exception
                            )
                            showToast("Failed to login.Please try again!")
                        }
                    }
                })


//            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            Log.e("TAG_Google_sigxnin", "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActiviyLogin2Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        postEvent(Constants.PAGE_LOGIN, null)

        binding.loginLayout.visibility = View.VISIBLE
        binding.otpLayout.visibility = View.GONE
        mAuth = FirebaseAuth.getInstance()
        phoneNumber = intent.getStringExtra(Constants.phoneNumber) ?: "1234567890"

        window.statusBarColor =
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.blue_status_bar_color
            )
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.your_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.signInButton.setOnClickListener {
            loader(true)
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        setMessage()
        setUpObserver()

        with(binding) {
            generateOtpBtn.setOnClickListener {
                if (generateOtpBtn.text.equals("Sign in with Mobile number")) {

                    customSignInButton.visibility = View.GONE
                    signInMessage.visibility = View.GONE
                    generateOtpBtn.text = "Generate OTP"
                    generateOtpBtn.backgroundTintList =
                        ContextCompat.getColorStateList(this@LoginActivity, R.color.btn_color)
                    generateOtpBtn.setTextColor(
                        ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.white
                        )
                    )
                    tvTermsConditions.visibility = View.VISIBLE
                    tvMessage.visibility = View.VISIBLE
                    divider.visibility = View.GONE
                    cvEditTextContent.visibility = View.VISIBLE

                } else {
                    postEvent(Constants.BUTTON_GENERATE_OTP, null)
                    editMode = false
//                PreferencesManagement.saveTempBaseUrl(this@LoginActivity,editTextText.text.toString())
                    startTimer()
                    loginUser()
                }
                /*if (isValidate()) {
                    loginUser()
                }*/
            }
        }
        initEditText()
        //setUpObserver()


        with(binding) {
            editPhoneNumber.setOnClickListener {
                postEvent(Constants.BUTTON_EDIT_PHONENUMBER, null)
                editMode = true
                cancelTimer()
                clearEditText()
                binding.otpLayout.visibility = View.GONE
                binding.loginLayout.visibility = View.VISIBLE
            }

            tvResendOtp.setOnClickListener {
                postEvent(Constants.BUTTON_RESEND_OTP, null)
                if (isOtpSend) {
                    resendOtp()
                }
            }

            otpVerifyBtn.setOnClickListener {
                postEvent(Constants.BUTTON_VERIFY_OTP, null)
                editPhoneNumber.isEnabled = false
                val otpString = binding.firstEdit.text.toString() +
                        binding.secondEdit.text.toString() +
                        binding.thirdEdit.text.toString() +
                        binding.fourthEdit.text.toString() +
                        binding.fifthEdit.text.toString() +
                        binding.sixthEdit.text.toString()
                Log.d("FIREBASE", "onCreate: $otpString")
                verifyOtp(otpString)
            }

        }

    }

    fun clearEditText() {
        with(binding) {
            firstEdit.requestFocus()
            firstEdit.setText("")
            secondEdit.setText("")
            thirdEdit.setText("")
            fourthEdit.setText("")
            fifthEdit.setText("")
            sixthEdit.setText("")
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (binding.otpLayout.visibility == View.VISIBLE) {
            editMode = true
            binding.otpLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
//            showToast("Do not press back button")
//            onBack = true
        } else {
            super.onBackPressed()
        }

        /*if (shouldAllowBack()) {
            super.onBackPressed()
        } else {

        }*/
    }

    private fun shouldAllowBack(): Boolean {
        return onBack
    }

    private fun resendOtp() {
        loginUser()
    }

    private fun verifyOtp(code: String) {
        if (code != "") {
            if (isNetworkAvailable()) {
                loader(true)
                editMode = false
                //verify firebase otp here
                Log.d("FIREBASE", "verifyOtp: verification ID: $verificationId code: $code")
                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)

                // after getting credential we are
                // calling sign in method.

                // after getting credential we are
                // calling sign in method.
                signInWithCredential(credential)
            } else {
                showSnackBar(
                    binding.otpLayout,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        } else {
            showToast("Please enter OTP")
        }
    }

    private fun cancelTimer() {
        with(binding) {
            isOtpSend = true
            tvResendOtp.visibility = View.VISIBLE
            tvReceiveOtp.text = resources.getString(R.string.did_receive_otp)
        }
        countDownTimer.cancel()
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    clearEditText()
                    generateAuthToken()
                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val idToken = it.result.token
                                val auth = "Bearer $idToken"
                                SIGNIN_METHOD = "phone"
                                val request = CreateUserRequest(
                                    method = SIGNIN_METHOD,
                                    idToken = idToken.toString()
                                )
                                authViewModel.postUserV2(request)

                                val clipboard =
                                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip =
                                    ClipData.newPlainText(android.R.attr.label.toString(), idToken)
//                                clipboard.setPrimaryClip(clip)
                                if (PreferencesManagement.saveAuthToken(this, auth)) {
                                    Log.d(
                                        "akd_debug",
                                        "generateAuthToken: Data saved in preferences."
                                    )
                                }
                            }
                        }


                } else {
                    binding.editPhoneNumber.isEnabled = true
                    loader(false)
                    showToast("Wrong OTP Entered.Please try again")
                    // Sign in failed, display a message and update the UI
                    Log.w("FIREBASE", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
        /*
        {"zza":{"zza":{"zzb":"AOkPPWQN7S8aKrd9x9R5f4Xw7_d2nbLUkCTYcZB4Q9zLUx-71cRUWXa3KvwPPV0g98p7fiRaU-XTQucrto_UfJCq61AZ7zRwv1kjAWSZ5yrU3XTQdBj77dzjSAnChmJaoe5uDVw2hC5wLI28Yqxwta-AmZcUj20KDKFjsKFN4blXXm1h42JMyxGhtK2ejQ6Z9qASZyAYZufTk47k0-ISeZHDll00Bz24Vw",
        "zzc":"eyJhbGciOiJSUzI1NiIsImtpZCI6ImY1NWU0ZDkxOGE0ODY0YWQxMzUxMDViYmRjMDEwYWY5Njc5YzM0MTMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vYWJyYS1rYS1kYWJyYS1jNmEyMCIsImF1ZCI6ImFicmEta2EtZGFicmEtYzZhMjAiLCJhdXRoX3RpbWUiOjE2NzM1OTk0MzgsInVzZXJfaWQiOiJCSDVmZHdFU3YzVHlncUo0OXBFSFNiWW9Ha2cxIiwic3ViIjoiQkg1ZmR3RVN2M1R5Z3FKNDlwRUhTYllvR2tnMSIsImlhdCI6MTY3MzU5OTQzOCwiZXhwIjoxNjczNjAzMDM4LCJwaG9uZV9udW1iZXIiOiIrOTE5NDkyMDQzNjAwIiwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJwaG9uZSI6WyIrOTE5NDkyMDQzNjAwIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGhvbmUifX0.gEpyiD9mfpKrvAaAYYWR3O5iL0KGg9uu--89wdXsgsl9iv8MRFVRJ23H9tZMusWmRFRb_QLtqmsriBRdjyu-4PekBfpoaaCA2CSovgsfI7guYba5QrTMUk87w1sG2MybQJEPeGpwo_9jZX4EGxPPorF8HddAKNmhIM8pV-VWuKyBAs8650soHKOHxGdbf33Y7XPs5Csy0aONzK3wqPsE4LBaLBHwWy0o9x81UA0lhodmKcy8TtaB5J0CnWPMA2tdjVno-2fbYU-KHbyMcJ1MEbkulhwaOoQHmB6wv_K8w8IiodWSifyk04d_4ZO2uJ7V81PCS3P4R7K2quztpC4R6g","zzd":3600,"zze":"Bearer","zzf":1673599436953},"zzb":{"zza":"BH5fdwESv3TygqJ49pEHSbYoGkg1","zzb":"firebase","zzg":"+919492043600","zzh":false},"zzc":"[DEFAULT]","zzd":"com.google.firebase.auth.internal.DefaultFirebaseUser","zze":[{"zza":"BH5fdwESv3TygqJ49pEHSbYoGkg1","zzb":"firebase","zzg":"+919492043600","zzh":false},{"zzb":"phone","zzg":"+919492043600","zzh":false}],"zzf":["phone"],
        "zzg":"2","zzh":false,"zzi":{"zza":1673599438274,"zzb":1673598888449},"zzj":false},"zzb":{"zzd":false}}
        * */
    }

    fun postFCMtoken() {
        generateAuthToken()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            PreferencesManagement.saveFCMToken(this, it)
            val data = FcmRequest(
                data = FCMData(
                    fcmToken = PreferencesManagement.getFCMToken(this)!!
                )
            )

            val map = java.util.HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token

            authViewModel.postFCMToken(map, data)

        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }

    // callback method is called on Phone auth provider.
    private val
            mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            // below method is used when
            // OTP is sent from Firebase
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                loader(false)

                // when we receive the OTP it
                // contains a unique id which
                // we are storing in our string
                // which we have already created.
                verificationId = s
                binding.loginLayout.visibility = View.GONE
                binding.otpLayout.visibility = View.VISIBLE
                phoneNumber = "+91" + binding.etPhoneNumber.text.toString().trim()
                setMessage1()
            }

            // this method is called when user
            // receive OTP from Firebase.
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                loader(false)

                // below line is used for getting OTP code
                // which is sent in phone auth credentials.
                val otp = phoneAuthCredential.smsCode
                Log.d("FIREBASE", "onVerificationCompleted: OTP from firebase:$otp")

                clearEditText()
                // checking if the code
                // is null or not.
                if (otp != null && !editMode) {

                    binding.firstEdit.setText("${otp[0]}")
                    binding.secondEdit.setText("${otp[1]}")
                    binding.thirdEdit.setText("${otp[2]}")
                    binding.fourthEdit.setText("${otp[3]}")
                    binding.fifthEdit.setText("${otp[4]}")
                    binding.sixthEdit.setText("${otp[5]}")

                    hideSoftKeyboard()
                    verifyOtp(otp)
                    // if the code is not null then
                    // we are setting that code to
                    // our OTP edittext field.
//                    edtOTP.setText(code)

                    // after setting this code
                    // to OTP edittext field we
                    // are calling our verifycode method.
//                    verifyCode(code)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                loader(false)

                // displaying error message with firebase exception.
                Log.d("FIREBASE", "onVerificationFailed: ${e.message}")
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun loginUser() {
        if (isValidate()) {
            loader(true)
            val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(
                    "+91" + binding.etPhoneNumber.text.toString().trim()
                )       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            phoneNumber = "+91" + binding.etPhoneNumber.text.toString().trim()
            Log.d("FIREBASE", "phone no:${binding.etPhoneNumber.text.toString()} sent to fb")

        }
    }

    fun getFCMToken1() {

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            PreferencesManagement.saveFCMToken(this, it)
        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }

    private fun isValidate(): Boolean {
        with(binding) {
            if (!ValidatorUtils.isMobileValidate(etPhoneNumber.text.toString().trim())) {
                showToast("Please Enter Valid Mobile Number")
                return false
            }
            return true
        }
    }

    private fun setMessage() {
        val str = resources.getString(R.string.login_str)

        val regular = ResourcesCompat.getFont(this@LoginActivity, R.font.montserrat_regular)
        val bold = ResourcesCompat.getFont(this@LoginActivity, R.font.montserrat_bold)

        val span = SpannableStringBuilder(str)
        span.setSpan(CustomTypefaceSpan("", regular), 0, 44, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        span.setSpan(CustomTypefaceSpan("", bold), 44, str.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.tvMessage.text = span

        binding.tvTermsConditions.makeLinks(
            Pair("Privacy Policy", View.OnClickListener {
                val intent = Intent(this, TermsAndConditionsActivity::class.java)
                intent.putExtra("FROM_KEY", Constants.privacyPolicy)
                startActivity(intent)
                /* LaunchUtility.launchUrl(
                     "https://abra-ka-dabra.com/privacy-policy/",
                     this@LoginActivity
                 )*/
            })
        )

    }

    private fun setUpObserver() {

        authViewModel.fcmSuccess.observe(this) {
            Log.d("okkhttp", "setUpObserver: fcm token response $it")
//            clearEditText()
//            goHome()
        }

        authViewModel.updateUserSuccess.observe(this) {
            loader(false)
            clearEditText()
            goHome()
        }
        authViewModel.createUserSuccess.observe(this) {
            showToast(it.responseMessage.toString())
            if (it.responseMessage == "User created successfully." || it.responseMessage == "User already exists." || it.responseMessage == "User Login Success.") {
                generateAuthToken()
                val map = HashMap<String, String>()
                map[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this).toString()
                authViewModel.getUser(map)
            } else {
                FirebaseAuth.getInstance().signOut()
                mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener { task ->
                        loader(false)
                        if (task.isSuccessful) {
                            // Google access revoked
                            showAlertToExistingAssociatedUser(it.responseMessage.toString())
                        } else {
                            // Handle error
                            showToast("Error! Please try again.")
                        }
                    }
//                showToast(it.responseMessage.toString())
            }
        }

        authViewModel.getUserSuccess.observe(this) {
            PreferencesManagement.saveUserInfo(this, it)
            Log.d("LOGIN>>>", "setUpObserver: ${Gson().toJson(it)}")
            PreferencesManagement.saveUserName(this, it.data?.name)
            PreferencesManagement.saveUserEmail(this, it.data?.email)
            PreferencesManagement.saveSignInMethod(this,SIGNIN_METHOD)

            if (it.data?.onboardingStatus!!){
                if (it.responseMessage != null) {
                    if (it.responseMessage == USER_NOT_FOUND) {
                        Log.d("LOGIN>>>", "setUpObserver: User not found")
                        loader(false)
                        val intent =
                            Intent(this@LoginActivity, AuthUserDetailActivity::class.java)
                        intent.putExtra(Constants.phoneNumber, phoneNumber)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                } else if ((it.data?.email == null || it.data?.email == "") ||
                    (it.data?.name == null || it.data?.name == "")
                ) {
                    Log.d("LOGIN>>>", "setUpObserver: EMail not found")

                    loader(false)
                    val intent =
                        Intent(this@LoginActivity, AuthUserDetailActivity::class.java)
                    intent.putExtra(Constants.phoneNumber, phoneNumber)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else if (it.data?.signinMethod == SIGN_IN_METHOD_GOOGLE) {
                    postFCMtoken()
                    updateUser(it)
                } else {
                    if (mAuth.currentUser != null) {
                        postFCMtoken()
                        postLocationUpdate()
                    }
                }
            }else{
                loader(false)
                val intent =
                    Intent(this@LoginActivity, AuthUserDetailActivity::class.java)
                intent.putExtra(Constants.phoneNumber, phoneNumber)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }


            authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

            authViewModel.isLoading.observe(this) { loader(it) }
        }
    }

    private fun updateUser(it: GetUserResponse?) {
        if (isNetworkAvailable()) {
            if (isLocationEnabled()) getLastLocation()

            val mapAuth = HashMap<String, String>()
            mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

            getLastLocation()
            val data = UsersUpdateData(
                name = it?.data?.name
            )
            val dataClass = DataClass(data)

            generateAuthToken()
            val map = java.util.HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token

            authViewModel.updateUserV2(map, dataClass)

        } else {
            showToast(
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun postLocationUpdate() {
        if (isNetworkAvailable()) {
            if (isLocationEnabled()) getLastLocation()

            val mapAuth = HashMap<String, String>()
            mapAuth[RequestKeys.authorization] = PreferencesManagement.getAuthToken(this)!!

            val data = UsersUpdateData(
                location = Location(lat, lng)
            )
            val dataClass = DataClass(data)

            generateAuthToken()
            val map = java.util.HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token

            authViewModel.updateUserV2(map, dataClass)

        } else {
            showToast(
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun goHome() {
        val intent =
            Intent(this@LoginActivity, NewHomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra(Constants.phoneNumber, phoneNumber)
        startActivity(intent)
        finish()
    }

    private fun showAlertToExistingAssociatedUser(errotTxt: String) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_submit_caution_dialog, null)
        dialogBuilder.setView(dialogView)

        val success_ok_btn = dialogView.findViewById<TextView>(R.id.success_ok_btn)
        val alert_text = dialogView.findViewById<TextView>(R.id.textView81)
        val alert_title = dialogView.findViewById<TextView>(R.id.textView812)
        alert_title.visibility = View.GONE
        alert_text.setText(errotTxt)
        success_ok_btn.setText("OK")
        success_ok_btn.setOnClickListener {
            clearPreferences()
            binding.otpLayout.visibility = View.GONE
            binding.loginLayout.visibility = View.VISIBLE
            alertDialog.dismiss()
        }
        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

    private fun clearPreferences() {
        clearEditText()
        PreferencesManagement.saveUserProfileFlag(this, false)
        PreferencesManagement.saveUserFlag(this, false)
        PreferencesManagement.saveUserInfo(this, null)
        PreferencesManagement.saveUserName(this, "")
        PreferencesManagement.saveUserEmail(this, "")
    }

    ////OTP functions
    private fun initEditText() {

        binding.firstEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.firstEdit,
                binding.secondEdit,
                this
            )
        )
        binding.secondEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.secondEdit,
                binding.thirdEdit,
                this
            )
        )
        binding.thirdEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.thirdEdit,
                binding.fourthEdit,
                this
            )
        )

        binding.fourthEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.fourthEdit,
                binding.fifthEdit,
                this
            )
        )
        binding.fifthEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.fifthEdit,
                binding.sixthEdit,
                this
            )
        )
        binding.sixthEdit.addTextChangedListener(
            GenericTextWatcher(
                binding.sixthEdit,
                null,
                this
            )
        )

        binding.firstEdit.setOnKeyListener(GenericKeyEvent(binding.firstEdit, null))
        binding.secondEdit.setOnKeyListener(GenericKeyEvent(binding.secondEdit, binding.firstEdit))
        binding.thirdEdit.setOnKeyListener(GenericKeyEvent(binding.thirdEdit, binding.secondEdit))
        binding.fourthEdit.setOnKeyListener(GenericKeyEvent(binding.fourthEdit, binding.thirdEdit))
        binding.fifthEdit.setOnKeyListener(GenericKeyEvent(binding.fifthEdit, binding.fourthEdit))
        binding.sixthEdit.setOnKeyListener(GenericKeyEvent(binding.sixthEdit, binding.fifthEdit))

    }

    private fun setMessage1() {
        val str =
            "${resources.getString(R.string.otp_verification_str)} - $phoneNumber"

        val regular =
            ResourcesCompat.getFont(this@LoginActivity, R.font.montserrat_regular)
        val bold = ResourcesCompat.getFont(this@LoginActivity, R.font.montserrat_bold)

        val span = SpannableStringBuilder(str)
        span.setSpan(CustomTypefaceSpan("", regular), 0, 55, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        span.setSpan(CustomTypefaceSpan("", bold), 55, str.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.tvMessage1.text = span

    }

    private fun startTimer() {

        with(binding) {

            tvResendOtp.visibility = View.GONE
            tvReceiveOtp.text = resources.getString(R.string.resend_otp_in)

            countDownTimer = object : CountDownTimer(300000, 1000) {

                override fun onTick(leftTimeInMilliseconds: Long) {
                    val totalDuration: String =
                        DateTimeUtils.convertLongToTimeFormat((leftTimeInMilliseconds / 1000).toInt())

                    val str = "${resources.getString(R.string.resend_otp_in)} $totalDuration"

                    tvReceiveOtp.text = str

                }

                override fun onFinish() {
                    isOtpSend = true
                    tvResendOtp.visibility = View.VISIBLE
                    tvReceiveOtp.text = resources.getString(R.string.did_receive_otp)
                }
            }.start()
        }
    }

    override fun onDestroy() {
        try {
            cancelTimer()
        } catch (e: Exception) {
            Log.d("TAG", "onDestroy: ${e.message}")
        }
        super.onDestroy()
    }

}