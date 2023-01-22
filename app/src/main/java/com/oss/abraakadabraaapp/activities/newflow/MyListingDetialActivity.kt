package com.oss.abraakadabraaapp.activities.newflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestedUsersAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyListingBinding
import com.oss.abraakadabraaapp.databinding.ActivityMyListingDetailsBinding
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyListingDetialActivity : BaseActivity() {
    lateinit var application: BaseActivity
    lateinit var product: RequestData
    private val mainViewModel: AuthViewModel by viewModel()

    private lateinit var binding:ActivityMyListingDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product =
            Gson().fromJson(intent.extras?.getString(Constants.PRODUCT, ""), RequestData::class.java)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_LISTING_DETAIL,null)

//        tempData()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        setUpObserver()

        loaddata()

    }

    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getListingDetails(map, product.id!!)
    }

    private fun setUpObserver() {
        mainViewModel.listingDetailsuccess.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
               // productDetails = it!!
                setUpProductDetails(it)
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.reportProductSuccess.observe(this){
            if (it.code == 201){
                showToast("Product reported")
            }else{
                showToast(it.responseMessage.toString())
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }

    private fun setUpProductDetails(it: ListingResponse) {
        val imageList = ArrayList<SlideModel>()
        for (i in it.product?.images!!) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        val data = it.product!!
        binding.imageSlider.setImageList(imageList)

        binding.categoryTxt.setText(data.category)
        binding.conditionTxt.setText(data.condition)
        binding.productName.setText(data.name)
        binding.usedForTxt.setText(data.usedFor)
        binding.costSavingTxt.setText("Rs ${data.costSaving}")
        binding.responsesOne.text = (data.postedBy.toString())
//        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.text = (data.description.toString())
        binding.locationName.text = (data.locationName.toString())
        binding.responsesOne.text = "${it.requests.size} Responses"
        binding.responsesTwo.text = "${it.requests.size} Responses"

    }



    private fun tempData() {
//        Glide.with(this).load("https://www.gstatic.com/webp/gallery/1.jpg").into(binding.imageSlider)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.FIT))
        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "", ScaleTypes.FIT))

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)

        val rvRequestedUsers = findViewById<RecyclerView>(R.id.rvRequestedUsers)
        rvRequestedUsers.layoutManager = LinearLayoutManager(this)

        var adapter = MyRequestedUsersAdapter(this,3)
        rvRequestedUsers.adapter = adapter
    }
}