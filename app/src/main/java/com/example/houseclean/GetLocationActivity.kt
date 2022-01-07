package com.example.houseclean

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.houseclean.databinding.ActivityGetLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import java.util.*

class GetLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityGetLocationBinding
    private var granted = true
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        granted = granted && it
        if (!granted) Toast.makeText(this, "Permissions needed!", Toast.LENGTH_SHORT).show()
        //if (granted) fetchLocation()
    }
    private var currentLocation: Location? = null
    private var currentLatLng: String? = null
    private var currentMarker: Marker? = null
    private var addressLine: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addLocationBtn.setCircleBackgroundColorResource(android.R.color.darker_gray)
        binding.addLocationBtn.setOnClickListener{
            val intent = Intent()
            intent.putExtra("address", addressLine)
            intent.putExtra("location", currentLatLng)
            setResult(RESULT_OK, intent)
            finish()
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
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

        val latLng = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        drawMarker(latLng)

        mMap.setOnMapLongClickListener(object: GoogleMap.OnMapLongClickListener {
            override fun onMapLongClick(latLng: LatLng) {
                currentLatLng = latLng.latitude.toString().plus(",").plus(latLng.longitude)
                if (currentMarker != null) currentMarker?.remove()
                drawMarker(latLng)
            }
        })

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {
                TODO("Not yet implemented")
            }

            override fun onMarkerDragEnd(marker: Marker) {
                if (currentMarker != null) currentMarker?.remove()
                val newLatLng = LatLng(marker.position.latitude, marker.position.longitude)
                drawMarker(newLatLng)
            }

            override fun onMarkerDragStart(p0: Marker) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun drawMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("My house").snippet(getAddress(latLng.latitude, latLng.longitude)).draggable(true)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addresses = geoCoder.getFromLocation(lat, lng, 1)
        val adrLn = addresses[0].getAddressLine(0).toString()
        addressLine = adrLn
        return adrLn
    }

    private fun getPermissions() {
        requestPermission.launch(Manifest.permission.INTERNET)
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            getPermissions()
            if (!granted) {
                granted = true
                finish()
            }
            //return
        }
        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
            if(it != null) {
                this.currentLocation = it
                this.currentLatLng = it.latitude.toString().plus(",").plus(it.longitude)
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }
}