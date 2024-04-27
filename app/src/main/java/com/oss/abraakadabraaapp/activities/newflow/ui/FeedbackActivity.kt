package com.oss.abraakadabraaapp.activities.newflow.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.databinding.ActivityFeedbackBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedbackActivity : BaseActivity() {
    private lateinit var binding:ActivityFeedbackBinding
    lateinit var application: BaseActivity

    private val mainViewModel: AuthViewModel by viewModel()
    var product_id = ""
    var user_id = ""
    var from = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        application = this
        application.postEvent(Constants.PAGE_FEEDBACK,null)

        if (intent.hasExtra("PRODUCT_ID")) {
            product_id = intent.extras?.getString("PRODUCT_ID", "")!!
        }
        if (intent.hasExtra("USER_ID")) {
                user_id = intent.extras?.getString("USER_ID","")!!

        }
        if (intent.hasExtra("from")) {
            from = intent.extras?.getString("from", "listing")!!
        }

        if (from == "listing"){
            binding.headingOne.text = "Was the product taken from you in a timely manner?"
            binding.headingTwo.text = "Was the product pick-up convenient to you?"
            binding.headingThree.text = "Did you find the receiver reliable?"
        }else{
            binding.headingOne.text = "Did you get the product as \ndescribed?\n"
            binding.headingTwo.text = "Was the giver responsive to your \nrequest?\n"
            binding.headingThree.text = "Was the product delivery convenient\nfor you?\n"
        }

        binding.seekbar.customSectionTrackColor { colorIntArr ->
            colorIntArr!![0] = Color.parseColor("#F95565")
            colorIntArr[1] = Color.parseColor("#FE962F")
            colorIntArr[2] = Color.parseColor("#FECE2F")
            colorIntArr[3] = Color.parseColor("#03C437")
            colorIntArr[4] = Color.parseColor("#2D7A76")

            true
        }
        binding.seekbar1.customSectionTrackColor { colorIntArr ->
            colorIntArr!![0] = Color.parseColor("#F95565")
            colorIntArr[1] = Color.parseColor("#FE962F")
            colorIntArr[2] = Color.parseColor("#FECE2F")
            colorIntArr[3] = Color.parseColor("#03C437")
            colorIntArr[4] = Color.parseColor("#2D7A76")

            true
        }
        binding.seekbar2.customSectionTrackColor { colorIntArr ->
            colorIntArr!![0] = Color.parseColor("#F95565")
            colorIntArr[1] = Color.parseColor("#FE962F")
            colorIntArr[2] = Color.parseColor("#FECE2F")
            colorIntArr[3] = Color.parseColor("#03C437")
            colorIntArr[4] = Color.parseColor("#2D7A76")

            true
        }
        binding.updateBtn.setOnClickListener {
            val i = binding.seekbar1.progress
            Log.d("FEEDBACK - ", "onCreate: $i")
            if (binding.descriptionTxt.text.toString() == ""){
                showToast("Please enter some text")
            }else{
                if (from == "listing"){
                    val map = HashMap<String,String>()
                    map["pickup_convenience"] = (binding.seekbar.progress).div(20).toString()
                    map["receiver_reliability"] = binding.seekbar1.progress.div(20).toString()
                    map["pick_up_timeliness"] = binding.seekbar2.progress.div(20).toString()
                    map["feedback_text"] = binding.descriptionTxt.text.toString()
                    map["submitted_for"] = user_id
                    map["feedback_type"] = "giver"

                    mainViewModel.sendFeedback(user_id,map)
                }else{
                    val map = HashMap<String,String>()
                    map["delivery_convenience"] = (binding.seekbar.progress).div(20).toString()
                    map["giver_responsiveness"] = binding.seekbar1.progress.div(20).toString()
                    map["product_satisfaction"] = binding.seekbar2.progress.div(20).toString()
                    map["feedback_text"] = binding.descriptionTxt.text.toString()
                    map["submitted_for"] = user_id
                    map["feedback_type"] = "reciever"

                    mainViewModel.sendFeedback(user_id,map)
                }
            }

        }
        binding.skipTxt.setOnClickListener {
//            gotoMain()
            showRating()
        }
        binding.backButton.setOnClickListener {
            showRating()
        }
        setUpObserver()
    }

    private fun showRating() {
        val manager: ReviewManager = ReviewManagerFactory.create(this)

// Create a ReviewManager request flow
        val request: Task<ReviewInfo> = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Get the ReviewInfo object
                val reviewInfo: ReviewInfo = task.result

                // Launch the in-app review flow
                val flow: Task<Void> = manager.launchReviewFlow(this, reviewInfo)

                flow.addOnCompleteListener { reviewFlowTask ->
                    // In-app review completed
                    if (reviewFlowTask.isSuccessful) {
                        showToast("Thanks for your rating.")
                    } else {
                        // Handle review flow failure
                    }
                    gotoMain()
                }
            } else {
                // There was an error getting the in-app review information
                // Handle the error accordingly
                showToast("Error getting rating information")
            }
        }
    }

    private fun gotoMain() {
        val intent =
            Intent(this@FeedbackActivity, NewHomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            intent.putExtra(Constants.phoneNumber,phoneNumber)
        startActivity(intent)
        finish()
    }

    private fun setUpObserver()
    {

        mainViewModel.sendFeedbackSuccess.observe(this){
            if (it.code == 201){
                showToast(it.response.toString())
                binding.successAlertDialog.visibility = View.VISIBLE
            }
        }

        mainViewModel.errorMessage.observe(this) { /*if (it.isNotBlank()) showToast(it)*/ }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }

}