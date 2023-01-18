package com.oss.abraakadabraaapp.activities.newflow.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyPayAsYouGoActivity
import com.oss.abraakadabraaapp.databinding.ActivityMyRequestingDetailBinding
import com.oss.abraakadabraaapp.utils.Constants


class MyRequestDetailsActivity : BaseActivity() {
    lateinit var application: BaseActivity
    private lateinit var binding:ActivityMyRequestingDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRequestingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_MY_REQUEST_DETAILS,null)


        binding.chatBtn.setOnClickListener{
            postEvent(Constants.BUTTON_CHAT_IN_REQUEST_DETAILS,null)
        }

        binding.markAsDelivered.setOnClickListener {
            //navigates to feedback pages...
            postEvent(Constants.BUTTON_MARK_AS_DELIVERED,null)
            startActivity(Intent(this,FeedbackActivity::class.java))
        }
        binding.editRequest.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            postEvent(Constants.BUTTON_EDIT_REQUEST,null)
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
            val i = Intent(this, MyPayAsYouGoActivity::class.java)
            i.putExtra("from","receiver")
//            i.putExtra("receiver_data", Gson().toJson(productDetailData))
            startActivity(i)
        }
        tempData()
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

    }
}