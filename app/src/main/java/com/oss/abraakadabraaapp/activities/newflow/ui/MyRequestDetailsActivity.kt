package com.oss.abraakadabraaapp.activities.newflow.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.oss.abraakadabraaapp.R


class MyRequestDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requesting_detail)

        var editRequest = findViewById<Switch>(R.id.editRequest)
        var markAsDelivered = findViewById<TextView>(R.id.markAsDelivered)

        markAsDelivered.setOnClickListener {
            //navigates to feedback pages...
            startActivity(Intent(this,FeedbackActivity::class.java))
        }
        editRequest.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
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