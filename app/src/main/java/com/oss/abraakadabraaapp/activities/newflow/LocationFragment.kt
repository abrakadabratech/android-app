import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oss.abraakadabraaapp.R


class LocationFragment : Fragment() ,OnMapReadyCallback{
    var mMapView: MapView? = null
    private var googleMap: GoogleMap? = null
    var lattitude = 0.0
    var longitude = 0.0
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

        Log.d("Location - ", "Location Details $lattitude & $longitude")

        //            mMap = mapFrag.getMap();
        //            mMap = mapFrag.getMap();

        mMapView!!.getMapAsync(this)
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

    override fun onMapReady(p0: GoogleMap?) {
        Log.d("Location - ", "on Map Ready Location Details $lattitude & $longitude")
        val latLng = LatLng(lattitude.toDouble(), longitude.toDouble())
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap?.addMarker(markerOptions)
    }
}