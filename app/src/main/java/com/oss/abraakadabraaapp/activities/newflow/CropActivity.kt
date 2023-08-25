package com.oss.abraakadabraaapp.activities.newflow

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CropAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.CropSelectAdapter
import com.oss.abraakadabraaapp.activities.newflow.model.CropSelectModel
import com.oss.abraakadabraaapp.databinding.ActivityCropBinding
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.customView.ImagePickerActivity
import com.yalantis.ucrop.UCrop
import java.io.File


class CropActivity : BaseActivity() ,CropSelectAdapter.OnSelectorClicks{
    private lateinit var binding: ActivityCropBinding
    private val TAG = "CropActivity"
    var adapterPosition :Int = 0
    var images: ArrayList<Uri> = arrayListOf()
    var imagesBottom: ArrayList<CropSelectModel> = arrayListOf()
    lateinit var adapter:CropAdapter
    lateinit var cropSelectAdapter: CropSelectAdapter
    private val EXTRA_PREFIX = BuildConfig.APPLICATION_ID

    private val IMAGE_COMPRESSION = 80
    private val ASPECT_RATIO_X :Int = 1
    private var ASPECT_RATIO_Y:Int = 1
    var COUNT_IMAGES = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("COUNT_IMAGES")){
            COUNT_IMAGES = intent.extras?.getInt("COUNT_IMAGES")!!
        }

        adapter = CropAdapter(this, images)
        cropSelectAdapter = CropSelectAdapter(this,imagesBottom,this)
        binding.viewPager2.adapter = adapter

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = cropSelectAdapter
        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int){
                super.onPageSelected(position)
                adapterPosition = position
                for (i in 0 until imagesBottom.size) imagesBottom[i].isSelect = i == position
                cropSelectAdapter.notifyDataSetChanged()
                binding.recyclerView.smoothScrollToPosition(adapterPosition)

            }
        })

        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Picture"), 123)

        clickEvents()
        if (!PreferencesManagement.isTooltipShown(this)){
            showTooltip()
        }

    }

    private fun showTooltip() {
        val editTooltip = TapTarget.forView(findViewById(R.id.cropImage), "Edit image", "Crop your image to look better").cancelable(false)
        val deleteTooltip = TapTarget.forView(findViewById(R.id.deleteImage), "Delete image", "Remove the image by clicking this icon").cancelable(false)
        val doneTooltip = TapTarget.forView(findViewById(R.id.doneButton),
            "Done", "Please make sure you have edited all the images").cancelable(false).tintTarget(false)

       val sequence =  TapTargetSequence(this)
            .targets(
                editTooltip,deleteTooltip,doneTooltip
            )
            .listener(object : TapTargetSequence.Listener {
                // This listener will tell us when interesting(tm) events happen in regards
                // to the sequence
                override fun onSequenceFinish() {
                    // Yay
                    Log.d(TAG, "onSequenceFinish: ${PreferencesManagement.disableCropTooltip(this@CropActivity,true)}")
                }

                override fun onSequenceStep(lastTarget: TapTarget, targetClicked: Boolean) {
                    // Perform action for the current target
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {
                    // Boo
                }
            })
        sequence.start()
    }

    private fun clickEvents() {
        with(binding){
            deleteImage.setOnClickListener {
                if (imagesBottom.size == 1){
                    finish()
                }else {
                    images.removeAt(adapterPosition)
                    imagesBottom.removeAt(adapterPosition)
                    if (imagesBottom.size == adapterPosition) {
                        imagesBottom[imagesBottom.size - 1].isSelect = true
                    } else {
                        imagesBottom[adapterPosition].isSelect = true
                    }
                    adapter.notifyDataSetChanged()
                    cropSelectAdapter.notifyDataSetChanged()
                }
            }
            cropImage.setOnClickListener {
                if (!imagesBottom[adapterPosition].isEdited){
                    cropImage(images[adapterPosition])
                }else{
                    showToast("You edited this image")
                }
            }
            close.setOnClickListener {
                finish()
            }
            doneButton.setOnClickListener {
                Log.d(TAG, "clickEvents: clicked")
                if(imagesBottom.all { it.isEdited }){
                    val intent = Intent()
                    intent.putParcelableArrayListExtra("imagesList", images)
                    setResult(RESULT_OK, intent)
                    finish()
                }else{
                    showToast("Edit all images to proceed.")
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP){
            handleUCropResult(data)
        }
        else if (resultCode == Activity.RESULT_OK && requestCode == 123) {
            if (data?.clipData != null) {
                val mClipData = data?.clipData

                for (i in 0 until mClipData!!.itemCount) {
                    val item = mClipData.getItemAt(i)
                    val uri = item.uri
                    if ((5 - COUNT_IMAGES) != 0
                    ){
                        images.add(uri)
                        imagesBottom.add(CropSelectModel(uri,false))
                        COUNT_IMAGES++
                    }
                }
            } else {

                var uriList = data!!.data
                images.add(uriList!!)
                imagesBottom.add(CropSelectModel(uriList,false))
            }
            imagesBottom[0].isSelect = true
            adapter.notifyDataSetChanged()
            cropSelectAdapter.notifyDataSetChanged()
        }
        else{
            //finish()
        }
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val uri = UCrop.getOutput(data)
//        val uri = data!!.getParcelableExtra<Uri>("path")!!
        images.removeAt(adapterPosition)
        images.add(adapterPosition,uri!!)
        adapter.notifyDataSetChanged()

        imagesBottom.removeAt(adapterPosition)
        imagesBottom.add(adapterPosition,CropSelectModel(uri, isSelect = true, isEdited = true))
        cropSelectAdapter.notifyDataSetChanged()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }
    override fun onSelectorClick(position: Int) {
        adapterPosition = position
        binding.viewPager2.setCurrentItem(position,false)
        for (i in 0 until imagesBottom.size) imagesBottom[i].isSelect = i == position
        cropSelectAdapter.notifyDataSetChanged()
    }
    fun cropImage(sourceUri: Uri?) {
        val destinationUri = Uri.fromFile(
            File(
                cacheDir, ImagePickerActivity.queryName(
                    contentResolver, sourceUri
                )
            )
        )
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(this, R.color.theme_color))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.theme_color))
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.white))
        options.withAspectRatio(
            1f,
            1f
        )
        options.withMaxResultSize(1000, 1000)
        UCrop.of(sourceUri!!, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .start(this)
    }
}
