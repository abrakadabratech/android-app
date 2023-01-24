package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityPostedUserBinding
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_POSTED_USER
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility.getAuthentication
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostedUserActivity : BaseActivity() {

    private lateinit var binding : ActivityPostedUserBinding
    private var productDetails: ProductDetailsData? = null
    private val mainViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_posted_user)

        binding = ActivityPostedUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        postEvent(Constants.PAGE_POSTED_USER,null)

        setUpObserver()

        productDetails =
            Gson().fromJson(intent.extras?.getString(Constants.PRODUCT, ""), ProductDetailsData::class.java)

        if(productDetails !=null){
            setData(productDetails!!)
        }
        Log.d("TAG - ", "onCreate: ${Gson().toJson(productDetails)}")
        binding.submitBtn.setOnClickListener {
            postClick(Constants.BUTTON_SEND_IN_POSTED_USERS_PAGE)
            generateAuthToken()
//            if(generateAuthToken())

            val body = HashMap<String,String>()
            body["message"] = binding.requestMsg.text.toString()

            if (binding.requestMsg.text.isNotEmpty()){
                mainViewModel.postProductRequest(getAuthentication(this),
                    productDetails?.data?.id.toString(),body
                )
            }else{
                showToast("Please Enter Message.")
            }
        }

        binding.successOkBtn.setOnClickListener {
            postClick(Constants.BUTTON_OK_GOTIT_IN_POSTED_USERS_PAGE)
//            showToast("Under development, should I navigate to My Listing as per Design?")
            binding.successAlertDialog.visibility = View.GONE
            startActivity(Intent(this,MyListingActivity::class.java))
        }

        binding.successAlertDialog.setOnClickListener {
            binding.successAlertDialog.visibility = View.GONE
        }

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_POSTED_USER)
            onBackPressed()
        }
    }

    private fun setUpObserver() {
        mainViewModel.requstProductSuccess.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
                showToast(it.responseMessage.toString())
                binding.successAlertDialog.visibility = View.VISIBLE

            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }

        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }

    private fun setData(productDetails: ProductDetailsData) {
        with(binding){
            productName.setText(productDetails?.data?.name)
            locationTxt.setText(productDetails?.data?.locationName)
            postedBy.setText("Posted By " + productDetails?.data?.postedBy)
            Glide.with(this@PostedUserActivity).load(productDetails.data.images[0]).into(imageView20)

        }
    }
}