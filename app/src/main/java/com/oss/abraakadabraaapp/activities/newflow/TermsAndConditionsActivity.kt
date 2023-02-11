package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.utils.Constants

class TermsAndConditionsActivity : AppCompatActivity() {
    var from = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)

        from = intent.extras?.getString("FROM_KEY","").toString()

        val webView = findViewById<WebView>(R.id.webView)
        val title = findViewById<TextView>(R.id.tv_toolbar_title)
        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }
        webView.settings.javaScriptEnabled = true

        when(from){
            Constants.aboutUs -> {
                webView.loadUrl("https://abra-ka-dabra.com/about-us/")
                title.setText("About Us")
            }
            Constants.privacyPolicy -> {
                title.setText("Privacy Policy")
                webView.loadUrl("https://abra-ka-dabra.com/privacy-policy/")

            }
            Constants.termsConditions -> {
                title.setText("Terms & Conditions")
                webView.loadUrl("https://abra-ka-dabra.com/terms-and-conditions/")

            }
        }

//        webView.loadUrl("https://merchant.razorpay.com/policy/KtgAylsnB0Q4uc")
    }
}