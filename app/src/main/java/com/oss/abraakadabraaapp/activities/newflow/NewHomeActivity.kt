package com.oss.abraakadabraaapp.activities.newflow

import android.app.Activity
import android.content.*
import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.analytics.Tracker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityNewHomeBinding
import com.oss.abraakadabraaapp.localdb.NotificationEntity
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NewHomeActivity : BaseActivity() {

    private lateinit var binding: ActivityNewHomeBinding
    lateinit var navView: BottomNavigationView
    private var mTracker: Tracker? = null
    private lateinit var mAppUpdateManager: AppUpdateManager
    private val RC_APP_UPDATE: Int = 1000

    //    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.hide()
        mAppUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdate()

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_new_home) as NavHostFragment?
            ?: return

        val navController =
            findNavController(R.id.nav_host_fragment_activity_new_home)/*host.navController*/
        navView = binding.navView

        val appBarConfiguration = AppBarConfiguration(navController.graph)

        //        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }

            Log.d("NavigationActivity", "Navigated to $dest")
        }
        //        showToast("Test Analytics sent to the console")
        Log.d("FIREBASE",
                        "signInWithCredential:success tokeId is:${PreferencesManagement.getAuthToken(this)!!}"
                    )
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
    private val listener: InstallStateUpdatedListener = InstallStateUpdatedListener { installState ->
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
            Log.e(TAG, "Downloading: $bytesDownloaded/$totalBytesToDownload", )
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
            }else{
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Int?) {

        // Do something
        Log.d("TAG", "onMessageEvent: $event")
        if (event == 0) {
            navView.visibility = View.GONE
        } else navView.visibility = View.VISIBLE
    }

    @Subscribe
    fun onMessageEvent(event: NotificationEntity) {
        // Do something
        Log.d("Notification ", "onMessageEvent: ${Gson().toJson(event)}")
    }

    override fun onResume() {
        super.onResume()
       /* mAppUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                        it,
                        AppUpdateType.FLEXIBLE,
                        this,
                        RC_APP_UPDATE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.d("IntentSenderError", e.localizedMessage!!)
                }
            }
        }*/

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
            setAction("RESTART") { mAppUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.btn_color))
            show()
        }
    }

}