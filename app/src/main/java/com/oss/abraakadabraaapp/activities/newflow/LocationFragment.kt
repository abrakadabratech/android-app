import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oss.abraakadabraaapp.R


class LocationFragment : Fragment() {
    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
    var lattitude = 0.0
    var longitude = 0.0
    var title = "Product"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.location_fragment, container, false)
        mMapView = rootView.findViewById(R.id.mapView)
        mMapView!!.onCreate(savedInstanceState)
        mMapView!!.onResume() // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity()?.getApplicationContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        lattitude = requireArguments().getString("lat_value")?.toDouble()!!
        longitude = requireArguments().getString("lang_value")?.toDouble()!!
        title = requireArguments().getString("title")?.toString()!!

        Log.d("Location - ", "Location Details $lattitude & $longitude")

        //            mMap = mapFrag.getMap();
        //            mMap = mapFrag.getMap();

//        mMapView!!.getMapAsync(this)

        try {
            MapsInitializer.initialize(requireActivity()!!.applicationContext)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        mMapView!!.getMapAsync { mMap ->
            googleMap = mMap

            // For showing a move to my location button
//            googleMap!!.isMyLocationEnabled = true

            // For dropping a marker at a point on the Map
            val sydney = LatLng(lattitude,longitude)
            googleMap!!.addMarker(
                MarkerOptions().position(sydney).title(title)
            )

            // For zooming automatically to the location of the marker
            val cameraPosition = CameraPosition.Builder().target(sydney).zoom(15f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        return rootView
    }

    override fun onResume() {
        super.onResume()
        mMapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView!!.onLowMemory()
    }
}