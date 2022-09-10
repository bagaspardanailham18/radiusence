package com.bagas.radiusence

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bagas.radiusence.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var latitude: Double? = 0.0
    var longitude: Double? = 0.0
    lateinit var currentLoc: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

//    override fun onStart() {
//        super.onStart()
//        when {
//            PermissionUtils.checkAccessFineLocationGranted(this) -> {
//                when {
//                    PermissionUtils.isLocationEnabled(this) -> {
//                        setUpLocationListener()
//                    }
//                    else -> {
//                        PermissionUtils.showGPSNotEnabledDialog(this)
//                    }
//                }
//            }
//            else -> {
//                PermissionUtils.requestAccessFineLocationPermission(
//                    this,
//                    1
//                )
//            }
//        }
//    }

//    @SuppressLint("MissingPermission")
//    private fun setUpLocationListener() {
//        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000).setPriority(
//            LocationRequest.PRIORITY_HIGH_ACCURACY)
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                super.onLocationResult(p0)
//                for (location in p0.locations) {
//                    latitude = location.latitude
//                    longitude = location.longitude
//                }
//                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
//                    val geoCoder = Geocoder(this@MainActivity)
//                    val currentLocation = geoCoder.getFromLocation(
//                        latitude!!,
//                        longitude!!,
//                        1
//                    )
//                    Log.d("Cek", "latitude : $latitude")
//                    Log.d("Cek", "longitude : $longitude")
//                    currentLoc = "${currentLocation.first().subLocality}, ${currentLocation.first().locality}, ${currentLocation.first().countryCode}"
//                }
//            }
//        }, Looper.getMainLooper())
//    }


//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            1 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    when {
//                        PermissionUtils.isLocationEnabled(this) -> {
//                            LocationListener {  }
//                        }
//                        else -> {
//                            PermissionUtils.showGPSNotEnabledDialog(this)
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
}