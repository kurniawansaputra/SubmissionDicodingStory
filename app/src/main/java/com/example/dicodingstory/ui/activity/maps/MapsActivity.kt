package com.example.dicodingstory.ui.activity.maps

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dicodingstory.R
import com.example.dicodingstory.data.remote.Result
import com.example.dicodingstory.databinding.ActivityMapsBinding
import com.example.dicodingstory.hawkstorage.HawkStorage
import com.example.dicodingstory.ui.activity.main.MainViewModel
import com.example.dicodingstory.ui.activity.main.MainViewModelFactory
import com.example.dicodingstory.utils.hideLoading
import com.example.dicodingstory.utils.showLoading
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var token: String

    private val factory: MainViewModelFactory = MainViewModelFactory.getInstance()
    private val mainViewModel: MainViewModel by viewModels {
        factory
    }

    private lateinit var mMap: GoogleMap

    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//
//        // Custom marker
//        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(dicodingSpace)
//                .title("Dicoding Space")
//                .snippet("Batik Kumeli No.50")
//                .icon(vectorToBitmap(resources, R.drawable.ic_location, Color.parseColor("#00aeff")))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//        )
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

//        getMyLocation()
        setMapStyle()
        addManyMarker()
    }

    private fun init() {
        setPref()
    }

    private fun setPref() {
        val user = HawkStorage.instance(this).getUser()
        token = user.loginResult?.token.toString()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()
    private fun addManyMarker() {
        mainViewModel.getStories(token).observe(this) {
            if (it != null) {
                when (it) {
                    is Result.Loading -> {
                        setLoading(true)
                    }
                    is Result.Success -> {
                        setLoading(false)
                        val error = it.data.error
                        if (!error) {
                            val listStory = it.data.listStory
                            listStory.forEach { story ->
                                val latLng = LatLng(story.lat, story.lon)
                                mMap.addMarker(MarkerOptions().position(latLng).title(story.name))?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                                boundsBuilder.include(latLng)
                            }

                            val bounds: LatLngBounds = boundsBuilder.build()
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
                        }
                    }
                    is Result.Error -> {
                        setLoading(false)
                        Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

//    private fun getMyLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this.applicationContext,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            mMap.isMyLocationEnabled = true
//        } else {
//            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }
//
//    // Permission for access location
//    private val requestPermissionLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { isGranted: Boolean ->
//            if (isGranted) {
//                getMyLocation()
//            }
//        }
    private fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            showLoading(this)
        } else {
            hideLoading()
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}