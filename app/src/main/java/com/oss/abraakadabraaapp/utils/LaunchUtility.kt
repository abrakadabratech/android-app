package com.oss.abraakadabraaapp.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast

object LaunchUtility {

    fun launchUrl(url: String, context: Context) {
        try {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(url)
            context.startActivity(openURL)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("Error", "launchUrl ${e.localizedMessage!!}")
            e.printStackTrace()
        }
    }

    fun whatsAppIntent(phoneNumber: String, context: Context) {
        try {
            val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp", context)
            val isWhatsappBusinessInstalled = whatsappInstalledOrNot("com.whatsapp.w4b", context)
            if (isWhatsappInstalled || isWhatsappBusinessInstalled) {
                val url = "https://api.whatsapp.com/send?phone=+91 $phoneNumber"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                context.startActivity(i)
            } else {
                Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("Error", "whatsAppIntent ${e.localizedMessage!!}")
            e.printStackTrace()
        }
    }

    private fun whatsappInstalledOrNot(uri: String, context: Context): Boolean {
        val pm: PackageManager = context.packageManager
        return  try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

}