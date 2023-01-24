package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.FeedbackActivity
import com.oss.abraakadabraaapp.databinding.ActivityRequesterBinding
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_ACCEPT_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_MARK_AS_DELIVERED_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_OK_GOT_IT_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_OK_GOT_IT_TO_FEEDBACK

class RequesterActivity : BaseActivity() {
    private lateinit var binding:ActivityRequesterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_REQUESTER)
            onBackPressed()
        }
        binding.acceptBtn.setOnClickListener{
            //
            postClick(BUTTON_ACCEPT_IN_REQUESTER)
            binding.successLayout.visibility = View.VISIBLE
            binding.markAsDelivered.text = "Mark As\nDelivered"
            binding.acceptBtn.isClickable = false
            binding.markAsDelivered.setTextColor(resources.getColor(R.color.title_color))
        }
        binding.markAsDelivered.setOnClickListener {
            postClick(BUTTON_MARK_AS_DELIVERED_IN_REQUESTER)
            if(binding.markAsDelivered.text.toString().equals("Reject"))
                finish()
            else
                binding.successLayout2.visibility = View.VISIBLE
            //Reject login write here...
        }
        binding.okGotItBtn.setOnClickListener {
            postClick(BUTTON_OK_GOT_IT_IN_REQUESTER)
            binding.successLayout.visibility = View.GONE
        }
        binding.successLayout.setOnClickListener {
            binding.successLayout.visibility = View.GONE
        }
        binding.successDialog.setOnClickListener {
            binding.successLayout.visibility = View.VISIBLE
        }
        binding.successLayout2.setOnClickListener {
            binding.successLayout2.visibility = View.GONE
        }
        binding.successDialog2.setOnClickListener {
            binding.successLayout2.visibility = View.VISIBLE
        }
        binding.okGotItBtn2.setOnClickListener {
            postClick(BUTTON_OK_GOT_IT_TO_FEEDBACK)
            startActivity(Intent(this,FeedbackActivity::class.java))
        }
        binding.closeBtn.setOnClickListener{
            binding.successLayout2.visibility = View.GONE

        }
    }
}