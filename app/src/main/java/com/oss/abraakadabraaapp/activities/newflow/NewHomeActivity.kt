package com.oss.abraakadabraaapp.activities.newflow

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.google.android.play.core.tasks.OnCompleteListener
import com.google.android.play.core.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GetTokenResult
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityNewHomeBinding
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NewHomeActivity : BaseActivity() {
    lateinit var application: BaseActivity

    private lateinit var binding: ActivityNewHomeBinding
    lateinit var navView: BottomNavigationView
    private var mTracker: Tracker? = null


    //    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.hide()

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
        Log.d(
                        "FIREBASE",
                        "signInWithCredential:success tokeId is:${PreferencesManagement.getAuthToken(this)!!}"
                    )
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Int?) {

        // Do something
        Log.d(TAG, "onMessageEvent: $event")
        if (event == 0) {
            navView.visibility = View.GONE
        } else navView.visibility = View.VISIBLE
    }

}