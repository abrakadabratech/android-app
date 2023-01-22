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
import com.oss.abraakadabraaapp.datasource.products.Product
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.requests.ReportProductRequest
import com.oss.abraakadabraaapp.databinding.ActivityNewProductDetailBinding
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewProductDetailActivity : BaseActivity() /*,OnMapReadyCallback*/ {
    private lateinit var binding: ActivityNewProductDetailBinding

    //    private var mMap: GoogleMap? = null
    private val mainViewModel: AuthViewModel by viewModel()
    private var productDetails: ProductDetailsData? = null
    lateinit var product: Product
    private var reportType = "Inappropriate Content"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        product =
            Gson().fromJson(intent.extras?.getString(Constants.PRODUCT, ""), Product::class.java)

        Log.d("TAG - ", "onProductClicked: receives ${Gson().toJson(product)}")

        postEvent(Constants.PAGE_PRODUCT_DETAILS, null)

        setUpObserver()

        loaddata()

//        val mapFragment = supportFragmentMa nager
//            .findFragmentById(R.id.maps_view) as SupportMapFragment?
//        mapFragment!!.getMapAsync(this)
        binding.requestBtn.setOnClickListener {
            postEvent(Constants.BUTTON_REQUEST_IN_DETAILS_PAGE, null)
            if (isNetworkAvailable()){
//                mainViewModel.reportProduct()
            }
            startActivity(Intent(this, PostedUserActivity::class.java))
        }

        binding.ivMenu.setOnClickListener {
            postEvent(Constants.BUTTON_MENU_IN_DETAILS_PAGE, null)
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.reportThis.setOnClickListener {
            postEvent(Constants.BUTTON_REPORT_THIS_IN_DETAILS_PAGE, null)
            showReportThisDialog()
        }
        binding.chatBtn.setOnClickListener {
            postEvent(Constants.BUTTON_CHAT_IN_DETAILS_PAGE, null)
            showToast("under development")
        }
    }

    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getProductDetails(map, product.id)
    }

    private fun setUpObserver() {
        mainViewModel.productDetailsData.observe(this) {
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

    private fun setUpProductDetails(it: ProductDetailsData) {
        val imageList = ArrayList<SlideModel>()
        for (i in it.data.images) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        binding.imageSlider.setImageList(imageList)

        binding.categoryTxt.setText(it.data.category)
        binding.productName.setText(it.data.name)
        binding.conditionTxt.setText(it.data.condition)
        binding.usedForTxt.setText(it.data.usedFor)
        binding.costSavingTxt.setText("Rs ${it.data.costSaving}")
        binding.postedByTxt.text = (it.data.postedBy.toString())
        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.text = (it.data.description.toString())
        binding.locationName.text = (it.data.locationName.toString())

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


        fakeTxt.setOnClickListener {
            fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_select)
            fakeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            fakeTxtSwitch.tag = 0
            inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
            inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            inAppropriateSwitch.tag = 1

            reportType = "Spam / Fake ad"

        }

        fakeTxtSwitch.setOnClickListener {
            fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_select)
            fakeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            fakeTxtSwitch.tag = 0
            inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
            inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            inAppropriateSwitch.tag = 1

            reportType = "Spam / Fake ad"

        }

        inAppropriateTxt.setOnClickListener {
            inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_select)
            inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            inAppropriateSwitch.tag = 0
            fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
            fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            fakeTxtSwitch.tag = 1

            reportType = "Inappropriate Content"

        }
        inAppropriateSwitch.setOnClickListener {
            inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_select)
            inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            inAppropriateSwitch.tag = 0
            fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
            fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
            fakeTxtSwitch.tag = 1

            reportType = "Inappropriate Content"

        }


        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        reportClose.setOnClickListener {
            postEvent(Constants.BUTTON_CLOSE_IN_REPORT_THIS_DETAILS_PAGE, null)
            alertDialog.dismiss()
        }
        cancelReport.setOnClickListener {
            postEvent(Constants.BUTTON_CANCEL_IN_REPORT_THIS_DETAILS_PAGE, null)
            alertDialog.dismiss()
        }
        okBtn.setOnClickListener {
            //Show sm
            postEvent(Constants.BUTTON_OK_IN_REPORT_THIS_DETAILS_PAGE, null)

            initLayout.visibility = View.GONE
            submitLayout.visibility = View.VISIBLE
        }
        submitBtn.setOnClickListener {
            postEvent(Constants.BUTTON_SUBMIT_IN_REPORT_THIS_DETAILS_PAGE, null)

            generateAuthToken()

            val map = HashMap<String, String>()
            val token = PreferencesManagement.getAuthToken(this)!!
            map[RequestKeys.authorization] = token

            if (productDetails!=null){
                if (isNetworkAvailable()){
//                    val request = ReportProductRequest(productDetails!!.data.id,reportType,reportEdt.text.toString())

                    val requestMap = HashMap<String,String>()
                    requestMap["productId"] = productDetails!!.data.id.toString()
                    requestMap["type"] = reportType
                    requestMap["message"] = reportEdt.text.toString()


                    mainViewModel.reportProduct(map, requestMap)
                }

            }
            alertDialog.dismiss()
        }
        alertDialog.show()
//        alertDialog.window?.setLayout(800, 700)
//        alertDialog.window?.setLayout(800, 700)
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