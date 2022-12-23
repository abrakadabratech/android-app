package com.oss.abraakadabraaapp.activities.auth

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.receiver.SMSReceiver
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityOtpVerificationBinding
import com.oss.abraakadabraaapp.model.UserData
import com.oss.abraakadabraaapp.response.commonResponse.CommonResponse
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.DateTimeUtils
import com.oss.abraakadabraaapp.utils.GenericKeyEvent
import com.oss.abraakadabraaapp.utils.GenericTextWatcher
import com.oss.abraakadabraaapp.utils.customView.CustomTypefaceSpan
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap


class OtpVerificationActivity : BaseActivity(), SMSReceiver.OTPReceiveListener {

    private lateinit var binding: ActivityOtpVerificationBinding

    private lateinit var countDownTimer: CountDownTimer

    private lateinit var phoneNumber: String
    private var userData: UserData? = null

    private val authViewModel: AuthViewModel by viewModel()

    private var isOtpSend = false

    private lateinit var smsReceiver: SMSReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        phoneNumber = intent.getStringExtra(Constants.phoneNumber) ?: "1234567890"
        userData = intent.getParcelableExtra(Constants.userData)

        window.statusBarColor =
            ContextCompat.getColor(
                this@OtpVerificationActivity,
                R.color.blue_status_bar_color
            )

        initEditText()
        //setUpObserver()
        startTimer()
        setMessage()

