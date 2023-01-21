package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityNewProductDetailBinding
import com.oss.abraakadabraaapp.response.productdetails.Data
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewProductDetailActivity : BaseActivity() /*,OnMapReadyCallback*/{
    private lateinit var binding: ActivityNewProductDetailBinding
//    private var mMap: GoogleMap? = null
    private val mainViewModel: AuthViewModel by viewModel()
    lateinit var productDetails:ProductDetailsData
    lateinit var productId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        postEvent(Constants.PAGE_PRODUCT_DETAILS,null)
        if(intent.hasExtra("id")){
            productId= intent.getStringExtra("id").toString()
            Log.d("TAG-", "onCreate: $productId")
        }
        //tempData()
        loaddata()

//        val mapFragment = supportFragmentMa nager
//            .findFragmentById(R.id.maps_view) as SupportMapFragment?
//        mapFragment!!.getMapAsync(this)
        binding.requestBtn.setOnClickListener {
            postEvent(Constants.BUTTON_REQUEST_IN_DETAILS_PAGE,null)
            startActivity(Intent(this, PostedUserActivity::class.java))
        }

        binding.ivMenu.setOnClickListener {
            postEvent(Constants.BUTTON_MENU_IN_DETAILS_PAGE,null)
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.reportThis.setOnClickListener {
            postEvent(Constants.BUTTON_REPORT_THIS_IN_DETAILS_PAGE,null)
            showReportThisDialog()
        }
        binding.chatBtn.setOnClickListener {
            postEvent(Constants.BUTTON_CHAT_IN_DETAILS_PAGE,null)
            showToast("under development")
        }
    }

    private fun loaddata(){
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getProductDetails(map,productId)

        mainViewModel.productDetailsData.observe(this){
            Log.d("TAG - Product deails", "setUpObserver: ${productDetails.data.description}")
            productDetails = it!!
            setViews(productDetails.data)

        }
    }

    fun setViews(data: Data){
        binding.productName.text=data.name
        binding.productLocation.text=data.locationName
        binding.createdAt.text=data.createdAt
        binding.castSaving.text=data.costSaving.toString()
        binding.energySaving.text=data.energySaving.toString()
        binding.postedBy.text=data.postedBy.name
        binding.condition.text=data.condition
        binding.usedFor.text=data.usedFor
        binding.productDes.text=data.description
        tempData()
    }

    private fun showReportThisDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_report_this, null)
        dialogBuilder.setView(dialogView)

        //Initial Layout
        val initLayout = dialogView.findViewById<ConstraintLayout>(R.id.initLayout)
        val inAppropriateTxt = dialogView.findViewById<TextView>(R.id.inAppropriateTxt)
        val inAppropriateSwitch = dialogView.findViewById<ImageView>(R.id.inAppropriateSwitch)
        val fakeTxt = dialogView.findViewById<TextView>(R.id.fakeTxt)
        val fakeTxtSwitch = dialogView.findViewById<ImageView>(R.id.fakeTxtSwitch)
        val reportClose = dialogView.findViewById<ImageView>(R.id.reportClose)
        val cancelReport = dialogView.findViewById<TextView>(R.id.cancelBtn)
        val okBtn = dialogView.findViewById<TextView>(R.id.okBtn)

        //Submit layout
        val submitLayout = dialogView.findViewById<ConstraintLayout>(R.id.submitLayout)
        val submitBtn = dialogView.findViewById<TextView>(R.id.submitBtn)
        val reportEdt = dialogView.findViewById<EditText>(R.id.reportEdt)

        submitBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SUBMIT_IN_REPORT_THIS_DETAILS_PAGE,null)

            showToast("submit button under development")
        }

        fakeTxt.setOnClickListener {
//            if (fakeTxtSwitch.tag == 1) {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                fakeTxtSwitch.tag = 0
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                inAppropriateSwitch.tag = 1

//            }
    /*else {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                fakeTxtSwitch.tag = 1
            }*/
        }

        fakeTxtSwitch.setOnClickListener {
//            if (fakeTxtSwitch.tag == 1) {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                fakeTxtSwitch.tag = 0
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                inAppropriateSwitch.tag = 1
//            }
    /*else {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                fakeTxtSwitch.tag = 1
            }*/
        }

        inAppropriateTxt.setOnClickListener {
//            if (inAppropriateSwitch.tag == 1) {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                inAppropriateSwitch.tag = 0
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                fakeTxtSwitch.tag = 1
//            }
    /*else {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                inAppropriateSwitch.tag = 1
            }*/
        }
        inAppropriateSwitch.setOnClickListener {
//            if (inAppropriateSwitch.tag == 1) {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                inAppropriateSwitch.tag = 0
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                fakeTxtSwitch.tag = 1
//            }
    /* else {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                inAppropriateSwitch.tag = 1
            }*/
        }


        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        reportClose.setOnClickListener {
            postEvent(Constants.BUTTON_CLOSE_IN_REPORT_THIS_DETAILS_PAGE,null)
            alertDialog.dismiss() }
        cancelReport.setOnClickListener{
            postEvent(Constants.BUTTON_CANCEL_IN_REPORT_THIS_DETAILS_PAGE,null)
            alertDialog.dismiss() }
        okBtn.setOnClickListener {
            //Show sm
            postEvent(Constants.BUTTON_OK_IN_REPORT_THIS_DETAILS_PAGE,null)

            initLayout.visibility = View.GONE
            submitLayout.visibility = View.VISIBLE
        }
        alertDialog.show()
//        alertDialog.window?.setLayout(800, 700)
//        alertDialog.window?.setLayout(800, 700)
    }

    private fun tempData() {
//        Glide.with(this).load("https://www.gstatic.com/webp/gallery/1.jpg").into(binding.imageSlider)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title
        for (image in productDetails.data.images){
            imageList.add(SlideModel(image, "", ScaleTypes.FIT))
        }
        /*imageList.add(SlideModel(productDetails.data.images[0], "", ScaleTypes.FIT))
        imageList.add(
            SlideModel(
                "https://www.gstatic.com/webp/gallery/1.jpg",
                "",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                "https://www.gstatic.com/webp/gallery/1.jpg",
                "",
                ScaleTypes.CENTER_INSIDE
            )
        )
        imageList.add(SlideModel("https://bit.ly/2BteuF2", "", ScaleTypes.CENTER_INSIDE))
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "", ScaleTypes.FIT))*/

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)
    }

   /* override fun onMapReady(p0: GoogleMap?) {
        val userLocation = PreferencesManagement.getUserLocation(this)
        val sydney = LatLng(userLocation!!.lat.toDouble(), userLocation!!.long.toDouble())
        mMap!!.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }*/
}