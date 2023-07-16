package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.content.Intent
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.devs.readmoreoption.ReadMoreOption
import com.google.android.gms.analytics.Tracker
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyNewProfileActivity
import com.oss.abraakadabraaapp.activities.newflow.NewNotificationActivity
import com.oss.abraakadabraaapp.databinding.FragmentHomeBinding
import com.oss.abraakadabraaapp.location.livedata.LocationViewModel
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_GIVE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_NOTIFICATION
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SHARE
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import java.util.*


class HomeFragment : Fragment(), LocationListener {

    private var _binding: FragmentHomeBinding? = null
    private val locationViewModel: LocationViewModel by viewModel()
    private var isGPSEnabled = false
    private lateinit var placesClient: PlacesClient

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
        application = (activity as BaseActivity)

        if (PreferencesManagement.getUserLocation(requireContext()) != null) {
            val userLocation = PreferencesManagement.getUserLocation(requireContext())!!
            /*latitude = userLocation.lat
            longitude = userLocation.long*/

//            Log.d("LOCCA", "onCreateView: ${Gson().toJson()}")
            var fullAddress = userLocation.address ?: ""

            val readMoreOption: ReadMoreOption = ReadMoreOption.Builder(application)
                .textLength(3, ReadMoreOption.TYPE_LINE) // OR
                //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel("MORE")
                .lessLabel("LESS")
                .moreLabelColor(Color.RED)
                .lessLabelColor(Color.BLUE)
                .labelUnderLine(true)
                .expandAnimation(true)
                .build()

//            readMoreOption.addReadMoreTo(binding.locationOnActionbar, fullAddress)

//            binding.locationOnActionbar.text = fullAddress
//            binding.locationOnActionbar.text = getAddress(userLocation.lat.toDouble(),userLocation.long.toDouble())
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        application.postEvent(Constants.PAGE_HOME,null)
        val fragmentManager: FragmentManager = requireFragmentManager()
        fragmentManager.beginTransaction()
            .replace(R.id.container, NewReceiverFragment::class.java, null)
            .setReorderingAllowed(true)
//            .addToBackStack("name") // name can be null
            .commit()

        binding.submitProfile.setOnClickListener {
            val intent = Intent(requireContext(), MyNewProfileActivity::class.java)
            intent.putExtra("from","activity")
            startActivity(intent)
            binding.profileLayout.visibility = View.GONE
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
//            alertDialog.dismiss()
            //showSubmitSuccessDialog()
        }

        binding.profileLayout.setOnClickListener {

           /* binding.profileLayout.visibility = View.GONE

            binding.receiveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.background = null
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewReceiverFragment::class.java, null)
                .setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
                .commit()
            EventBus.getDefault().post(1)*/

        }

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

        getNotificationData()

        getUserLocation()

        return root
    }

    private fun getNotificationData() {

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("notifications").whereEqualTo("userId",currentUserId)
            .whereEqualTo("deleted",false)

        docRef.get().addOnSuccessListener { snap ->
            if(snap.isEmpty){
                binding.notifications.setImageResource(R.drawable.ic_bell)
            }else{
                binding.notifications.setImageResource(R.drawable.ic_bell_fille)
            }
        }

    }


    private fun getUserLocation() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())
        binding.locationOnActionbar.text = userLocation?.address
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test")
        i.putExtra(Intent.EXTRA_TEXT, "Try this great app Abra Ka Dabra to share second hand products with others for free. App is available at the below link: https://play.google.com/store/apps/details?id=com.oss.abraakadabraaapp")
        startActivity(Intent.createChooser(i, "Share"))
    }
    fun getAddress(lat: Double, lng: Double) :String{

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 100)
            val obj = addresses!![0]
            var add = obj.getAddressLine(0)
            var string = ""
            if(obj.subLocality != null){
                string = "${obj.subLocality},${obj.locality},${obj.adminArea}"
            }else{
                string = obj.locality+","+obj.adminArea
            }
//            Toast.makeText(requireContext(),string,Toast.LENGTH_SHORT).show()
            return string

        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        return ""
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String?) {
        // Do something
//        application.showToast("triggered.")
        if(event == "clear"){
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
        }else{
            binding.profileLayout.visibility = View.VISIBLE
        }
    }
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onLocationChanged(p0: Location) {

    }
}
