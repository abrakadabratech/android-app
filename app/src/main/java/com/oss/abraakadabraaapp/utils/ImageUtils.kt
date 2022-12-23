package com.oss.abraakadabraaapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.module.GlideApp
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


object ImageUtils {

    fun uriToBitMap(uri: Uri, context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                context.contentResolver,
                uri
            )
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    fun bitmapToFile(bitmap: Bitmap, context: Context, name:String): File {
        val file = File(context.cacheDir, name)
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun setImage(context: Context, imageView: ImageView, url: String, progressBar: ProgressBar?, placeHolder:Int?) {
        if (progressBar != null) progressBar.visibility = View.VISIBLE
        GlideApp.with(context)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (progressBar != null) progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (progressBar != null) progressBar.visibility = View.GONE
                    return false
                }
            })
            .error(placeHolder ?: R.drawable.home_toolbar_app_logo)
            .into(imageView)
    }

}