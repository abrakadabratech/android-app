package com.oss.abraakadabraaapp.activities.newflow

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestedUsersAdapter
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatListModel
import com.oss.abraakadabraaapp.activities.newflow.ui.FeedbackActivity
import com.oss.abraakadabraaapp.databinding.ActivityRequesterBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.Data1
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestorResponse
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_ACCEPT_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_MARK_AS_DELIVERED_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_OK_GOT_IT_IN_REQUESTER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_OK_GOT_IT_TO_FEEDBACK
import com.oss.abraakadabraaapp.utils.Constants.UNDER_DEV
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RequesterActivity : BaseActivity() {
    private lateinit var binding: ActivityRequesterBinding
    var productDetails: RequestorResponse? = null
    private val mainViewModel: AuthViewModel by viewModel()
    var position: Int = 0
    var requestor_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* productDetails = Gson().fromJson(
            intent.extras?.getString(Constants.PRODUCT, ""),
            ListingResponse::class.java
        )*/
        position = intent.extras?.getInt("POSTION", 0)!!

        setUpObserver()

        if (intent.hasExtra(Constants.productId)){
            requestor_id = intent.extras!!.getString(Constants.productId).toString()
            loaddata()
        }

        if (productDetails != null) {
            //setUpProductDetails(productDetails!!)
        }

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_REQUESTER)
            onBackPressed()
        }
        LocalBroadcastManager.getInstance(this@RequesterActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

        /*binding.payAsYouWish.setOnClickListener {
            postClick(Constants.BUTTON_PAY_AS_YOU_WISH)
            val i = Intent(this, MyPayAsYouGoActivity::class.java)
            i.putExtra("productId",productDetails?.product?.id)
            i.putExtra("product_data",Gson().toJson(productDetial))
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
            startActivity(i)
        }*/

        binding.acceptBtn.setOnClickListener {
            //
            postClick(BUTTON_ACCEPT_IN_REQUESTER)

            if (binding.acceptTxt.text == "Chat") {
                sendToChat()
//                showToast(productDetails?.product?.name.toString())
            } else {
                generateAuthToken()
                mainViewModel.updateProductRequest(
                    Utility.getAuthentication(this),
                    productDetails?.data?.requestId.toString(),
                    "accepted"
                )
            }
        }
        binding.markAsDelivered.setOnClickListener {
            postClick(BUTTON_MARK_AS_DELIVERED_IN_REQUESTER)
            if (binding.markAsDelivered.text.toString().equals("Reject")) {

                var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Alert!")
                alertDialog.setMessage("Are you sure you want to Reject?")

                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                    //cancel the request
                    generateAuthToken()
                    mainViewModel.updateProductRequest(
                        Utility.getAuthentication(this),
                        productDetails?.data?.requestId.toString(),
                        "rejected"
                    )
                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })
                alertDialog.show()

            } else if (binding.markAsDelivered.text.toString().equals("Pay \nAs you Wish")) {
                // under development
                val i = Intent(this, MyPayAsYouGoActivity::class.java)
                i.putExtra("productId", productDetails?.data?.product?.productId)
                i.putExtra("phone", productDetails?.data?.receiverInfo!!.phone)
                i.putExtra("email", productDetails?.data?.receiverInfo!!.email)
                i.putExtra("name", productDetails?.data?.receiverInfo!!.name)
                i.putExtra("receiver_id", productDetails?.data?.request?.userId)
                i.putExtra("product_data", Gson().toJson(productDetails))
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
                startActivity(i)
            } else {
                /*var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Alert!")
                alertDialog.setMessage("Are you sure you want to Deliver?")

                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
                    //cancel the request


                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id ->
                    dialog.dismiss()
                })
                alertDialog.show()*/
                generateAuthToken()
                mainViewModel.updateProductRequest(
                    Utility.getAuthentication(this),
                    productDetails?.data?.requestId.toString(),
                    "delivered"
                )

            }
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
            binding.successLayout2.visibility = View.VISIBLE
        }
        binding.successDialog2.setOnClickListener {
            binding.successLayout2.visibility = View.VISIBLE
        }
        binding.okGotItBtn2.setOnClickListener {
            postClick(BUTTON_OK_GOT_IT_TO_FEEDBACK)
            val i = Intent(this, MyPayAsYouGoActivity::class.java)
            i.putExtra("productId", productDetails?.data?.product?.productId)
            i.putExtra("phone", productDetails?.data?.receiverInfo!!.phone)
            i.putExtra("email", productDetails?.data?.receiverInfo!!.email)
            i.putExtra("name", productDetails?.data?.receiverInfo!!.name)
            i.putExtra("receiver_id", productDetails?.data?.request?.userId)
            i.putExtra("from","listing")
            i.putExtra("product_data", Gson().toJson(productDetails))
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
            startActivity(i)
            finish()

            /* val intent = Intent(this,FeedbackActivity::class.java)
             intent.putExtra("from","listing")
             intent.putExtra("PRODUCT_ID", productDetails?.requests!![position].productId)
             intent.putExtra("USER_ID",productDetails?.requests!![position].userId)
             startActivity(intent)*/
        }
        binding.closeBtn.setOnClickListener {
            binding.successLayout2.visibility = View.GONE

        }
    }
    override fun onBackPressed() {
        if (intent.hasExtra(Constants.hasNotificationData)) {
            startActivity(NewHomeActivity.createIntent(this@RequesterActivity))
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
    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getRequestor(map,requestor_id)
//        mainViewModel.getListingDetails(map, productDetails?.requests!![position].productId!!)
    }

    /*private fun loadAll() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getListingDetails(map, productDetails?.data?.product?.productId!!)
    }*/

    private fun setUpObserver() {

        mainViewModel.getRequestorSuccess.observe(this){
            if (it.code == 200) {

                setUpProductDetails(it)
                productDetails = it!!
            }
        }
        /*mainViewModel.listingDetailsuccess.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {

                productDetails = getDetails(it)!!

                setUpProductDetails(it)
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }*/

        mainViewModel.updateProductRequest.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {

                if (it.data.request_status == "delivered") {
                    binding.successLayout2.visibility = View.VISIBLE
                }
//                productDetails = it!!
                //setUpProductDetails(it)
                loaddata()
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.reportProductSuccess.observe(this) {
            if (it.code == 201) {
                showToast("Product reported")
            } else {
                showToast(it.responseMessage.toString())
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }
    }

    private fun setUpProductDetails(it: RequestorResponse) {
        Log.d("ok", "setUpProductDetails: ${Gson().toJson(it)}")
        when (it.data?.request?.status) {
            "requested" -> {
                binding.status.setText("Requested")
                binding.status.setTextColor(resources.getColor(R.color.status_pending))
                binding.statusIcon.setImageResource(R.drawable.status_pending)
            }
            "accepted" -> {
                binding.status.setText("Accepted")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)

            }
            "rejected" -> {
                binding.status.setText("Declined")
                binding.status.setTextColor(resources.getColor(R.color.status_declined))
                binding.statusIcon.setImageResource(R.drawable.status_declined)
            }
            "received" -> {
                binding.status.setText("Received")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
            }
            "delivered" -> {
                binding.status.setText("Delivered")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
            }
        }


        val imageList = ArrayList<SlideModel>()
        for (i in it.data!!.product?.images!!) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        val data = it.data!!.product!!
        binding.imageSlider.setImageList(imageList)

        binding.categoryTxt.setText(data.category?.name?.capitalize())
        binding.condition.setText(data.condition)
        binding.productName.setText(data.name?.capitalize())
        binding.usedFor.setText(data.usedFor?.capitalize())
        binding.castSaving.setText("Rs ${data.costSaving}")
        binding.energySaving.text = (data.energySaving.toString())
        if (data.brand == null || data.brand == "No Brand" || data.brand == ""){
            binding.brandTxt2.visibility = View.GONE
            binding.some.visibility = View.GONE
        }else{
            binding.brandTxt2.text = (data.brand.toString())
            binding.some.text = "Brand"
        }

//        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.text = (data.description?.capitalize().toString())
        binding.locationName.text = (data.locationName.toString())
        binding.userLocation.text = "location ?"
        binding.userName.text = it.data?.receiverInfo!!.name
        binding.email.text = it.data?.receiverInfo!!.email
//        binding.phone.text = it..receiverInfo.phone

        binding.phone.text = it.data?.receiverInfo!!.phone
        Glide.with(this@RequesterActivity).load(it.data!!.receiverInfo!!.userAvatar)
            .placeholder(resources.getDrawable(R.drawable.user))
            .into(binding.imageView20)
        binding.userLocation.text = getAddress(
            it.data!!.request!!.coordinates?.Latitude?.toDouble()!!,
            it.data!!.request!!.coordinates?.Longitude?.toDouble()!!
        )
        binding.userMessage.text = it.data!!.request!!.message

        if (it.data!!.request!!.status == "accepted") {
            binding.successLayout.visibility = View.VISIBLE
            binding.markAsDelivered.text = "Mark As\nDelivered"
            binding.acceptTxt.text = "Chat"
            binding.acceptBtn.isClickable = true
            binding.chatIcon.visibility = View.VISIBLE
            binding.markAsDelivered.isEnabled = true
            binding.markAsDelivered.setTextColor(resources.getColor(R.color.title_color))
        } else if (it.data!!.request!!.status == "rejected") {
            binding.markAsDelivered.setText("Rejected")
            binding.markAsDelivered.isEnabled = false
            binding.acceptBtn.visibility = View.GONE
            binding.markAsDelivered.visibility = View.GONE
            binding.constraintLayout3.visibility = View.GONE
        } else if (it.data!!.request!!.status == "delivered") {
//            binding.constraintLayout3.visibility = View.GONE
            binding.markAsDelivered.visibility = View.VISIBLE
            binding.markAsDelivered.text = "Pay \nAs you Wish"
            binding.chatIcon.visibility = View.VISIBLE
            binding.markAsDelivered.isEnabled = true
            binding.acceptTxt.text = "Chat"
            binding.acceptBtn.isClickable = true
            binding.markAsDelivered.setTextColor(resources.getColor(R.color.title_color))
//            binding.payAsYouWish.visibility = View.VISIBLE
        } else if (it.data!!.request!!.status == "received") {
            binding.markAsDelivered.text = "Mark As\nDelivered"
            binding.markAsDelivered.setTextColor(resources.getColor(R.color.title_color))
            binding.acceptBtn.isClickable = true
            binding.acceptTxt.text = "Chat"
            binding.chatIcon.visibility = View.VISIBLE
            //binding.successLayout2.visibility = View.VISIBLE
        }

        if (it.data?.request?.isDelivered != null){
            if (it.data?.request?.isDelivered!!) {
                binding.markAsDelivered.visibility = View.VISIBLE
                binding.markAsDelivered.text = "Pay \nAs you Wish"
                binding.chatIcon.visibility = View.VISIBLE
                binding.markAsDelivered.isEnabled = true
                binding.acceptTxt.text = "Chat"
                binding.acceptBtn.isClickable = true
                binding.markAsDelivered.setTextColor(resources.getColor(R.color.title_color))

                binding.status.setText("Delivered")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
            }
        }
//        binding.email.text = it.requests[0].message
//        binding.email.text = it.requests[0].phone
//        binding.responsesOne.text = "${it.requests.size} Responses"
//        binding.responsesTwo.text = "${it.requests.size} Responses"

    }

    private fun sendToChat() {
        val receiver_id = productDetails?.data?.request?.userId
        val product_id = productDetails?.data?.product?.productId
        val sender_id = FirebaseAuth.getInstance().currentUser?.uid

        val db = Firebase.firestore
        var product = productDetails?.data?.product?.name?.capitalize()
        val receiver_name = productDetails?.data?.receiverInfo?.name

        val senderInfo = db.collection("users").document(sender_id.toString())
        senderInfo.get().addOnSuccessListener { doc ->
            Log.d("TAG - ", "sendToChat: ${doc.data}")
            Log.d("TAG - ", "sendToChat: ${doc.data?.get("user_avatar")}")
            val userInfo = doc.toObject(UsersData::class.java)!!

            /* val chat_room = hashMapOf(
                 "from" to sender_id,
                 "sender_id" to sender_id,
                 "sender_name" to doc.data?.get("name"),
                 "sender_avatar" to doc.data?.get("user_avatar"),
                 "receiver_id" to receiver_id,
                 "receiver_name" to receiver_name,
                 "receiver_avatar" to productDetails?.requests!![position].user_avatar,
                 "product_id" to product_id,
                 "product" to product,
                 "product_giver" to sender_id, //Product Giver
                 "product_receiver" to receiver_id //Product Receiver
             )*/
            val chat_room = ChatListModel()
            chat_room.from = sender_id
            chat_room.sender_avatar = doc.data?.get("user_avatar").toString()
            chat_room.sender_id = sender_id
            chat_room.sender_name = doc.data?.get("name").toString()
            chat_room.receiver_id = receiver_id
            chat_room.receiver_name = receiver_name
            chat_room.receiver_avatar = productDetails?.data?.receiverInfo?.userAvatar
            chat_room.product_id = product_id
            chat_room.product = product
            chat_room.product_giver = sender_id
            chat_room.product_receiver = receiver_id
            val intent = Intent(this, ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA, Gson().toJson(chat_room))
            intent.putExtra("data_from", "activity")
            intent.putExtra(Constants.DISPLAY_NAME,receiver_name)
            intent.putExtra(Constants.DISPLAY_PIC,productDetails?.data?.receiverInfo?.userAvatar)
            startActivity(intent)
        }
    }
}