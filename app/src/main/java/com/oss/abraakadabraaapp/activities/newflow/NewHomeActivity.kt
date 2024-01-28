package com.oss.abraakadabraaapp.activities.newflow

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityNewHomeBinding
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NewHomeActivity : BaseActivity() {

    private lateinit var binding: ActivityNewHomeBinding
    private lateinit var navView: BottomNavigationView
    private lateinit var mAppUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE: Int = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.hide()

        window.statusBarColor =
            ContextCompat.getColor(
                this,
                R.color.blue_status_bar_color
            )

        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdate()
        checkNotificationPermission()


        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_new_home) as NavHostFragment?
            ?: return

        val navController =
            findNavController(R.id.nav_host_fragment_activity_new_home)/*host.navController*/
        navView = binding.navView

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }

            Log.d("NavigationActivity", "Navigated to $dest")
        }


    }

    private fun getUnreadMessageCount() {
        val db = FirebaseFirestore.getInstance()

        val collectionReference = db.collection("chats")
        val targetSubstring = FirebaseAuth.getInstance().currentUser?.uid
        val badge = navView.getOrCreateBadge(R.id.navigation_community)
        badge.isVisible = false
        collectionReference.get()
            .addOnSuccessListener { querySnapshot ->
                var count = 0
                val lock = Object()
                for (document in querySnapshot.documents) {
                    if (document.id.contains(targetSubstring.toString())) {
                        // Access data from each document
                        val data = document.id
                        println("Number of unread messages in sub-collection: $data")


                        collectionReference.document(data).collection("Messages")
                            .whereNotEqualTo("from",targetSubstring)
                            .whereEqualTo("read", false).get().addOnSuccessListener { records ->
                                val unreadCount = records.size()
                                synchronized(lock) {
                                    count += unreadCount
                                }
                                if (count > 0) {
                                    badge.isVisible = true
                                    badge.number = count
                                } else {
                                    badge.isVisible = false
                                }
                                println("Number of unread messages in sub-collection: $count")

                            }.addOnFailureListener { e ->
                                // Handle errors
                                println("Error getting unread messages in subcollection: $e")
                            }
                    }
                }
                println("total count: $count")

//
            }
            .addOnFailureListener { e ->
                // Handle errors
                println("Error getting documents: $e")
            }
    }

    private fun checkNotificationPermission() {
        if (!PreferencesManagement.isNotificationEnabled(this)) {

            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()){

                PreferencesManagement.setisNotificationEnabled(this,true)

                val listener = object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }
                val permissions = arrayListOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )

                // Notifications are already enabled
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_permission_title))
                builder.setMessage(getString(R.string.dialog_notification_permission_message))
                builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
                    dialog.cancel()
                    Dexter.withContext(this)
                        .withPermissions(permissions)
                        .withListener(listener)
                        .check()
                }
                builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
                builder.show()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            RC_APP_UPDATE -> if (resultCode != RESULT_OK) { //RESULT_OK / RESULT_CANCELED / RESULT_IN_APP_UPDATE_FAILED
                Log.d("MYT", "$resultCode")
                //checkForUpdate()
            }

            Activity.RESULT_CANCELED -> {
                //checkForUpdate()
                Log.d(TAG, "" + "Result Cancelled")
                //  handle user's rejection  }
            }

            ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                //checkForUpdate()
                //if you want to request the update again just call checkUpdate()
                Log.d(TAG, "" + "Update Failure")
                //  handle update failure
            }
        }
    }

    private val listener: InstallStateUpdatedListener =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Log.d(TAG, "An update has been downloaded")
                popupSnackbarForCompleteUpdate()
                // mAppUpdateManager.completeUpdate()
            }
            if (installState.installStatus() == InstallStatus.DOWNLOADING) {
                val bytesDownloaded = installState.bytesDownloaded()
                val totalBytesToDownload = installState.totalBytesToDownload()
                Log.e(TAG, "Downloading: $bytesDownloaded/$totalBytesToDownload")
                //showSnackBar(binding.container,"Downloading...$bytesDownloaded/$totalBytesToDownload")
            }
        }

    private fun checkForUpdate() {
        mAppUpdateManager.registerListener(listener)

        mAppUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        this,
                        RC_APP_UPDATE
                    )
//                    showToast("Downloading...")
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("MYT", e.localizedMessage!!)
                }
            }
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && it.updatePriority() >= 4 /* high priority */
                && it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request an immediate update.
                mAppUpdateManager.startUpdateFlowForResult(
                    it,
                    AppUpdateType.IMMEDIATE,
                    this,
                    RC_APP_UPDATE
                )
            }else {
                mAppUpdateManager.unregisterListener(listener)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    // Bottom Navigation will be disappear if not Home Tab
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Int?) {
        if (event == 0) {
            navView.visibility = View.GONE
        } else navView.visibility = View.VISIBLE
    }

    companion object {
        fun createIntent(context: Context): Intent {
            val intent = Intent(context, NewHomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("EXIT", true)
            return intent
        }
    }

    fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.container),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("INSTALL") { mAppUpdateManager.completeUpdate() }
            setActionTextColor(ContextCompat.getColor(this@NewHomeActivity,R.color.btn_color))
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        getUnreadMessageCount()
    }
}