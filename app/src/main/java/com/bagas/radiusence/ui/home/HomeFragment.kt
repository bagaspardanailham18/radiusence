package com.bagas.radiusence.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bagas.radiusence.FirebaseService
import com.google.android.gms.location.*
import com.google.firebase.database.*
import com.bagas.radiusence.LocationService
import com.bagas.radiusence.R
import com.bagas.radiusence.data.datastore.StoreUserData
import com.bagas.radiusence.data.model.Courses
import com.bagas.radiusence.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: com.bagas.radiusence.databinding.FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var locationService: LocationService

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var userRef: DatabaseReference

    private lateinit var courseList: MutableList<Courses>

    private lateinit var adapter: ListCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationService = LocationService(requireActivity())
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        auth = Firebase.auth
        databaseRef = FirebaseDatabase.getInstance().getReference("Courses")
        userRef = FirebaseDatabase.getInstance().getReference("Users")

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        latitude = (activity as MainActivity).latitude
//        longitude = (activity as MainActivity).longitude

        val sharedPref = activity?.getSharedPreferences("myPref", Context.MODE_PRIVATE)
        Log.d("Data", "UserID = ${sharedPref?.getString("userId", null)}")

        userRef.child(auth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString().trim()
                    val nim = snapshot.child("nim").value.toString().trim()
                    val email = snapshot.child("email").value.toString().trim()
                    val faculty = snapshot.child("faculty").value.toString().trim()
                    val major = snapshot.child("major").value.toString().trim()
                    val usertype = snapshot.child("usertype").value.toString().trim()
                    val avatarUrl = snapshot.child("avatarUrl").value.toString().trim()

                    binding.tvName.text = name
                    binding.tvNim.text = nim
                    binding.tvMajor.text = major
                    binding.tvFacultyUsertype.text = getString(R.string.tv_faculty_usertype, faculty, usertype)
                    Glide.with(requireActivity())
                        .load(avatarUrl)
                        .into(binding.tvImgUser)

                    Log.d("Home", name)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        courseList = mutableListOf()
        adapter = ListCourseAdapter(courseList)

        showLoading(true)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    courseList.clear()
                    showLoading(false)
                    shownotfound(false)

                    for (c in snapshot.children) {
                        val data = c.getValue(Courses::class.java)
                        if (data != null) {
                            courseList.add(data)
                        }
                    }

                    binding.rvCourses.adapter = adapter
                    binding.rvCourses.layoutManager = LinearLayoutManager(requireActivity())
                    binding.rvCourses.setHasFixedSize(true)
                } else {
                    shownotfound(true)
                    showLoading(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


//        binding.classMenu.setOnClickListener(this)

//        binding.cardItemPresence.setOnClickListener {
//            if (locationService.hasLocationPersmission()) {
//                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
//                    val geoCoder = Geocoder(requireActivity())
//                    val currentLocation = geoCoder.getFromLocation(
//                        location.latitude,
//                        location.longitude,
//                        1
//                    )
//
//                    val toPresenceFormActivity = HomeFragmentDirections.actionNavigationHomeToPresenceFormActivity()
//                    toPresenceFormActivity.latitude = location.latitude.toString()
//                    toPresenceFormActivity.longitude = location.longitude.toString()
//                    toPresenceFormActivity.location = "${currentLocation.first().subLocality}, ${currentLocation.first().locality}, ${currentLocation.first().countryCode}"
//                    it.findNavController().navigate(toPresenceFormActivity)
//                }
//            } else {
//                locationService.requestLocationPermission()
//            }
//        }

        adapter.setOnItemClickCallback(object : ListCourseAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Courses) {
                if (locationService.hasLocationPersmission()) {
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        val geoCoder = Geocoder(requireActivity())
                        val currentLocation = geoCoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1
                        )

                        Log.d("Datalok", "lat : ${location.latitude}")
                        Log.d("Datalok", "long : ${location.longitude}")
                        Log.d("Datalok", "loc : ${currentLocation.first().subLocality}")
                        Log.d("Datalok", "radius : ${data.radiuspresence}")

                        if (location != null) {
                            val toPresenceFormActivity = HomeFragmentDirections.actionNavigationHomeToPresenceFormActivity()
                            toPresenceFormActivity.radiuspresence = data.radiuspresence!!.toInt()
                            toPresenceFormActivity.latitude = location.latitude.toString()
                            toPresenceFormActivity.longitude = location.longitude.toString()
                            toPresenceFormActivity.location = "${currentLocation.first().subLocality}, ${currentLocation.first().locality}, ${currentLocation.first().countryCode}"
                            view.findNavController().navigate(toPresenceFormActivity)
                        } else {
//                            val toPresenceFormActivity = HomeFragmentDirections.actionNavigationHomeToPresenceFormActivity()
//                            toPresenceFormActivity.radiuspresence = data.radiuspresence!!.toInt()
//                            toPresenceFormActivity.latitude = location.latitude.toString()
//                            toPresenceFormActivity.longitude = location.longitude.toString()
//                            toPresenceFormActivity.location = "${currentLocation.first().subLocality}, ${currentLocation.first().locality}, ${currentLocation.first().countryCode}"
//                            view.findNavController().navigate(toPresenceFormActivity)
                            Toast.makeText(requireActivity(), "Lokasi terakhir tidak terdeteksi", Toast.LENGTH_SHORT).show()
                            val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                                override fun onLocationResult(p0: LocationResult) {
                                    super.onLocationResult(p0)
                                    for (location in p0.locations) {
                                        var latitude = location.latitude
                                        var longitude = location.longitude
                                    }

                                    val toPresenceFormActivity = HomeFragmentDirections.actionNavigationHomeToPresenceFormActivity()
                                    toPresenceFormActivity.radiuspresence = data.radiuspresence!!.toInt()
                                    toPresenceFormActivity.latitude = p0.locations[0].latitude.toString()
                                    toPresenceFormActivity.longitude = p0.locations[0].longitude.toString()
                                    toPresenceFormActivity.location = "${currentLocation.first().subLocality}, ${currentLocation.first().locality}, ${currentLocation.first().countryCode}"
                                    view.findNavController().navigate(toPresenceFormActivity)
                                }
                            }, Looper.myLooper())
                        }
                    }

                } else {
                    locationService.requestLocationPermission()
                }

//                val toPresenceFormActivity = HomeFragmentDirections.actionNavigationHomeToPresenceFormActivity()
//                toPresenceFormActivity.radiuspresence = data.radiuspresence!!.toInt()
//                toPresenceFormActivity.latitude = latitude
//                toPresenceFormActivity.longitude = longitude
//                toPresenceFormActivity.location = currentLoc
//                view.findNavController().navigate(toPresenceFormActivity)
//
//                Log.d("Cek", "latitude : $latitude")
//                Log.d("Cek", "longitude : $latitude")
//                Log.d("Cek", "latitude : $latitude")
            }

        })
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun shownotfound(show: Boolean) {
        if (show) {
            binding.rvCourses.visibility = View.GONE
            binding.textNotfound.visibility = View.VISIBLE
        } else {
            binding.rvCourses.visibility = View.VISIBLE
            binding.textNotfound.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
//        when (v?.id) {
//            R.id.class_menu -> {
//                val toClassActivity = HomeFragmentDirections.actionNavigationHomeToClassActivity()
//                view?.findNavController()?.navigate(toClassActivity)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}