package com.oss.abraakadabraaapp.activities.newflow.ui

import RequestDetails
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyPayAsYouGoActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.apimodels.UsersData
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatListModel
import com.oss.abraakadabraaapp.activities.newflow.model.NotificationDataModel
import com.oss.abraakadabraaapp.databinding.ActivityMyRequestingDetailBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.Data
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_PAY_AS_YOU_WISH
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale


class MyRequestDetailsActivity : BaseActivity() {
    private var productDetial: RequestDetails? = null
    lateinit var application: BaseActivity
    private val mainViewModel: AuthViewModel by viewModel()
    private lateinit var productId: String

    private lateinit var binding:ActivityMyRequestingDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRequestingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_REQUEST_DETAILS,null)

        if (intent.hasExtra(Constants.productId)) {
            productId = intent.getStringExtra(Constants.productId)!!
        }
        if (intent.hasExtra(Constants.hasNotificationData)) {
            var bundle  = Gson().fromJson(intent.getStringExtra(Constants.productId).toString(),
                NotificationDataModel::class.java)
            productId = bundle.requestId.toString()
            val db = Firebase.firestore
            db.collection("notifications")
                .document(bundle.notificationDoc.toString())
                .update("deleted", true)
            loaddata()
        }


        LocalBroadcastManager.getInstance(this@MyRequestDetailsActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

        clickEvents()

        setUpObserver()

        loaddata()

        Log.d("TAG - ", "onCreate: product ID ${productDetial?.data?.productId}")
//        Log.d("TAG - ", "onCreate: User ID ${product.postedBy?.uid}")
//        tempData()
    }

    private fun clickEvents() {

        binding.chatBtn.setOnClickListener{

            if (productDetial?.data?.requestStatus == "accepted" || productDetial?.data?.requestStatus == "received"
                || productDetial?.data?.requestStatus == "delivered"){
                val sender_id = FirebaseAuth.getInstance().currentUser?.uid

                val db = Firebase.firestore

                val senderInfo = db.collection("users").document(sender_id.toString())
                senderInfo.get().addOnSuccessListener { doc ->
                    Log.d("TAG - ", "sendToChat: ${doc.data}")
                    Log.d("TAG - ", "sendToChat: ${doc.data?.get("user_avatar")}")
                    val chat_room = ChatListModel()
                    chat_room.from = sender_id
                    chat_room.product_id = productDetial?.data!!.productId
                    chat_room.product = productDetial?.data?.name?.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
                    chat_room.product_giver = productDetial?.data?.postedBy?.id
                    chat_room.product_receiver = sender_id
                    chat_room.receiver_id = sender_id
                    chat_room.receiver_avatar = doc.data?.get("user_avatar").toString()
                    chat_room.receiver_name = doc.data?.get("name").toString()
                    chat_room.sender_avatar = productDetial?.data!!.postedBy?.userAvatar
                    chat_room.sender_id = productDetial?.data?.postedBy?.id
                    chat_room.sender_name = productDetial?.data?.postedBy?.name
                    chat_room.requestId = productDetial?.data?.request_id.toString()
                    chat_room.time_stamp = Timestamp.now()
                    chat_room.status = productDetial?.data?.status
                    if (productDetial?.data?.images?.size!! > 0) {
                        chat_room.product_image = productDetial?.data?.images!![0]
                    }

                    val intent = Intent(this, ChatDetailActivity::class.java)
                    intent.putExtra(Constants.CHATS_DATA, Gson().toJson(chat_room))
                    intent.putExtra("data_from", "activity")
                    intent.putExtra(Constants.DISPLAY_NAME, productDetial?.data?.postedBy?.name)
                    intent.putExtra(
                        Constants.DISPLAY_PIC,
                        productDetial?.data?.postedBy?.userAvatar
                    )
                    startActivity(intent)
                }


            }else{
                showToast("Product not accepted yet!")
            }
            postClick(Constants.BUTTON_CHAT_IN_REQUEST_DETAILS)

        }

        binding.ivBack.setOnClickListener {
            postClick(Constants.BUTTON_BACK_IN_MYLISTING_DETAILS)
            onBackPressed()
        }
        binding.markAsDelivered.setOnClickListener {
            //navigates to feedback pages...
            postClick(Constants.BUTTON_MARK_AS_DELIVERED)
            val status = binding.markAsDelivered.text.toString()
            if (status == "Cancel"){
                // cancel the request
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Cancel")
                alertDialog.setMessage("Are you sure you want to cancel request on this product ?")

                alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener{dialog, id ->
                    //cancel the request
                    mainViewModel.cancelProductRequest(productId
                    )
                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener{dialog, id ->
                    dialog.dismiss()
                })
                alertDialog.show()
            } else if(status == "Pay\nAs you wish"){
                val i = Intent(this, MyPayAsYouGoActivity::class.java)
                i.putExtra("productId",productDetial?.data?.productId)
                i.putExtra("phone", productDetial?.data?.postedBy?.phone)
                i.putExtra("email", productDetial?.data?.postedBy?.email)
                i.putExtra("name", productDetial?.data?.postedBy?.name)
                i.putExtra("product_data",Gson().toJson(productDetial))
                startActivity(i)
            }
            else /*if(status == "Mark As\\nReceived"){*/
            //send mark as delivered
            {
                mainViewModel.updateProductRequest(
                    productDetial?.data?.request_id.toString(),
                    "received"
                )

            }
        }
        binding.editRequest.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            postClick(Constants.BUTTON_EDIT_REQUEST)
            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Cancel")
            alertDialog.setMessage("Are you sure you want to cancel request on this product ?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener{dialog, id ->
                dialog.dismiss()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener{dialog, id ->
                dialog.dismiss()
            })
            if (isChecked){
                alertDialog.show()
            }else{

            }
        })
        binding.payAsYouWish.setOnClickListener {
            postClick(BUTTON_PAY_AS_YOU_WISH)
            val i = Intent(this, MyPayAsYouGoActivity::class.java)
            i.putExtra("productId",productId)
            i.putExtra("product_data",Gson().toJson(productDetial))
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
            startActivity(i)
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
    override fun onBackPressed() {
        if (intent.hasExtra(Constants.hasNotificationData)) {
            startActivity(NewHomeActivity.createIntent(this@MyRequestDetailsActivity))
        }else{
            super.onBackPressed()
        }
    }

    private fun loaddata() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val idToken = it.result.token
                    val auth = "Bearer $idToken"
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.shimmerLayout.startShimmer()
                    val map = HashMap<String, String>()
                    map[RequestKeys.authorization] = auth
                    mainViewModel.getRequestDetails(map,productId)
                }
            }

    }

    private fun setUpObserver() {
        mainViewModel.updateProductRequest.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {

                if (it.data.request_status == "received"){

                    val i = Intent(this, MyPayAsYouGoActivity::class.java)
                    i.putExtra("from","requesting")
                    i.putExtra("productId",productDetial?.data?.productId)
                    i.putExtra("phone", productDetial?.data?.postedBy?.phone)
                    i.putExtra("email", productDetial?.data?.postedBy?.email)
                    i.putExtra("name", productDetial?.data?.postedBy?.name)
                    i.putExtra("product_data",Gson().toJson(productDetial))
                    i.putExtra("receiver_id",productDetial?.data?.postedBy?.id)
                    startActivity(i)

                  /*  val intent = Intent(this,FeedbackActivity::class.java)
                    intent.putExtra("from","requesting")
                    intent.putExtra("PRODUCT_ID", productDetial?.data?.productId)
                    intent.putExtra("USER_ID",productDetial?.data?.postedBy?.id)
                    startActivity(intent)*/
                }

//                productDetails = it!!
                //setUpProductDetails(it)
                loaddata()
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.initPaymentSuccess.observe(this){
            if (it.code == 200){
                showToast("Order generated")
            }
        }

        mainViewModel.cancelRequestSuccess.observe(this){
            if (it.code == 200) {
                showToast(it.responseMessage.toString())
                finish()
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.requestDetailsuccess.observe(this) {
            binding.shimmerLayout.visibility = View.GONE
            binding.shimmerLayout.stopShimmer()
            if (it.code == 200) {
                setUpProductDetails(it)
                productDetial = it

                Log.d("TAG - ", "onCreate: product ID ${productDetial?.data?.productId}")
//                Log.d("TAG - ", "onCreate: User ID ${product.postedBy?.uid}")

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

        mainViewModel.errorMessage.observe(this) {
           // if (it.isNotBlank()) showToast(it)
        }

        mainViewModel.isLoading.observe(this) {
            loader(it)
        }

    }

    private fun setUpProductDetails(it: RequestDetails) {

        when (it.data?.requestStatus) {
            "requested" -> {
                binding.status.setText("Requested")
                binding.status.setTextColor(resources.getColor(R.color.status_pending))
                binding.statusIcon.setImageResource(R.drawable.status_pending)
                binding.markAsDelivered.setText("Cancel")
                binding.chatBtn.background = (resources.getDrawable(R.drawable.chat_disabled_bg))
//                binding.chatBtn.isEnabled = false
            }

            "accepted" -> {
                binding.status.setText("Accepted")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
                binding.markAsDelivered.setText("Mark As\nReceived")
//                binding.payAsYouWish.visibility = View.VISIBLE
                binding.chatBtn.isEnabled = true
                binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))

            }

            "rejected" -> {
                binding.status.setText("Declined")
                binding.status.setTextColor(resources.getColor(R.color.status_declined))
                binding.statusIcon.setImageResource(R.drawable.status_declined)
                binding.markAsDelivered.visibility = View.GONE
//                binding.payAsYouWish.visibility = View.GONE
                binding.markAsDelivered.setText("Rejected")
                binding.chatBtn.isEnabled = false
                binding.chatBtn.background = (resources.getDrawable(R.drawable.chat_disabled_bg))
                //hiding the accept and chat button
                binding.constraintLayout3.visibility = View.GONE
            }

            "delivered" -> {
                binding.status.setText("Delivered")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
//                binding.payAsYouWish.visibility = View.VISIBLE
                binding.chatBtn.isEnabled = true
                binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))
                // binding.markAsDelivered.setText("Pay\nAs you wish")
//                binding.payAsYouWish.visibility = View.VISIBLE
                binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))
//                binding.constraintLayout3.visibility = View.GONE
            }

            "received" -> {
                binding.status.setText("Received")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
//                binding.chatBtn.isEnabled = true
                binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))
