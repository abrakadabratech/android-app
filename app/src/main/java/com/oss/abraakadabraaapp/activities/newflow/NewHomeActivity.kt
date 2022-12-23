package com.oss.abraakadabraaapp.activities.newflow

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityNewHomeBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NewHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewHomeBinding
    lateinit var navView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar?.hide()

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_new_home) as NavHostFragment? ?: return

        val navController =  findNavController(R.id.nav_host_fragment_activity_new_home)/*host.navController*/
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
        if (event == 0){
            navView.visibility = View.GONE
        }else navView.visibility = View.VISIBLE
    }

}