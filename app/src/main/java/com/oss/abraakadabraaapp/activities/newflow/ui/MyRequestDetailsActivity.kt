package com.oss.abraakadabraaapp.activities.newflow.ui

import RequestDetails
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyPayAsYouGoActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyRequestingDetailBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.Data
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_PAY_AS_YOU_WISH
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MyRequestDetailsActivity : BaseActivity() {
    private var productDetial: RequestDetails? = null
    lateinit var application: BaseActivity
    lateinit var product: Data
    private val mainViewModel: AuthViewModel by viewModel()

    private lateinit var binding:ActivityMyRequestingDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRequestingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_REQUEST_DETAILS,null)

        product =
            Gson().fromJson(intent.extras?.getString(Constants.PRODUCT, ""), Data::class.java)
        Log.d("ok", "onCreate in linsting activity: $${Gson().toJson(product)}")


        binding.chatBtn.setOnClickListener{
            postClick(Constants.BUTTON_CHAT_IN_REQUEST_DETAILS)
            val sender_id = FirebaseAuth.getInstance().currentUser?.uid

            val db = Firebase.firestore

            val chat_room = hashMapOf(
                "receiver_id" to productDetial?.data?.postedBy?.id,
                "sender_id" to sender_id,
                "product_id" to product.product?.id,
                "receiver_name" to  productDetial?.data?.postedBy?.name,
                "product" to productDetial?.data?.name,
                "user_avatar" to product.postedBy?.userAvatar
            )
            val intent = Intent(this, ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA,chat_room)
            Log.d("ok", "sending to chat activity: $${Gson().toJson(chat_room)}")

            intent.putExtra("data_from","activity")
            startActivity(intent)
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
                    mainViewModel.cancelProductRequest(Utility.getAuthentication(this),
                        product.id.toString()
                    )
                    dialog.dismiss()
                })
                alertDialog.setNegativeButton("No", DialogInterface.OnClickListener{dialog, id ->
                    dialog.dismiss()
                })
                alertDialog.show()
            }else /*if(status == "Mark As\\nReceived"){*/
                //send mark as delivered
            {
                generateAuthToken()
                mainViewModel.updateProductRequest(
                    Utility.getAuthentication(this),
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
            i.putExtra("productId",product.id)
            i.putExtra("product_data",Gson().toJson(productDetial))
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
            startActivity(i)
        }
        setUpObserver()

        loaddata()

        Log.d("TAG - ", "onCreate: product ID ${productDetial?.data?.productId}")
        Log.d("TAG - ", "onCreate: User ID ${product.postedBy?.uid}")
//        tempData()
    }
    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getRequestDetails(map, product.id!!)
    }

    private fun setUpObserver() {
        mainViewModel.updateProductRequest.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {

                if (it.data.request_status == "received"){
                    val intent = Intent(this,FeedbackActivity::class.java)
                    intent.putExtra("from","requesting")
                    intent.putExtra("PRODUCT_ID", productDetial?.data?.productId)
                    intent.putExtra("USER_ID",productDetial?.data?.postedBy?.id)
                    startActivity(intent)
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

            if (it.code == 200) {
                setUpProductDetails(it)
                productDetial = it

                Log.d("TAG - ", "onCreate: product ID ${productDetial?.data?.productId}")
                Log.d("TAG - ", "onCreate: User ID ${product.postedBy?.uid}")

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
            if (it.isNotBlank()) showToast(it)
        }

        mainViewModel.isLoading.observe(this) {
            loader(it)
        }

    }

    private fun setUpProductDetails(it: RequestDetails) {

        when(it.data?.requestStatus){
            "requested" -> {
                binding.status.setText("Requested")
                binding.status.setTextColor(resources.getColor(R.color.status_pending))
                binding.statusIcon.setImageResource(R.drawable.status_pending)
                binding.markAsDelivered.setText("Cancel")
                binding.chatBtn.isEnabled = false
            }
            "accepted" -> {
                binding.status.setText("Accepted")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
                binding.markAsDelivered.setText("Mark As\nReceived")
                binding.payAsYouWish.visibility = View.VISIBLE
                binding.chatBtn.isEnabled = true

            }
            "rejected" -> {
                binding.status.setText("Declined")
                binding.status.setTextColor(resources.getColor(R.color.status_declined))
                binding.statusIcon.setImageResource(R.drawable.status_declined)
                binding.markAsDelivered.visibility = View.GONE
                binding.payAsYouWish.visibility = View.GONE
                binding.markAsDelivered.setText("Rejected")
                binding.chatBtn.isEnabled = false
                //hiding the accept and chat button
                binding.constraintLayout3.visibility = View.GONE
            }
            "received" -> {
                binding.status.setText("Received")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
                binding.payAsYouWish.visibility = View.VISIBLE
                binding.markAsDelivered.setText("Received")
                binding.chatBtn.isEnabled = true
                binding.constraintLayout3.visibility = View.GONE
            }
            "delivered" -> {
                binding.status.setText("Delivered")
                binding.status.setTextColor(resources.getColor(R.color.status_accepted))
                binding.statusIcon.setImageResource(R.drawable.status_accepted)
                binding.payAsYouWish.visibility = View.VISIBLE
                binding.markAsDelivered.setText("Delivered")
                binding.chatBtn.isEnabled = true
                binding.constraintLayout3.visibility = View.GONE
            }
        }

        val imageList = ArrayList<SlideModel>()
        for (i in it.data?.images!!) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        binding.imageSlider.setImageList(imageList)

        Glide.with(this).load(it.data!!.postedBy?.userAvatar)
            .placeholder(resources.getDrawable(R.drawable.ic_profile))
            .into(binding.imageView20)

        binding.categoryTxt.setText(it.data?.category?.name?.capitalize())
        binding.productName.setText(it.data?.name?.capitalize())
        binding.condition.setText(it.data?.condition)
        binding.usedFor.setText(it.data?.usedFor)
        binding.castSaving.setText("Rs ${it.data?.costSaving}")
        binding.energySaving.text = (it.data?.energySaving?.toString())
        binding.prodcutLocation.text = (it.data?.locationName.toString())
        binding.descriptionTxt.text = (it.data?.description.toString().capitalize())
        binding.postedUserName.text = (it.data?.postedBy?.name.toString().capitalize())
        binding.phone.text = (it.data?.postedBy?.phone.toString())
        binding.email.text = (it.data?.postedBy?.email.toString())
        binding.postedLocation.text = (it.data?.locationName.toString())
//        binding.locationName.text = (it.data.locationName.toString())

    }
}