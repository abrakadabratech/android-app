package com.oss.abraakadabraaapp.activities.newflow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestedUsersAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyListingDetailsBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_MYLISTING_DETAILS
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyListingDetialActivity : BaseActivity() ,MyRequestedUsersAdapter.OnRequestClicks {
    lateinit var application: BaseActivity
    lateinit var product: RequestData
    private val mainViewModel: AuthViewModel by viewModel()
    var productDetails : ListingResponse? = null
    private lateinit var binding:ActivityMyListingDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product =
            Gson().fromJson(intent.extras?.getString(Constants.PRODUCT, ""), RequestData::class.java)

        application = this
        application.postEvent(Constants.PAGE_MY_LISTING_DETAIL,null)

//        tempData()

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_MYLISTING_DETAILS)
            onBackPressed()
        }

        binding.ivMenu.setOnClickListener {
            postClick(Constants.BUTTON_MENU_IN_DETAILS_PAGE)
            binding.editMenuDialog.visibility = View.VISIBLE
        }

        binding.editProduct.setOnClickListener {
            postClick(Constants.BUTTON_EDIT_PRODUCT)

            binding.productName.requestFocus()
            binding.productName.setSelection(binding.productName.text.toString().length)

            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.productName, InputMethodManager.SHOW_IMPLICIT)

//            binding.productName.isFocusable = true
            binding.updateBtn.visibility = View.VISIBLE
            binding.editMenuDialog.visibility = View.GONE

        }
        binding.editMenuDialog.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
        }

        binding.editCardview.setOnClickListener {
            binding.editMenuDialog.visibility = View.VISIBLE
        }

        binding.updateBtn.setOnClickListener {
            if (binding.productName.text.toString().isNotEmpty() &&
                    binding.descriptionTxt.text.toString().isNotEmpty()){
                val map = HashMap<String,String>()
                map["name"] = binding.productName.text.toString()
                map["description"] = binding.descriptionTxt.text.toString()
                postClick(Constants.BUTTON_DELETE_PRODUCT)
                if (isNetworkAvailable()){
                    generateAuthToken()
                    mainViewModel.updateProduct(Utility.getAuthentication(this),product.id.toString(),map)
                }
            }else{
                showToast("Please enter Product Name and description")
            }

        }

        binding.shareProduct.setOnClickListener {
            postClick(Constants.BUTTON_SHARE_PRODUCT)
        }

        binding.deleteProduct.setOnClickListener {
            postClick(Constants.BUTTON_DELETE_PRODUCT)
            if (isNetworkAvailable()){
                generateAuthToken()
                mainViewModel.deleteProduct(Utility.getAuthentication(this), product.id.toString())
            }
        }
        binding.shareProduct.setOnClickListener {
            loadShareData()
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
        mainViewModel.updateProductSuccess.observe(this){
            if (it.code == 201){
                showToast(it.responseMessage.toString())
                binding.productName.setText( it.data?.name)
                binding.descriptionTxt.setText(it.data?.description)
                binding.productName.isEnabled = false
                binding.descriptionTxt.isEnabled = false
                binding.updateBtn.visibility = View.GONE
            }
        }
        mainViewModel.deleteProductSuccess.observe(this){
            if (it.code == 200){
               finish()
            }
        }
        mainViewModel.listingDetailsuccess.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
                productDetails = it!!
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

        binding.categoryTxt.text = data.category?.name?.capitalize()
        binding.conditionTxt.text = data.condition
        binding.productName.setText(data.name?.capitalize())
        binding.usedForTxt.text = data.usedFor
        binding.costSavingTxt.text = "Rs ${data.costSaving}"
//        binding.responsesOne.text = (data.postedBy.toString())
//        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.setText(data.description?.capitalize().toString())
        binding.locationName.text = (data.locationName.toString())
        binding.responsesOne.text = "${it.requests.size} Responses"
        binding.responsesTwo.text = "${it.requests.size} Responses"

        val adapter = MyRequestedUsersAdapter(this,it.requests,this)
        binding.rvRequestedUsers.layoutManager = LinearLayoutManager(this)
        binding.rvRequestedUsers.adapter = adapter

    }
    private fun loadShareData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test")
        i.putExtra(Intent.EXTRA_TEXT, "Try this great app Abra Ka Dabra to share second hand products with others for free. App is available at the below link: https://play.google.com/store/apps/details?id=com.oss.abraakadabraaapp")
        startActivity(Intent.createChooser(i, "Share"))
    }

    override fun onClick(position:Int) {
        if (productDetails!=null){
            val intent = Intent(this,RequesterActivity::class.java)
            intent.putExtra(Constants.PRODUCT,Gson().toJson(productDetails))
            intent.putExtra("POSTION",position)
            startActivity(intent)

        }
    }

}