        with(binding) {
            tvResendOtp.setOnClickListener {
                if (isOtpSend) {
                    resendOtp()
                }
            }

            otpVerifyBtn.setOnClickListener {

                //New Code
                val intent =
                    Intent(this@OtpVerificationActivity, AuthUserDetailActivity::class.java)
                startActivity(intent)

                // Old Code
                // verifyOtp()
            }
        }

    }

    private fun verifyOtp() {
        if (isValidate()) {
            if (isNetworkAvailable()) {

                val otpString = binding.firstEdit.text.toString() +
                        binding.secondEdit.text.toString() +
                        binding.thirdEdit.text.toString() +
                        binding.fourthEdit.text.toString()

                val map = HashMap<String, String>()

                map[RequestKeys.otp] = otpString
                map[RequestKeys.phoneNumber] = phoneNumber

                authViewModel.otpVerification(map)

            } else {
                showSnackBar(
                    binding.clVerifyOtpActivity,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }

    }

    private fun setUpObserver() {
        authViewModel.isLoading.observe(this) { loader(it) }

        authViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }

        authViewModel.otpVerificationSuccess.observe(this) {
            otpVerification(it!!)
        }

        authViewModel.sendOtpSuccess.observe(this) {
            startTimer()
            showToast(it.responseMessage)
        }
    }

    private fun otpVerification(it: CommonResponse) {
        showToast(it.responseMessage)
        cancelTimer()
        if (userData != null) {
            loginInUser(intent.getParcelableExtra(Constants.userData)!!)
        } else {
            val intent =
                Intent(this@OtpVerificationActivity, AuthUserDetailActivity::class.java)
            intent.putExtra(Constants.phoneNumber, phoneNumber)
            startActivity(intent)
        }
    }

    private fun resendOtp() {
        with(binding) {
            firstEdit.setText("")
            secondEdit.setText("")
            thirdEdit.setText("")
            fourthEdit.setText("")

            firstEdit.requestFocus()

            if (isNetworkAvailable()) {

                val map = HashMap<String, String>()
                map[RequestKeys.phoneNumber] = phoneNumber
                map[RequestKeys.smsKey] = smsToken
                authViewModel.sendOtp(map)

            } else {
                showSnackBar(
                    clVerifyOtpActivity,
                    applicationContext.resources.getString(R.string.no_internet_connection_found)
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startSMSListener()
    }

    private fun startSMSListener() {
        try {
            smsReceiver = SMSReceiver()
            smsReceiver.setOTPListener(this)
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            this.registerReceiver(smsReceiver, intentFilter)
            val client = SmsRetriever.getClient(this)
            val task = client.startSmsRetriever()
//            task.addOnSuccessListener {
//                // API successfully started
//            }
//            task.addOnFailureListener {
//                // Fail to start API
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOTPReceived(otp: String?) {
        val p = Pattern.compile("(?<=# Do not share: )\\d+")
        if (otp != null) {
            val m = p.matcher(otp)
            if (m.find()) {
                setOtp(m.group())
            }
        }
    }

    private fun setOtp(otp: String) {
        with(binding){
            Log.d("MYT", "otp ${otp[0]} ${otp[1]} ${otp[2]} ${otp[3]}")
            firstEdit.setText("${otp[0]}")
            secondEdit.setText("${otp[1]}")
            thirdEdit.setText("${otp[2]}")
            fourthEdit.setText("${otp[3]}")

            hideSoftKeyboard()
            verifyOtp()
        }
    }

    override fun onOTPTimeOut() {
        //  showToast("OTP Time out")
    }

    override fun onOTPReceivedError(error: String?) {
        // showToast(error!!)
    }

    private fun isValidate(): Boolean {
        return if (binding.firstEdit.text.toString().isNotBlank() &&
            binding.secondEdit.text.toString().isNotBlank() &&
            binding.thirdEdit.text.toString().isNotBlank() &&
            binding.fourthEdit.text.toString().isNotBlank()
        ) {
            true
        } else {
            showToast(resources.getString(R.string.enter_valid_otp))
            false
        }
    }

    private fun startTimer() {

        with(binding) {

            tvResendOtp.visibility = View.GONE
            tvReceiveOtp.text = resources.getString(R.string.resend_otp_in)

            countDownTimer = object : CountDownTimer(300000, 1000) {

                override fun onTick(leftTimeInMilliseconds: Long) {
                    val totalDuration : String = DateTimeUtils.convertLongToTimeFormat((leftTimeInMilliseconds / 1000).toInt())

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

    private fun temp() {

        with(binding) {

//            clCountTimer.visibility = View.VISIBLE
            tvResendOtp.visibility = View.GONE
            tvReceiveOtp.text = resources.getString(R.string.resend_otp_in)

            countDownTimer = object : CountDownTimer(30000, 1000) {

                override fun onTick(leftTimeInMilliseconds: Long) {
                    val seconds = leftTimeInMilliseconds / 1000
                    val countDown = seconds.toInt() * 100 / (30000 / 1000)
                    progressTimer.progress = countDown
                    tvTimer.text = "$seconds"
                }

                override fun onFinish() {
                    isOtpSend = true
//                    clCountTimer.visibility = View.GONE
                    tvResendOtp.visibility = View.VISIBLE
                    tvReceiveOtp.text = resources.getString(R.string.did_receive_otp)
                }
            }.start()
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

    private fun setMessage() {
        val str =
            "${resources.getString(R.string.otp_verification_str)} ${resources.getString(R.string.default_country_code)} - $phoneNumber"

        val regular =
            ResourcesCompat.getFont(this@OtpVerificationActivity, R.font.montserrat_regular)
        val bold = ResourcesCompat.getFont(this@OtpVerificationActivity, R.font.montserrat_bold)

        val span = SpannableStringBuilder(str)
        span.setSpan(CustomTypefaceSpan("", regular), 0, 55, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        span.setSpan(CustomTypefaceSpan("", bold), 55, str.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        binding.tvMessage.text = span

    }

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
                null,
                this
            )
        )

        binding.firstEdit.setOnKeyListener(GenericKeyEvent(binding.firstEdit, null))
        binding.secondEdit.setOnKeyListener(GenericKeyEvent(binding.secondEdit, binding.firstEdit))
        binding.thirdEdit.setOnKeyListener(GenericKeyEvent(binding.thirdEdit, binding.secondEdit))
        binding.fourthEdit.setOnKeyListener(GenericKeyEvent(binding.fourthEdit, binding.thirdEdit))

    }

    override fun onDestroy() {
        cancelTimer()
        super.onDestroy()
    }
}