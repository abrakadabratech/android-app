package com.oss.abraakadabraaapp.activities.auth

import DataClass
import UsersUpdateData
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.TermsAndConditionsActivity
import com.oss.abraakadabraaapp.databinding.ActivityLoginBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.*
import com.oss.abraakadabraaapp.utils.Constants.USER_NOT_FOUND
import com.oss.abraakadabraaapp.utils.customView.CustomTypefaceSpan
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class LoginActivity : BaseActivity() {

    private lateinit var countDownTimer: CountDownTimer
    private var isOtpSend = false
    private var editMode = false
    private var verificationId: String? = null

    private lateinit var phoneNumber: String

    private lateinit var binding: ActivityLoginBinding

    private val authViewModel: AuthViewModel by viewModel()

    private var latitude = ""
    private var longitude = ""
    var onBack = false
    private var address = ""
//    private val authToken by lazy { PreferencesManagement.getAuthToken(this)!! }

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loginLayout.visibility = View.VISIBLE
        binding.otpLayout.visibility = View.GONE
        mAuth = FirebaseAuth.getInstance()
        phoneNumber = intent.getStringExtra(Constants.phoneNumber) ?: "1234567890"

        window.statusBarColor =
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.blue_status_bar_color
            )

        setMessage()
        setUpObserver()

        with(binding) {
            generateOtpBtn.setOnClickListener {
                loginUser()

                /*if (isValidate()) {
                    loginUser()
                }*/
            }
        }
        initEditText()
        //setUpObserver()
        startTimer()


        with(binding) {
            tvResendOtp.setOnClickListener {
                if (isOtpSend) {
                    resendOtp()
                }
            }

            otpVerifyBtn.setOnClickListener {
                val otpString = binding.firstEdit.text.toString() +
                        binding.secondEdit.text.toString() +
                        binding.thirdEdit.text.toString() +
                        binding.fourthEdit.text.toString() +
                        binding.fifthEdit.text.toString() +
                        binding.sixthEdit.text.toString()
                Log.d("FIREBASE", "onCreate: $otpString")
                verifyOtp(otpString)
            }
            editPhoneNumber.setOnClickListener {
                editMode = true
                binding.otpLayout.visibility = View.VISIBLE
                binding.loginLayout.visibility = View.VISIBLE
            }
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
        }else{
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
        if (isValidate()) {
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
        }

    }
    private fun cancelTimer() {
        with(binding){
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
                    generateAuthToken()
                    val mUser = FirebaseAuth.getInstance().currentUser
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener(object : OnCompleteListener<GetTokenResult?>,
                            com.google.android.gms.tasks.OnCompleteListener<GetTokenResult> {
                            override fun onComplete(task: Task<GetTokenResult?>) {
                                loader(false)
                                if (task.isSuccessful()) {
                                    val idToken: String = task.getResult().getToken()!!
                                    Log.d(NewHomeActivity.TAG, "onComplete: $idToken")
                                } else {
                                    // Handle error -> task.getException();
                                }
                            }

                            override fun onComplete(task: com.google.android.gms.tasks.Task<GetTokenResult>) {
                                if (task.isSuccessful()) {
                                    loader(false)
                                    val idToken: String = task.getResult().getToken()!!
                                    val auth = "Bearer "+idToken
                                    val map = HashMap<String,String>()

                                    map[RequestKeys.authorization] = auth

                                    authViewModel.getUser(map)

                                    /*val clipboard =
                                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(android.R.attr.label.toString(), idToken)
                                    clipboard.setPrimaryClip(clip)*/

                                    Log.d(NewHomeActivity.TAG, "onComplete11: ${PreferencesManagement.saveAuthToken(this@LoginActivity,auth)}")
                                    // Send token to your backend via HTTPS
                                    // ...
                                } else {
                                    // Handle error -> task.getException();
                                }
                            }

                        })

//                    showToast("Login Success!!!")

//                    Log.d(
//                        "FIREBASE",
//                        "signInWithCredential:success tokeId is:${PreferencesManagement.getAuthToken(this)!!}"
//                    )

                } else {
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
    fun postFCMtoken(){
        generateAuthToken()
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            PreferencesManagement.saveFCMToken(this,it)
            val data = UsersUpdateData(
                fcmToken = PreferencesManagement.getFCMToken(this)!!
            )
            val dataClass = DataClass(data)

            val map = java.util.HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token
            Log.d(
                NewHomeActivity.TAG,
                "Token in Accounts fragment: ${JSONObject(Gson().toJson(dataClass))}"
            )
            authViewModel.updateUser(map, dataClass)

        }.addOnFailureListener {
            loader(false)
            if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                "Error Please try again !"
            )
        }
    }
    // callback method is called on Phone auth provider.
    private val   // initializing our callbacks for on
    // verification callback method.
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
            PreferencesManagement.saveFCMToken(this,it)
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

        authViewModel.updateUserSuccess.observe(this) {
            val intent =
                Intent(this@LoginActivity, NewHomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra(Constants.phoneNumber, phoneNumber)
            startActivity(intent)
            finish()
        }

        authViewModel.getUserSuccess.observe(this) {
            PreferencesManagement.saveUserInfo(this, it)
            if (it.responseMessage != null) {
                if (it.responseMessage == USER_NOT_FOUND) {

                    val intent =
                        Intent(this@LoginActivity, AuthUserDetailActivity::class.java)
                    intent.putExtra(Constants.phoneNumber, phoneNumber)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            } else {
                if (mAuth.currentUser != null) {
                    generateAuthToken()
                    FirebaseMessaging.getInstance().token.addOnSuccessListener {
                        PreferencesManagement.saveFCMToken(this, it)
                        val data = UsersUpdateData(
                            fcmToken = PreferencesManagement.getFCMToken(this)!!
                        )
                        val dataClass = DataClass(data)

                        val map = java.util.HashMap<String, String>()
                        val token = PreferencesManagement.getAuthToken(this)!!
                        map[RequestKeys.authorization] = token
                        Log.d(
                            NewHomeActivity.TAG,
                            "Token in Accounts fragment: ${JSONObject(Gson().toJson(dataClass))}"
                        )
                        authViewModel.updateUser(map, dataClass)

                    }.addOnFailureListener {
                        loader(false)
                        if (BuildConfig.DEBUG) showToast("Error Please try again ! ${it.localizedMessage}") else showToast(
                            "Error Please try again !"
                        )
                    }
                }
            }

            authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

            authViewModel.isLoading.observe(this) { loader(it) }
        }
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
        cancelTimer()
        super.onDestroy()
    }

}