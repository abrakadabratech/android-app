package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.MyRequestActivity
import com.oss.abraakadabraaapp.adapter.RatingAdapter
import com.oss.abraakadabraaapp.databinding.ActivityPostedUserBinding
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_POSTED_USER
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility.getAuthentication
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.greenrobot.eventbus.EventBus
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



            val useLocation = PreferencesManagement.getUserLocation(this)
            val body = HashMap<String,String>()
            body["message"] = binding.requestMsg.text.toString()
            body["latitude"] = useLocation?.lat.toString()
            body["longitude"] = useLocation?.long.toString()

            if (binding.requestMsg.text.isNotEmpty()){
                val userInfo = PreferencesManagement.getUserInfo(this)
                if(userInfo?.data?.status == "active"){
                    mainViewModel.postProductRequest(getAuthentication(this),
                        productDetails?.data?.id.toString(),body
                    )
                }else{
                    showToast("Your profile not verified yet.")
                }
            }else{
                showToast("Please Enter Message.")
            }
        }

        binding.skipTxt.setOnClickListener {
            postClick(Constants.BUTTON_OK_GOTIT_IN_POSTED_USERS_PAGE)
//            showToast("Under development, should I navigate to My Listing as per Design?")
            binding.successAlertDialog.visibility = View.GONE
//            EventBus.getDefault().post("clear")

            val intent = Intent(this, NewMyRequestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("isFromRequestPage",true)
            startActivity(intent)
            finish()
        }
        binding.successOkBtn.setOnClickListener {
            binding.successAlertDialog.visibility = View.GONE
//            EventBus.getDefault().post("clear")

            val intent = Intent(this, NewMyRequestActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra("isFromRequestPage",true)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
           /* postClick(Constants.BUTTON_PAY_AS_YOU_WISH)
            val i = Intent(this, MyPayAsYouGoActivity::class.java)
            i.putExtra("productId",productDetails?.data?.id)
            i.putExtra("product_data",Gson().toJson(productDetails))
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
            startActivity(i)*/

//            startActivity(Intent(this,NewMyRequestActivity::class.java))
        }

        binding.successAlertDialog.setOnClickListener {
            binding.successAlertDialog.visibility = View.VISIBLE
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

                /*if (it.data.product_status == "hold"){
                    binding.textView81.text = getString(R.string.on_hold_product_message)
                }*/

            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }

        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }

    private fun setData(productDetails: ProductDetailsData) {
        with(binding){
            productName.setText(productDetails?.data?.name?.capitalize())
            locationTxt.setText(productDetails?.data?.locationName)
            memberSince.setText(productDetails?.data?.postedBy?.memberSince)
            givenItems.setText("Given ${productDetails?.data?.postedBy?.userStats?.given} Items")
            postedBy.setText("Posted By " + productDetails?.data?.postedBy?.name?.capitalize())
            Glide.with(this@PostedUserActivity).load(productDetails.data.images[0])
                .placeholder(resources.getDrawable(R.drawable.user))
                .into(imageView20)
            if (productDetails.data.postedBy?.userAvatar != null){
                Glide.with(this@PostedUserActivity).load(productDetails.data.postedBy!!.userAvatar)
                    .placeholder(resources.getDrawable(R.drawable.user))
                    .into(imageView22)

            }else{
                Glide.with(this@PostedUserActivity).load(resources.getDrawable(R.drawable.ic_profile)).into(imageView22)
            }

         val count = productDetails?.data?.postedBy?.userStats?.rating
            var list:ArrayList<Boolean> = ArrayList()
            for (i in 0..4){
                if (i < count!!) list.add(true)
                else list.add(false)
//                if (i< count!!){
//
//                }else list.add(false)
            }
            Log.d("Rating", "setupProfile: ${Gson().toJson(list)}")
            binding.imageView23.layoutManager = LinearLayoutManager(this@PostedUserActivity,
                LinearLayoutManager.HORIZONTAL,false)
            binding.imageView23.adapter = RatingAdapter(this@PostedUserActivity,list)

        }
    }
}