//                binding.payAsYouWish.visibility = View.VISIBLE
                binding.markAsDelivered.setText("Pay\nAs you wish")
                binding.chatBtn.isEnabled = true
                binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))
//                binding.constraintLayout3.visibility = View.GONE
            }

            else -> {

                binding.status.setText("Cancelled")
                binding.status.setTextColor(resources.getColor(R.color.status_declined))
                binding.statusIcon.setImageResource(R.drawable.status_declined)
                binding.markAsDelivered.visibility = View.GONE
//                binding.payAsYouWish.visibility = View.GONE
//                binding.markAsDelivered.setText("Rejected")
                binding.chatBtn.isEnabled = false
                binding.chatBtn.background = (resources.getDrawable(R.drawable.chat_disabled_bg))
                //hiding the accept and chat button
                binding.constraintLayout3.visibility = View.GONE

            }

        }

        if (it.data?.isReceived!!) {
            binding.status.setText("Received")
            binding.status.setTextColor(resources.getColor(R.color.status_accepted))
            binding.statusIcon.setImageResource(R.drawable.status_accepted)
//                binding.chatBtn.isEnabled = true
            binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))
//                binding.payAsYouWish.visibility = View.VISIBLE
            binding.markAsDelivered.setText("Pay\nAs you wish")
            binding.chatBtn.isEnabled = true
            binding.chatBtn.background = (resources.getDrawable(R.drawable.btn_bg_rounded_rect))
        }


        val imageList = ArrayList<SlideModel>()
        for (i in it.data?.images!!) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        binding.imageSlider.setImageList(imageList)

        Glide.with(this).load(it.data!!.postedBy?.userAvatar)
            .placeholder(resources.getDrawable(R.drawable.user))
            .into(binding.imageView20)

        binding.categoryTxt.setText(it.data?.category?.name?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        })
        binding.productName.setText(it.data?.name?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        })
        binding.condition.setText(it.data?.condition)
        binding.usedFor.setText(it.data?.usedFor)
        binding.castSaving.setText("Rs ${it.data?.costSaving}")
        binding.energySaving.text = (it.data?.energySaving?.toString())
        binding.prodcutLocation.text = (it.data?.locationName.toString())
        binding.descriptionTxt.text = (it.data?.description.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        binding.postedUserName.text = (it.data?.postedBy?.name.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
        binding.phone.text = (it.data?.postedBy?.phone.toString())
        binding.email.text = (it.data?.postedBy?.email.toString())
        binding.postedLocation.text = (it.data?.locationName.toString())
//        binding.locationName.text = (it.data.locationName.toString())

    }
}