package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.analytics.Tracker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.HomeActivity
import com.oss.abraakadabraaapp.activities.newflow.NewNotificationActivity
import com.oss.abraakadabraaapp.databinding.FragmentHomeBinding
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_GIVE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_NOTIFICATION
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SHARE
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val locationViewModel: LocationViewModel by viewModel()
    private var isGPSEnabled = false

    private var mTracker: Tracker? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var application: BaseActivity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (PreferencesManagement.getUserLocation(requireContext()) != null) {
            val userLocation = PreferencesManagement.getUserLocation(requireContext())!!
            /*latitude = userLocation.lat
            longitude = userLocation.long*/

//            Log.d("LOCCA", "onCreateView: ${Gson().toJson()}")
            var fullAddress = userLocation.address ?: ""
            binding.locationOnActionbar.setText(getAddress(userLocation.lat.toDouble(),userLocation.long.toDouble()))
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_HOME,null)
        val fragmentManager: FragmentManager = requireFragmentManager()
        fragmentManager.beginTransaction()
            .replace(R.id.container, NewReceiverFragment::class.java, null)
            .setReorderingAllowed(true)
//            .addToBackStack("name") // name can be null
            .commit()
        binding.receiveBtn.setOnClickListener {
            // Load receiver fragment

            application.postClick(Constants.BUTTON_RECEIVE)
            binding.receiveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.background = null
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewReceiverFragment::class.java, null)
                .setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
                .commit()
            EventBus.getDefault().post(1)
        }
        binding.giveBtn.setOnClickListener(View.OnClickListener {
            application.postClick(BUTTON_GIVE)
//            Toast.makeText(context,"Giver",Toast.LENGTH_LONG).show()
            // Load receiver fragment
            EventBus.getDefault().post(0)

            binding.receiveBtn.background = null
            binding.giveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewGiverFragment::class.java, null)
                .setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
                .commit()
        })
        binding.shareAKD.setOnClickListener {
            application.postClick(BUTTON_SHARE)
            loadData()
        }
        binding.notifications.setOnClickListener {
            application.postClick(BUTTON_NOTIFICATION)
            startActivity(Intent(requireActivity(), NewNotificationActivity::class.java))
        }

        getUserLocation()

        return root
    }
    private fun getUserLocation() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())
        binding.locationOnActionbar.text = userLocation?.address
    }
    override fun onStart() {
        super.onStart()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test")
        i.putExtra(Intent.EXTRA_TEXT, "Try this great app Abra Ka Dabra to share second hand products with others for free. App is available at the below link: link")
        startActivity(Intent.createChooser(i, "Share"))

//        val modalBottomSheet = ModalBottomSheet()
//
//        modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)

    }

    fun getAddress(lat: Double, lng: Double) :String{
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val obj = addresses[0]
            var add = obj.getAddressLine(0)
            add = """
            $add
            ${obj.countryName}
            """.trimIndent()
            add = """
            $add
            ${obj.countryCode}
            """.trimIndent()
            add = """
            $add
            ${obj.adminArea}
            """.trimIndent()
            add = """
            $add
            ${obj.postalCode}
            """.trimIndent()
            add = """
            $add
            ${obj.subAdminArea}
            """.trimIndent()
            add = """
            $add
            ${obj.locality}
            """.trimIndent()
            add = """
            $add
            ${obj.subThoroughfare}
            """.trimIndent()
            Log.v("IGA", "Address$add")
            return obj.locality+","+obj.adminArea
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        return ""
    }
}
