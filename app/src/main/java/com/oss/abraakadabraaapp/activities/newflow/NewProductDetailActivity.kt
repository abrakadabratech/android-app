package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityNewProductDetailBinding
import com.oss.abraakadabraaapp.databinding.ActivityProductDetailBinding

class NewProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tempData()

        binding.Submit.setOnClickListener {
            startActivity(Intent(this, PostedUserActivity::class.java))
        }

        binding.ivMenu.setOnClickListener {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.reportThis.setOnClickListener {
            showReportThisDialog()
        }
    }

    private fun showReportThisDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_report_this, null)
        dialogBuilder.setView(dialogView)

        val inAppropriateTxt = dialogView.findViewById<TextView>(R.id.inAppropriateTxt)
        val inAppropriateSwitch = dialogView.findViewById<ImageView>(R.id.inAppropriateSwitch)

        val fakeTxt = dialogView.findViewById<TextView>(R.id.fakeTxt)
        val fakeTxtSwitch = dialogView.findViewById<ImageView>(R.id.fakeTxtSwitch)

        fakeTxt.setOnClickListener {
            if (fakeTxtSwitch.tag == 1) {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                fakeTxtSwitch.tag = 0
            } else {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                fakeTxtSwitch.tag = 1
            }
        }

        fakeTxtSwitch.setOnClickListener {
            if (fakeTxtSwitch.tag == 1) {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                fakeTxtSwitch.tag = 0
            } else {
                fakeTxtSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                fakeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                fakeTxtSwitch.tag = 1
            }
        }

        inAppropriateTxt.setOnClickListener {
            if (inAppropriateSwitch.tag == 1) {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                inAppropriateSwitch.tag = 0
            } else {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                inAppropriateSwitch.tag = 1
            }
        }
        inAppropriateSwitch.setOnClickListener {
            if (inAppropriateSwitch.tag == 1) {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_select)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_select_color))
                inAppropriateSwitch.tag = 0
            } else {
                inAppropriateSwitch.setBackgroundResource(R.drawable.ic_radio_unselect)
                inAppropriateTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
                inAppropriateSwitch.tag = 1
            }
        }

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
//        alertDialog.window?.setLayout(800, 700)
    }

    private fun tempData() {
//        Glide.with(this).load("https://www.gstatic.com/webp/gallery/1.jpg").into(binding.imageSlider)

        val imageList = ArrayList<SlideModel>() // Create image list

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

        imageList.add(SlideModel("https://www.gstatic.com/webp/gallery/1.jpg", "", ScaleTypes.FIT))
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
        imageList.add(SlideModel("https://bit.ly/3fLJf72", "", ScaleTypes.FIT))

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
        imageSlider.setImageList(imageList)
    }
}