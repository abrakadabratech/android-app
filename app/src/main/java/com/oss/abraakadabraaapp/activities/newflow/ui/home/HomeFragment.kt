package com.oss.abraakadabraaapp.activities.newflow.ui.home

import androidx.compose.foundation.Image
import androidx.compose.material3.Card
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import coil.compose.rememberAsyncImagePainter
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.devs.readmoreoption.ReadMoreOption
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
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
import com.oss.abraakadabraaapp.activities.newflow.adapters.BannerAdapter
import com.oss.abraakadabraaapp.activities.newflow.apimodels.BannerData
import com.oss.abraakadabraaapp.databinding.FragmentHomeBinding
import com.oss.abraakadabraaapp.model.UserLocation
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_GIVE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_NOTIFICATION
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_SHARE
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.HashMap


class HomeFragment : Fragment(), LocationListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var placesClient: PlacesClient
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var application: BaseActivity
    private val TAG = "HomeFragment"
    private val mainViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView: called")
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        application = (activity as BaseActivity)


        loadBanner()
        setUpObserver()

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
            .addToBackStack(null)
            .commit()

        val apiKey = BuildConfig.API_KEY

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), apiKey)
        }

        placesClient = Places.createClient(requireContext())


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
            binding.imageSliderLayout.visibility = View.VISIBLE
            EventBus.getDefault().post(1)
        }
        binding.giveBtn.setOnClickListener {
            application.postClick(BUTTON_GIVE)
            binding.receiveBtn.background = null
            binding.giveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewGiverFragment::class.java, null)
                .setReorderingAllowed(true)
                .commit()
            binding.imageSliderLayout.visibility = View.GONE
            EventBus.getDefault().post(0)
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

    @OptIn(ExperimentalPagerApi::class)
    @Composable
    fun BannerSlider(list:ArrayList<BannerData>){
        val pagerState = rememberPagerState(initialPage = 0)

        LaunchedEffect(Unit) {
            while (true) {
                yield()
                delay(2600)
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % (pagerState.pageCount)
                )
            }
        }


        Column {
            HorizontalPager(
                count = list.size,
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) { page ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                    /*.graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }*/
                ) {
                    val banner = rememberAsyncImagePainter(model = list[page].imageUrl)
                    Image(
                        painter = banner,
                        contentDescription = stringResource(R.string.image_slider),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.height(list[page].height!!.dp)
                            .width(list[page].width!!.dp)
                    )
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp)
            )
        }
    }
    private fun setUpObserver() {
        mainViewModel.bannerSuccess.observe(requireActivity()){
            /*binding.composeView.setContent {
                BannerSlider(it.data)
            }*/
            Log.d(TAG, "setUpObserver: $it")
            val imageList = ArrayList<SlideModel>()
            if(it.data.size > 0){
                for (i in it.data) {
                    imageList.add(SlideModel(i.imageUrl, "", ScaleTypes.FIT))
                }
                binding.imageSlider.setImageList(imageList)
                binding.imageSliderLayout.visibility = View.VISIBLE

               /* val params = binding.cardView5.layoutParams
                if (params is ViewGroup.MarginLayoutParams) {
                    params.topMargin = 10
                    view?.layoutParams = binding.cardView5.layoutParams
                }*/
                
            }else{
                binding.imageSliderLayout.visibility = View.GONE
                /*val params = binding.cardView5.layoutParams
                if (params is ViewGroup.MarginLayoutParams) {
                    params.topMargin = 18
                    view?.layoutParams = binding.cardView5.layoutParams
                }*/
            }
        }
    }

    private fun loadBanner() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(requireContext())!!
        map[RequestKeys.authorization] = token
        mainViewModel.getBanners(map)
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
