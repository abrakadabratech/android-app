package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.oss.abraakadabraaapp.activities.newflow.ui.FeedbackActivity
import com.oss.abraakadabraaapp.databinding.ActivityRequesterBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
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
    private lateinit var binding:ActivityRequesterBinding
    var productDetails : ListingResponse? = null
    private val mainViewModel: AuthViewModel by viewModel()
    var position :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequesterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productDetails = Gson().fromJson(intent.extras?.getString(Constants.PRODUCT,""),ListingResponse::class.java)
        position = intent.extras?.getInt("POSTION",0)!!

        setUpObserver()
        loaddata()
        if (productDetails != null){
            setUpProductDetails(productDetails!!)
        }
        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_REQUESTER)
            onBackPressed()
        }
        binding.acceptBtn.setOnClickListener{
            //
            postClick(BUTTON_ACCEPT_IN_REQUESTER)

            if (binding.acceptTxt.text == "Chat"){
                sendToChat()
                showToast(productDetails?.product?.name.toString())
            }else{
                generateAuthToken()
                mainViewModel.updateProductRequest(
                    Utility.getAuthentication(this),
                    productDetails?.requests!![position].requestId.toString(),
                    "accepted"
                )
            }
        }
        binding.markAsDelivered.setOnClickListener {
            postClick(BUTTON_MARK_AS_DELIVERED_IN_REQUESTER)
            if(binding.markAsDelivered.text.toString().equals("Reject")) {

                generateAuthToken()
                mainViewModel.updateProductRequest(
                    Utility.getAuthentication(this),
                    productDetails?.requests!![position].requestId.toString(),
                    "rejected"
                )
            }
            else {
                binding.successLayout2.visibility = View.VISIBLE
                generateAuthToken()
                mainViewModel.updateProductRequest(
                    Utility.getAuthentication(this),
                    productDetails?.requests!![position].requestId.toString(),
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
            binding.successLayout2.visibility = View.GONE
        }
        binding.successDialog2.setOnClickListener {
            binding.successLayout2.visibility = View.VISIBLE
        }
        binding.okGotItBtn2.setOnClickListener {
            postClick(BUTTON_OK_GOT_IT_TO_FEEDBACK)
            val intent = Intent(this,FeedbackActivity::class.java)
            intent.putExtra("from","listing")
            intent.putExtra("PRODUCT_ID", productDetails?.requests!![position].productId)
            intent.putExtra("USER_ID",productDetails?.requests!![position].userId)
            startActivity(intent)
        }
        binding.closeBtn.setOnClickListener{
            binding.successLayout2.visibility = View.GONE

        }
    }
    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getListingDetails(map, productDetails?.requests!![position].productId!!)
    }
    private fun setUpObserver() {

        mainViewModel.listingDetailsuccess.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
                productDetails = it!!

                setUpProductDetails(it)
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }

        mainViewModel.updateProductRequest.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
//                productDetails = it!!
                //setUpProductDetails(it)
                loaddata()
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

        binding.categoryTxt.setText(data.category?.name?.capitalize())
        binding.condition.setText(data.condition)
        binding.productName.setText(data.name?.capitalize())
        binding.usedFor.setText(data.usedFor?.capitalize())
        binding.castSaving.setText("Rs ${data.costSaving}")
        binding.energySaving.text = (data.energySaving.toString())
//        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.text = (data.description?.capitalize().toString())
        binding.locationName.text = (data.locationName.toString())
        binding.userLocation.text = "location ?"
        binding.userName.text = it.requests[position].username
        binding.email.text = it.requests[position].email
        binding.phone.text = it.requests[position].phone
        binding.userLocation.text = getAddress(it.requests[position].coordinates?.Latitude?.toDouble()!!,
            it.requests[position].coordinates?.Longitude?.toDouble()!!
        )
        binding.userMessage.text = it.requests[position].message

        if (it.requests[position].status == "accepted"){
            binding.successLayout.visibility = View.VISIBLE
            binding.markAsDelivered.text = "Mark As\nDelivered"
            binding.acceptTxt.text = "Chat"
            binding.acceptBtn.isClickable = true
            binding.chatIcon.visibility = View.VISIBLE
            binding.markAsDelivered.isEnabled = true
            binding.markAsDelivered.setTextColor(resources.getColor(R.color.title_color))
        }else if(it.requests[position].status == "rejected"){
            binding.markAsDelivered.setText("Rejected")
            binding.markAsDelivered.isEnabled = false
            binding.acceptBtn.visibility = View.GONE
        }else if(it.requests[position].status == "delivered" || it.requests[position].status == "received"){
            binding.markAsDelivered.setText("Delivered")
            //binding.successLayout2.visibility = View.VISIBLE
            binding.constraintLayout3.visibility = View.GONE
        }
//        binding.email.text = it.requests[0].message
//        binding.email.text = it.requests[0].phone
//        binding.responsesOne.text = "${it.requests.size} Responses"
//        binding.responsesTwo.text = "${it.requests.size} Responses"

    }
    private fun sendToChat() {
        val receiver_id = productDetails?.requests!![position].userId
        val product_id = productDetails?.product?.id
        val sender_id = FirebaseAuth.getInstance().currentUser?.uid

        val db = Firebase.firestore
        var product = productDetails?.product?.name?.capitalize()
        val receiver_name = productDetails?.requests!![position].username

        val senderInfo = db.collection("users").document(sender_id.toString())
        senderInfo.get().addOnSuccessListener { doc->
            Log.d("TAG - ", "sendToChat: ${doc.data}")
            Log.d("TAG - ", "sendToChat: ${doc.data?.get("user_avatar")}")
            val userInfo = doc.toObject(UsersData::class.java)!!

            val chat_room = hashMapOf(
                "from" to sender_id,
                "sender_id" to sender_id,
                "sender_name" to doc.data?.get("user_avatar"),
                "sender_avatar" to doc.data?.get("user_avatar"),
                "receiver_id" to receiver_id,
                "receiver_name" to receiver_name,
                "receiver_avatar" to productDetails?.requests!![position].user_avatar,
                "product_id" to product_id,
                "product" to product
            )

            val intent = Intent(this, ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA,chat_room)
            intent.putExtra("data_from","activity")
            startActivity(intent)
        }
    }
}