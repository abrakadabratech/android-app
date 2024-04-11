package com.oss.abraakadabraaapp.utils

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.oss.abraakadabraaapp.BuildConfig
import javax.inject.Inject
import kotlin.math.log

class ForceUpdateChecker @Inject constructor() {
    /**
     * About [setDefaultsAsync]
     * Provided default values for your parameters that act as fallbacks
     * in case the actual remote values are not available due to network issues or if the parameters haven't been fetched yet.
     * This helps ensure that your app doesn't break or behave unexpectedly if there's a delay in fetching remote configuration values.
     *
     * Set an interval for fetching config. (Default is 12 hours)
     *
     * Compare current and required versions.
     * If current version is lower than required one, fetch store url and set to Force Update Data Model
     * otherwise return null
     */
    fun checkForceUpdateRequired(updateRequired: (result: UpdateRequiredModel?) -> Unit) {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setDefaultsAsync(
            mapOf(
                KEY_UPDATE_REQUIRED to false,
                KEY_REQUIRED_VERSION to BuildConfig.VERSION_NAME,
                KEY_UPDATE_URL to "store_url"
            )
        ).addOnCompleteListener {
            remoteConfig.fetch(MINIMUM_FETCH_INTERVAL).addOnCompleteListener {
                if (it.isSuccessful) {
                    remoteConfig.activate().addOnCompleteListener {
                        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
                            val currentVersion = (BuildConfig.VERSION_CODE).toString().replace(".", "").toInt()

                            val requiredVersion =
                                remoteConfig.getString(KEY_REQUIRED_VERSION).replace(".", "")
                                    .toInt()

                            Log.e("REMOTE_CONFIG", "checkForceUpdateRequired: $requiredVersion  current version :$currentVersion", )

                            if (currentVersion < requiredVersion) {
                                updateRequired.invoke(
                                    UpdateRequiredModel(
                                        updateUrl = remoteConfig.getString(KEY_UPDATE_URL)
                                    )
                                )
                            } else {
                                updateRequired.invoke(null)
                            }
                        }
                    }
                } else {
                    updateRequired.invoke(null)
                }
            }
        }
    }

    companion object {
        const val KEY_UPDATE_REQUIRED = "android_force_update_required"
        const val KEY_REQUIRED_VERSION = "android_force_update_required_version"
        const val KEY_UPDATE_URL = "android_force_update_store_url"
        const val MINIMUM_FETCH_INTERVAL = 60L
    }
}

data class UpdateRequiredModel(
    val updateUrl: String? = null
)