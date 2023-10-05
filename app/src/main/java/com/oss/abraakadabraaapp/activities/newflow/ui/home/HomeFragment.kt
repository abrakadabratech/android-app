package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.devs.readmoreoption.ReadMoreOption
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.oss.abraakadabraaapp.BuildConfig
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyNewProfileActivity
import com.oss.abraakadabraaapp.activities.newflow.NewNotificationActivity
import com.oss.abraakadabraaapp.databinding.FragmentHomeBinding
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_GIVE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_NOTIFICATION
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SHARE
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class HomeFragment : Fragment(), LocationListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var application: BaseActivity
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView: called")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        application = (activity as BaseActivity)

        if (PreferencesManagement.getUserLocation(requireContext()) != null) {
            val userLocation = PreferencesManagement.getUserLocation(requireContext())!!

            var fullAddress = userLocation.address ?: ""

            val readMoreOption: ReadMoreOption = ReadMoreOption.Builder(application)
                .textLength(3, ReadMoreOption.TYPE_LINE)
                .moreLabel("MORE")
                .lessLabel("LESS")
                .moreLabelColor(Color.RED)
                .lessLabelColor(Color.BLUE)
                .labelUnderLine(true)
                .expandAnimation(true)
                .build()

        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        application.postEvent(Constants.PAGE_HOME, null)
        val fragmentManager: FragmentManager = requireFragmentManager()
        fragmentManager.beginTransaction()
            .replace(R.id.container, NewReceiverFragment::class.java, null)
            .setReorderingAllowed(true)
            .commit()

        val apiKey = BuildConfig.API_KEY

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }

        placesClient = Places.createClient(requireContext())

        binding.submitProfile.setOnClickListener {
            val intent = Intent(requireContext(), MyNewProfileActivity::class.java)
            intent.putExtra("from", "activity")
            startActivity(intent)
            binding.profileLayout.visibility = View.GONE
            binding.receiveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.background = null

            fragmentManager.beginTransaction()
                .replace(R.id.container, NewReceiverFragment::class.java, null)
                .setReorderingAllowed(true)
                .commit()
            EventBus.getDefault().post(1)
        }


        binding.locationOnActionbar.setOnClickListener {
            locationPicker()
        }

        binding.receiveBtn.setOnClickListener {
            application.postClick(Constants.BUTTON_RECEIVE)
            binding.receiveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.background = null
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewReceiverFragment::class.java, null)
                .setReorderingAllowed(true)
                .commit()
            EventBus.getDefault().post(1)
        }
        binding.giveBtn.setOnClickListener {
            application.postClick(BUTTON_GIVE)
            EventBus.getDefault().post(0)
            binding.receiveBtn.background = null
            binding.giveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewGiverFragment::class.java, null)
                .setReorderingAllowed(true)
                .commit()
        }
        binding.shareAKD.setOnClickListener {
            application.postClick(BUTTON_SHARE)
            loadData()
        }
        binding.notifications.setOnClickListener {
            application.postClick(BUTTON_NOTIFICATION)
            startActivity(Intent(requireActivity(), NewNotificationActivity::class.java))
        }

        getNotificationData()

        getUserLocation()

        return root
    }

    private fun locationPicker() {
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        ).setCountry("IN")
            .build(requireActivity())
        locationLauncher.launch(intent)
    }

    private var locationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK)
                if (result != null) {
                    val data: Intent? = result.data
                    if (data != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Constants.fullAddress = if (place.address != null) {
                            place.address!!
                        } else {
                            "TODO geo api required"
                        }
                        PreferencesManagement.saveUserLocation(
                            requireContext(),
                            UserLocation(
                                lat = place.latLng!!.latitude.toString(),
                                long = place.latLng!!.longitude.toString(),
                                Constants.fullAddress,
                            )
                        )

                        binding.locationOnActionbar.text = Constants.fullAddress
                        val fragmentManager: FragmentManager = requireFragmentManager()
                        fragmentManager.beginTransaction()
                            .replace(R.id.container, NewReceiverFragment::class.java, null)
                            .setReorderingAllowed(true)
                            .commit()

                    }
                }
        }

    private fun getNotificationData() {

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("notifications").whereEqualTo("userId", currentUserId)
            .whereEqualTo("deleted", false)
        docRef.addSnapshotListener { snapshot, e ->

            if (snapshot!!.isEmpty) {
                binding.notifications.setImageResource(R.drawable.ic_bell)
            } else {
                binding.notifications.setImageResource(R.drawable.ic_bell_fille)
            }

        }

    }


    private fun getUserLocation() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())
        binding.locationOnActionbar.text = userLocation?.address
    }

    private fun loadData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test")
        i.putExtra(
            Intent.EXTRA_TEXT,
            "Try this great app Abra Ka Dabra to share second hand products with others for free. App is available at the below link: https://play.google.com/store/apps/details?id=com.oss.abraakadabraaapp"
        )
        startActivity(Intent.createChooser(i, "Share"))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String?) {
        when (event) {
            "clear" -> {
                binding.receiveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
                binding.receiveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
                binding.giveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
                binding.giveBtn.background = null
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.container, NewReceiverFragment::class.java, null)
                    ?.setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
                    ?.commit()
                EventBus.getDefault().post(1)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        Log.d(TAG, "onStart: Called")
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        Log.d(TAG, "onStop: called")
    }

    override fun onLocationChanged(p0: Location) {

    }

}
