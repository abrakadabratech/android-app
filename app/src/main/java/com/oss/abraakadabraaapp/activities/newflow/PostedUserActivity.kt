package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityPostedUserBinding
import com.oss.abraakadabraaapp.utils.Constants

class PostedUserActivity : BaseActivity() {
    private lateinit var binding : ActivityPostedUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_posted_user)

        binding = ActivityPostedUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        postEvent(Constants.PAGE_POSTED_USER,null)

        binding.submitBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SEND_IN_POSTED_USERS_PAGE,null)
            binding.successAlertDialog.visibility = View.VISIBLE
        }

        binding.successOkBtn.setOnClickListener {
            postEvent(Constants.BUTTON_OK_GOTIT_IN_POSTED_USERS_PAGE,null)
            showToast("Under development, should I navigate to My Listing as per Design?")
//            startActivity(Intent(this,MyListingActivity::class.java))
        }

        binding.successAlertDialog.setOnClickListener {
            binding.successAlertDialog.visibility = View.GONE
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}