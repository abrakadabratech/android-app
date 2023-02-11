package com.oss.abraakadabraaapp.activities.newflow

import LocationFragment
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.ui.home.NewGiverFragment
import com.oss.abraakadabraaapp.databinding.ActivityNewProductDetailBinding
import com.oss.abraakadabraaapp.datasource.products.Product
import com.oss.abraakadabraaapp.response.productdetails.ProductDetailsData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_PRODUCT_DETAILS
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_DELETE_PRODUCT
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_EDIT_PRODUCT
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SHARE_PRODUCT
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewProductDetailActivity : BaseActivity() , OnMapReadyCallback {
    private lateinit var binding: ActivityNewProductDetailBinding

        private var mMap: GoogleMap? = null
    var mMapView: MapView? = null

    private val mainViewModel: AuthViewModel by viewModel()
    private var productDetails: ProductDetailsData? = null
    lateinit var product: Product
    private var reportType = "Inappropriate Content"

    var lattitude = ""
    var longitude = ""

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

        binding.requestBtn.setOnClickListener {
            postClick(Constants.BUTTON_REQUEST_IN_DETAILS_PAGE)

            val intent = Intent(this,PostedUserActivity::class.java)
            intent.putExtra(Constants.PRODUCT,Gson().toJson(productDetails))
            startActivity(intent)
        }

        binding.ivMenu.setOnClickListener {
            postClick(Constants.BUTTON_MENU_IN_DETAILS_PAGE)
            binding.editMenuDialog.visibility = View.VISIBLE
        }

        binding.editProduct.setOnClickListener {
            postClick(BUTTON_EDIT_PRODUCT)
        }

        binding.shareProduct.setOnClickListener {
            postClick(BUTTON_SHARE_PRODUCT)
        }

        binding.deleteProduct.setOnClickListener {
            postClick(BUTTON_DELETE_PRODUCT)
            if (isNetworkAvailable()){
                generateAuthToken()
                mainViewModel.deleteProduct(Utility.getAuthentication(this),productDetails!!.data.id.toString())
            }
        }

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_PRODUCT_DETAILS)
            onBackPressed()
        }

        binding.reportThis.setOnClickListener {
            postClick(Constants.BUTTON_REPORT_THIS_IN_DETAILS_PAGE)
            if (productDetails?.data?.isReported!!){
                showToast("Already Reported")
            }else{
                showReportThisDialog()
            }
        }
        binding.chatBtn.setOnClickListener {
            postClick(Constants.BUTTON_CHAT_IN_DETAILS_PAGE)
            if (productDetails?.data?.requestedStatus!!){
                sendToChat()
            }else{
                showToast("Chat request will be enable after\nyour request is accepted")
            }
        }
    }

    private fun sendToChat() {
        val receiver_id = productDetails?.data?.postedBy?.uid
        val product_id = productDetails?.data?.id
        val sender_id = FirebaseAuth.getInstance().currentUser?.uid

        val db = Firebase.firestore
        var product = productDetails?.data?.name
        val receiver_name = productDetails?.data?.postedBy?.name

        val senderInfo = db.collection("users").document(sender_id.toString())
        senderInfo.get().addOnSuccessListener { doc->
            Log.d("TAG - ", "sendToChat: ${doc.data}")
            Log.d("TAG - ", "sendToChat: ${doc.data?.get("user_avatar")}")
            val userInfo = doc.toObject(UsersData::class.java)!!

            val chat_room = hashMapOf(
                "from" to sender_id,
                "sender_id" to sender_id,
                "sender_name" to userInfo.name,
                "sender_avatar" to doc.data?.get("user_avatar"),
                "receiver_id" to receiver_id,
                "receiver_name" to receiver_name,
                "receiver_avatar" to productDetails?.data?.postedBy?.userAvatar,
                "product_id" to product_id,
                "product_name" to product
            )

            val intent = Intent(this,ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA,chat_room)
            intent.putExtra("data_from","activity")
            startActivity(intent)
        }
    }

    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getProductDetails(map, product.id)
    }

    private fun setUpObserver() {
        mainViewModel.deleteProductSuccess.observe(this){
            if (it.code == 200){
                showToast(it.responseMessage.toString())
                finish()

            }
        }
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

        binding.categoryTxt.setText(it.data.category?.name?.capitalize())
        binding.productName.setText(it.data.name?.capitalize())
        binding.energySaving.setText(it.data.energySaving.toString())
        binding.conditionTxt.setText(it.data.condition)
        binding.usedForTxt.setText(it.data.usedFor)
        binding.costSavingTxt.setText("Rs ${it.data.costSaving}")
        binding.postedByTxt.text = (it.data.postedBy?.name.toString())
        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.text = (it.data.description.toString()?.capitalize())
        binding.locationName.text = (it.data.locationName.toString())

        lattitude = it.data.coordinates?.Latitude.toString()
        longitude = it.data.coordinates?.Longitude.toString()

        val bundle = bundleOf("lat_value" to lattitude,
            "lang_value" to longitude)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<LocationFragment>(R.id.maps_view,args = bundle)
        }

        if (it.data.isRequested!!){
            binding.requestBtn.setText("Requested")
            binding.requestBtn.isEnabled = false
            //binding.chatBtn.isEnabled = true


//            binding.chatBtn.visibility = View.VISIBLE
        }
        if (it.data.isReported!!){
           /* binding.reportThis.setText("Reported")
            binding.reportThis.setTextColor(resources.getColor(R.color.status_declined))*/
            binding.reportThis.isEnabled = false
//            binding.chatBtn.visibility = View.GONE
            binding.chatBtn.isEnabled = false
        }

        if (it.data.requestedStatus!!){
            binding.requestBtn.setText("Accepted")
            binding.requestBtn.isEnabled = false
        }

      /*  binding.mapsView.settings.javaScriptEnabled = true
        binding.mapsView.setWebViewClient(
            WebViewClient());
        binding.mapsView.loadUrl("http://maps.google.com/maps?q=$lattitude,$longitude")
*/
        /*val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.maps_view) as
                SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this@NewProductDetailActivity)*/

    }

     override fun onMapReady(p0: GoogleMap?) {
         /*val latLng = LatLng(lattitude.toDouble(), longitude.toDouble())
         val markerOptions = MarkerOptions().position(latLng).title("I am here!")
         mMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
         mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
         mMap?.addMarker(markerOptions)*/
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
}