package com.oss.abraakadabraaapp.activities.newflow

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.model.PhonepeResponce
import com.oss.abraakadabraaapp.activities.newflow.ui.FeedbackActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyPayAsYouGoBinding
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_100
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_200
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_500
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_PAYASWISH
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CONTRIBUTE
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import com.razorpay.Checkout
import com.razorpay.PayloadHelper
import com.razorpay.PaymentResultListener
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MyPayAsYouGoActivity : BaseActivity(), PaymentResultListener {
    var productId = ""
    var orderId = ""
    var receiverId = ""
    var razorPayId = ""
    var name = ""
    var email = ""
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    var phone = ""
    val map = HashMap<String, String>()
    private val mainViewModel: AuthViewModel by viewModel()
    private lateinit var from: String

    private lateinit var binding: ActivityMyPayAsYouGoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPayAsYouGoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        from = intent.extras?.getString("from", "").toString()

        productId = intent.extras?.getString("productId", "")!!
        name = intent.extras?.getString("name", "")!!
        email = intent.extras?.getString("email", "")!!
        phone = intent.extras?.getString("phone", "")!!
        receiverId = intent.extras?.getString("receiver_id", "")!!

        //getRazorPay()

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_PAYASWISH)
            onBackPressed()
        }
        binding.skipPay.setOnClickListener {
            if (from == "listing" || from == "requesting") {
                val intent = Intent(
                    this,
                    FeedbackActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("from", from)
                intent.putExtra("PRODUCT_ID", productId)
                intent.putExtra("USER_ID",currentUserId )
                startActivity(intent)
                finish()
            } else {
                finish()
            }
        }
        binding.button3.setOnClickListener {
            postClick(BUTTON_100)
            changeButtonSelection(binding.button3)
            takeToPayment("100")

        }
        binding.button5.setOnClickListener {
            postClick(BUTTON_200)
            changeButtonSelection(binding.button5)
            takeToPayment("200")
        }
        binding.button6.setOnClickListener {
            postClick(BUTTON_500)
            changeButtonSelection(binding.button6)
            takeToPayment("500")
        }
        binding.button7.setOnClickListener {
            postClick(BUTTON_CONTRIBUTE)
            takeToPayment(binding.customAmount.text.toString())
        }

        setUpObserver()

    }

    private fun changeButtonSelection(selectBtn: TextView) {
        when(selectBtn){
            binding.button3 -> {
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.button5.setTextColor(ContextCompat.getColor(this, R.color.title_color))
                binding.button6.setTextColor(ContextCompat.getColor(this, R.color.title_color))
                binding.button3.background = (ContextCompat.getDrawable(this, R.drawable.amount_select_bg))
                binding.button5.background = (ContextCompat.getDrawable(this, R.drawable.rounded_rect_border))
                binding.button6.background = (ContextCompat.getDrawable(this, R.drawable.rounded_rect_border))
            }
            binding.button5 -> {
                binding.button5.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.title_color))
                binding.button6.setTextColor(ContextCompat.getColor(this, R.color.title_color))
                binding.button5.background = (ContextCompat.getDrawable(this, R.drawable.amount_select_bg))
                binding.button3.background = (ContextCompat.getDrawable(this, R.drawable.rounded_rect_border))
                binding.button6.background = (ContextCompat.getDrawable(this, R.drawable.rounded_rect_border))
            }
            binding.button6 -> {
                binding.button6.setTextColor(ContextCompat.getColor(this, R.color.white))
                binding.button3.setTextColor(ContextCompat.getColor(this, R.color.title_color))
                binding.button5.setTextColor(ContextCompat.getColor(this, R.color.title_color))
                binding.button6.background = (ContextCompat.getDrawable(this, R.drawable.amount_select_bg))
                binding.button3.background = (ContextCompat.getDrawable(this, R.drawable.rounded_rect_border))
                binding.button5.background = (ContextCompat.getDrawable(this, R.drawable.rounded_rect_border))
            }
        }

    }

    private fun takeToPayment(amount: String) {
        if (amount == "") {
            showToast("Please enter some amount")
        } else {
            val user = PreferencesManagement.getUserName(this)
            val auth = FirebaseAuth.getInstance().currentUser

            val map = HashMap<String,String>()
            map["amount"] = amount.toString()
            map["phone"] = phone
            map["name"] = name
            mainViewModel.initPayment(map)


           /* if (auth != null){
                Log.d("TAG_UPI", "takeToPayment: ${auth.displayName}")
                Log.d("TAG_UPI", "takeToPayment:user ${user}")
                val uri = Uri.parse("upi://pay").buildUpon()
                    .appendQueryParameter("pa", Constants.upiId)
                    .appendQueryParameter("pn", user)
                    .appendQueryParameter("tn", "")
                    .appendQueryParameter("am", amount)
                    .appendQueryParameter("cu", "INR")
                    .build()

                map["amount"] = amount
                map["payeeName"] = user.toString()
                map["transactionNote"] = "payment from ${user.toString()}"

                val upiPayIntent = Intent(Intent.ACTION_VIEW)
                upiPayIntent.data = uri

                // will always show a dialog to user to choose an app
                val chooser = Intent.createChooser(upiPayIntent, "Pay with")

                // check if intent resolves
                if (null != chooser.resolveActivity(packageManager)) {
                    startActivityForResult(chooser, 1001)
                } else {
                    Toast.makeText(
                        this,
                        "No UPI app found, please install one to continue",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                showToast("no user found")
            }*/
        }

    }

    private fun setUpObserver() {
        mainViewModel.razorpaySuccess.observe(this) {
            if (it.code == 200) {
                razorPayId = it.data?.RAZORPAYKEYID.toString()
            }
        }

        mainViewModel.initPaymentSuccess.observe(this) {
            if (it.code == 200) {
//                showToast(it.data!!.url.toString())

                if (it.data!!.url != ""){

                    binding.webview.visibility=View.VISIBLE
                    binding.webview.loadUrl(it.data?.url.toString())
                    Log.e("WEBVIEW", "setUpObserver: ${it.data?.url.toString()} ")

                    binding.webview.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {
                            val url = request.url.toString()
                            Log.d("WEBVIEW", "shouldOverrideUrlLoading: $url")

                            // Check if the URL corresponds to the payment completion page
                            return if (url.contains("/app/payment/status/")) {
                                // Payment completed, perform necessary actions
                                // You might want to notify your activity or handle it here
                                true // return true to indicate that you've handled the URL loading
                            } else super.shouldOverrideUrlLoading(view, request)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            Log.e("WEBVIEW", "onPageFinished: finished url $url")
                            if (url!!.contains("/app/payment/status/")){
                                FetchJsonTask(this@MyPayAsYouGoActivity,from,receiverId,productId,binding).execute(url)
                            } // Inject JavaScript to extract JSON response
                            binding.webview.evaluateJavascript("(function() { return JSON.stringify(window.YOUR_JSON_OBJECT); })();") { jsonString ->
                                // Process JSON response here
                                if (jsonString != null && jsonString.isNotBlank()) {
                                    try {
                                        val jsonObject = JSONObject(jsonString)
                                        Log.d("WEBVIEW", "onPageFinished: $jsonObject")

                                        // Now you have the JSON object, you can parse and use it as needed
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }

                }
//                orderId = it.data?.orderId.toString()
//                sendToRazorPay(it.data?.orderId, it.data?.amount!!)
            }
        }

        mainViewModel.postUPIPaymentSuccess.observe(this) {

            showToast(it.userMessage.toString())
            if (from == "listing" || from == "requesting") {
                val intent = Intent(
                    this,
                    FeedbackActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.putExtra("from", from)
                intent.putExtra("PRODUCT_ID", productId)
                intent.putExtra("USER_ID", receiverId)
                startActivity(intent)
                finish()
            } else {
                finish()
            }
//                sendToRazorPay(it.data?.orderId,it.data?.amount)

        }
        mainViewModel.errorMessage.observe(this) { /*if (it.isNotBlank()) showToast(it)*/ }
        mainViewModel.isLoading.observe(this) { loader(it) }
    }

    private fun sendToRazorPay(order: String?, amount: Int) {
        val activity: Activity = this
//        var amount = (amount?.times(100))
//        Log.d("Razorpay - ", "onPaymentSuccess: $amount")
        if (razorPayId != "") {
            val checkout = Checkout()
            checkout.setKeyID(razorPayId)
//        checkout.setKeyID(Constants.razor_pay_id)

            val payloadHelper = PayloadHelper("INR", amount, order!!)
            payloadHelper.description = "$amount Rupees from ${name}"
            payloadHelper.prefillEmail = email
            payloadHelper.prefillContact = phone
            checkout.open(activity, payloadHelper.getJson())
        } else {
            showToast("Razor pay id is missing")
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.d("Razorpay - ", "onPaymentSuccess: $p0")
        val map = HashMap<String, String>()
        map["orderId"] = orderId
        map["transactionId"] = p0.toString()
        map["status"] = "success"
        mainViewModel.updatePayment(map)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.d("Razorpay - ", "Failed: $p0  : $p1")
        showToast("Payment Failed!")
        val map = HashMap<String, String>()
        map["orderId"] = orderId
        map["transactionId"] = "null"
        map["status"] = "failed"

        mainViewModel.updatePayment( map)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1001 -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.d("UPI", "onActivityResult: $trxt")
                    val dataList = ArrayList<String>()
                    if (trxt != null) {
                        dataList.add(trxt)
                    }
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                Log.d(
                    "UPI",
                    "onActivityResult: " + "Return data is null"
                ) //when user simply back without payment
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (data.size > 0) {
            var str: String? = data[0]
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str!!)
            var paymentCancel = ""
            if (str == "") str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            Log.d("UPIPAY", "upiPaymentDataOperation: response arr " + Gson().toJson(response))

            for (i in response.indices) {
                val equalStr =
                    response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].lowercase() == "Status".lowercase()) {
                        status = equalStr[1].lowercase()
                    } else if (equalStr[0].lowercase() == "ApprovalRefNo".lowercase() || equalStr[0].lowercase() == "txnId".lowercase()) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            if (status == "success") {
                //Code to handle successful transaction here.
                map["status"] = status
                map["transactionId"] = approvalRefNo

                mainViewModel.postUPIPayment(map)
                Log.d("UPI", "responseStr: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                showToast("Payment cancelled")
            } else {
                map["status"] = "failed"
                map["transactionId"] = approvalRefNo
                mainViewModel.postUPIPayment(map)

//                Toast.makeText(this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show()
            }
        } else {
            showToast("Please try again!")
        }

    }
    class FetchJsonTask(val context: MyPayAsYouGoActivity,val from:String,
                        val receiverId:String,val productId:String,val binding: ActivityMyPayAsYouGoBinding) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg urls: String): String {
            val urlString = urls[0]
            var result = ""

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.connect()
                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))

                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }

                result = stringBuilder.toString()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {

                } else {
                    // Handle error response
                    Log.e("WEBVIEW", "doInBackground: error")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String) {
            // Process the JSON response here
            Log.e("WEBVIEW","results is $result")
            val res = Gson().fromJson(result, PhonepeResponce::class.java)
            if (res != null){
                Toast.makeText(context,res.message,Toast.LENGTH_SHORT).show()
                if (res.message == "Payment Success"){
                    binding.webview.visibility = View.GONE
                    PopupDialog.getInstance(context)
                        .statusDialogBuilder()
                        .createSuccessDialog()
                        .setActionButtonText("Close")
                        .setHeading("Payment Success")
                        .setDescription("The payment was successful. " +
                                "Thank you for contributing to Abra Ka Dabra's mission.")
                        .build {
                            val intent = Intent(
                                context,
                                FeedbackActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.putExtra("from", from)
                            intent.putExtra("PRODUCT_ID", productId)
                            intent.putExtra("USER_ID", receiverId)
                            context.startActivity(intent)
                            context.finish()
                        }
                        .show();

                }else{
                    paymentFailedPopup(context)
                }
            }else{
                paymentFailedPopup(context)
            }
        }

        private fun paymentFailedPopup(applicationContext: Context) {
            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show()
            PopupDialog.getInstance(applicationContext)
                .statusDialogBuilder()
                .createErrorDialog()
                .setHeading("Payment Failed!")
                .setDescription("Unexpected error occurred." +
                        " Please try again")
                .build(Dialog::dismiss)
                .show();
            binding.webview.visibility = View.GONE
        }
    }

    // Usage:

}