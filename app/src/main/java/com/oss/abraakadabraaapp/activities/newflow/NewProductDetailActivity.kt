package com.oss.abraakadabraaapp.activities.newflow

import LocationFragment
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatListModel
import com.oss.abraakadabraaapp.activities.newflow.model.NotificationDataModel
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
import okhttp3.internal.notify
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewProductDetailActivity : BaseActivity() , OnMapReadyCallback {
    private lateinit var binding: ActivityNewProductDetailBinding
    private lateinit var productId: String


    private val mainViewModel: AuthViewModel by viewModel()
    private var productDetails: ProductDetailsData? = null
    private var reportType = "Inappropriate Content"

    var lattitude = ""
    var longitude = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        postEvent(Constants.PAGE_PRODUCT_DETAILS, null)

        if (intent.hasExtra(Constants.productId)) {
            productId = intent.getStringExtra(Constants.productId)!!
        }

        if (intent.hasExtra(Constants.hasNotificationData)){
            val bundle = Gson().fromJson(intent.extras?.getString(Constants.productId),NotificationDataModel::class.java)
            productId = bundle.product_id.toString()
            val db = Firebase.firestore
            if (bundle.notificationDoc.toString() != ""){
                db.collection("notifications")
                    .document(bundle.notificationDoc.toString())
                    .update("deleted", true)

            }
        }

        setUpObserver()

        loaddata()

        binding.requestBtn.setOnClickListener {
            postClick(Constants.BUTTON_REQUEST_IN_DETAILS_PAGE)
            val userInfo = PreferencesManagement.getUserInfo(this)
            if (userInfo?.data?.status == "not verified"){
                //Show a pop up that is not verified yet
                showNotActivePopUp()
            }else if (userInfo?.data?.status == "pending"){
                showPendingPopUp()
            }else{
                val intent = Intent(this,PostedUserActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(Constants.PRODUCT,Gson().toJson(productDetails))
                startActivity(intent)
            }
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
//            ""
            loadShareData()
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
            if (productDetails?.data?.isRequested!!){
                if (productDetails?.data?.requestedStatus!!){
                    sendToChat()
                }else{
                    showToast("Chat request will be enable after\nyour request is accepted")
                }
            }else{
                showToast("Product not accepted yet!")
            }
            postClick(Constants.BUTTON_CHAT_IN_DETAILS_PAGE)

        }
        LocalBroadcastManager.getInstance(this@NewProductDetailActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))
    }

    override fun onBackPressed() {
        if (intent.hasExtra(Constants.hasNotificationData)) {
            startActivity(NewHomeActivity.createIntent(this@NewProductDetailActivity))
        }else{
            super.onBackPressed()
        }
    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {
                    loaddata()
                }
            }
        }
    }
    private fun loadShareData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Share Product")
        i.putExtra(Intent.EXTRA_TEXT, "Check out the product I have listed on this great app Abra Ka Dabra where we can share second hand products with others for free: https://play.google.com/store/apps/details?id=com.oss.abraakadabraaapp")
        startActivity(Intent.createChooser(i, "Share"))
    }

    private fun showNotActivePopUp() {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Alert!")
        alertDialog.setMessage("To request products you must be a verified user, please submit your any social profile link to start verification.")

        alertDialog.setPositiveButton("Submit") { dialog, id ->
            //cancel the request
            val intent = Intent(this,MyNewProfileActivity::class.java)
            intent.putExtra("from","activity")
            startActivity(intent)
        }
        alertDialog.show()
    }
    private fun showPendingPopUp() {
        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Alert!")
        alertDialog.setMessage("Your profile is pending for verification please wait till it's get verified, thank you.")

        alertDialog.setPositiveButton("Ok") { dialog, id ->
            //cancel the request
           dialog.dismiss()
        }
        alertDialog.show()
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

            var sender_avatar = doc.data?.get("user_avatar")

            val chat_room = ChatListModel()
            chat_room.from = sender_id
            chat_room.sender_avatar = productDetails?.data?.postedBy?.userAvatar
            chat_room.sender_id = receiver_id
            chat_room.sender_name = receiver_name
            chat_room.receiver_id = sender_id
            chat_room.receiver_name = userInfo.name
            chat_room.receiver_avatar = sender_avatar.toString()
            chat_room.product_id = product_id
            chat_room.product = product

            /*val chat_room = hashMapOf(
                "from" to sender_id,
                "sender_id" to sender_id,
                "sender_name" to userInfo.name,
                "sender_avatar" to doc.data?.get("user_avatar"),
                "receiver_id" to receiver_id,
                "receiver_name" to receiver_name,
                "receiver_avatar" to productDetails?.data?.postedBy?.userAvatar,
                "product_id" to product_id,
                "product_name" to product
            )*/

            val intent = Intent(this,ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA,Gson().toJson(chat_room))
            intent.putExtra("data_from","activity")
            intent.putExtra(Constants.DISPLAY_NAME,productDetails?.data?.postedBy?.name)
            intent.putExtra(Constants.DISPLAY_PIC,productDetails?.data?.postedBy?.userAvatar)
            startActivity(intent)
        }
    }

    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getProductDetails(map, productId)
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
                productDetails?.data?.isReported = true
            }else{
                showToast(it.responseMessage.toString())
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }

    private fun setUpProductDetails(it: ProductDetailsData) {
        val imageList = ArrayList<SlideModel>()

        if(it.data.images.size > 0){
            for (i in it.data.images) {
                imageList.add(SlideModel(i, "", ScaleTypes.FIT))
            }
        }else{
            imageList.add(SlideModel(R.drawable.no_image,"",ScaleTypes.FIT))
        }

        binding.imageSlider.setImageList(imageList)

        binding.categoryTxt.setText(it.data.category?.name?.capitalize())
        binding.productName.setText(it.data.name?.capitalize())
        binding.energySaving.setText("${if(it.data.energySaving != null) it.data.energySaving else 0}")
        binding.conditionTxt.setText(it.data.condition)
        binding.usedForTxt.setText(it.data.usedFor)
        binding.costSavingTxt.setText("Rs ${if(it.data.costSaving != null) it.data.costSaving else 0}")
        binding.postedByTxt.text = (it.data.postedBy?.name.toString())
        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.text = (it.data.description.toString()?.capitalize())
        binding.locationName.text = (it.data.locationName.toString())
        if (it.data.brand == null || it.data.brand == "No Brand" || it.data.brand == ""){
            binding.brandTxt.visibility = View.GONE
            binding.some111.visibility = View.GONE
        }else{
            binding.brandTxt.text = (it.data.brand.toString())
        }

        lattitude = it.data.coordinates?.Latitude.toString()
        longitude = it.data.coordinates?.Longitude.toString()

        val bundle = bundleOf("lat_value" to lattitude,
            "lang_value" to longitude,
        "title" to productDetails?.data?.name)
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
//            binding.reportThis.isEnabled = false
//            binding.chatBtn.visibility = View.GONE
//            binding.chatBtn.isEnabled = false
            binding.chatBtn.background = resources.getDrawable(R.drawable.white_chat_disabled_bg)

        }

        if (it.data.requestedStatus!!){
            binding.requestBtn.setText("Accepted")
            binding.requestBtn.isEnabled = false
            binding.chatBtn.background = resources.getDrawable(R.drawable.rounded_rect_white_gray_stroke)

        }


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

            if (reportEdt.text.toString() == ""){
                showToast("Please enter some text")
            }else{
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
        }
        alertDialog.show()
//        alertDialog.window?.setLayout(800, 700)
//        alertDialog.window?.setLayout(800, 700)
    }
}