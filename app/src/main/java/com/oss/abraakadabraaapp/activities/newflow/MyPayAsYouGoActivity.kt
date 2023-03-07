package com.oss.abraakadabraaapp.activities.newflow

import RequestDetails
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.FeedbackActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyPayAsYouGoBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_100
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_200
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_500
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_PAYASWISH
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CONTRIBUTE
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import com.razorpay.Checkout
import com.razorpay.PayloadHelper
import com.razorpay.PaymentResultListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyPayAsYouGoActivity : BaseActivity(), PaymentResultListener {
    var productId = ""
    var orderId = ""
    var receiverId = ""
    var razorPayId = ""
    var name = ""
    var email = ""
    var phone = ""
    private var productDetial: RequestDetails? = null
    private val mainViewModel: AuthViewModel by viewModel()
    private lateinit var from: String

    private lateinit var binding:ActivityMyPayAsYouGoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPayAsYouGoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        from = intent.extras?.getString("from", "").toString()

        productId = intent.extras?.getString("productId","")!!

        name = intent.extras?.getString("name","")!!
        email = intent.extras?.getString("email","")!!
        phone = intent.extras?.getString("phone","")!!
        receiverId = intent.extras?.getString("receiver_id","")!!

        getRazorPay()

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_PAYASWISH)
            onBackPressed()
        }
        binding.skipPay.setOnClickListener {
            if (from == "listing" || from == "requesting"){
                val intent = Intent(this, FeedbackActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("from",from)
                intent.putExtra("PRODUCT_ID", productId)
                intent.putExtra("USER_ID",receiverId)
                startActivity(intent)
                finish()
            }else{
                finish()
            }
        }
        binding.button3.setOnClickListener {
            postClick(BUTTON_100)
            takeToPayment("100")

        }
        binding.button5.setOnClickListener {
            postClick(BUTTON_200)
            takeToPayment("200")
             }
        binding.button6.setOnClickListener {
            postClick(BUTTON_500)
            takeToPayment("500")
            }
        binding.button7.setOnClickListener {
            postClick(BUTTON_CONTRIBUTE)
            takeToPayment(binding.customAmount.text.toString())
            }

        setUpObserver()

    }

    private fun getRazorPay() {
        generateAuthToken()
        val map = HashMap<String,String>()
        val authMap = Utility.getAuthentication(this)
        mainViewModel.getRazorPay(authMap)
    }

    private fun takeToPayment(s: String) {
        if (s == ""){
            showToast("Please enter some amount")
        }else{
            generateAuthToken()
            val map = HashMap<String,String>()
            map["amount"] = (s).toString()
            map["productId"] = productId
            val authMap = Utility.getAuthentication(this)
            authMap["logging"] = "true"
            mainViewModel.initPayment(authMap,map)
        }

    }

    private fun setUpObserver()
    {
        mainViewModel.razorpaySuccess.observe(this){
            if (it.code == 200){
                razorPayId = it.data?.RAZORPAYKEYID.toString()
            }
        }

        mainViewModel.initPaymentSuccess.observe(this){
            if (it.code == 200){
                orderId = it.data?.orderId.toString()
                sendToRazorPay(it.data?.orderId, it.data?.amount!!)
            }
        }

        mainViewModel.updatePaymentSuccess.observe(this){
            if (it.code == 200){
                showToast("Payment Success")
                if (from == "listing" || from == "requesting"){
                    val intent = Intent(this, FeedbackActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("from",from)
                    intent.putExtra("PRODUCT_ID", productId)
                    intent.putExtra("USER_ID",receiverId)
                    startActivity(intent)
                    finish()
                }else{
                    finish()
                }
//                sendToRazorPay(it.data?.orderId,it.data?.amount)
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }
    }

    private fun sendToRazorPay(order: String?, amount: Int) {
        val activity: Activity = this
//        var amount = (amount?.times(100))
//        Log.d("Razorpay - ", "onPaymentSuccess: $amount")
        if (razorPayId != ""){
            val checkout = Checkout()
            checkout.setKeyID(razorPayId)
//        checkout.setKeyID(Constants.razor_pay_id)

            val payloadHelper = PayloadHelper("INR", amount, order!!)
            payloadHelper.description = "$amount Rupees from ${name}"
            payloadHelper.prefillEmail = email
            payloadHelper.prefillContact = phone
            checkout.open(activity, payloadHelper.getJson())
        }else{
            showToast("Razor pay id is missing")
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.d("Razorpay - ", "onPaymentSuccess: $p0")
        generateAuthToken()
        val map = HashMap<String,String>()
        map["orderId"] = orderId
        map["transactionId"] = p0.toString()
        map["status"] = "success"

        val authMap = Utility.getAuthentication(this)
        authMap["logging"] = "true"
        mainViewModel.updatePayment(authMap,map)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.d("Razorpay - ", "Failed: $p0  : $p1")
        showToast("Payment Failed!")
        generateAuthToken()
        val map = HashMap<String,String>()
        map["orderId"] = orderId
        map["transactionId"] = "null"
        map["status"] = "failed"

        val authMap = Utility.getAuthentication(this)
        authMap["logging"] = "true"
        mainViewModel.updatePayment(authMap,map)
    }

}