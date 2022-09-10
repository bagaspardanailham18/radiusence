package com.bagas.radiusence.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.navArgs
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.bagas.radiusence.DistanceMapActivity
import com.bagas.radiusence.Geo
import com.bagas.radiusence.MainActivity
import com.bagas.radiusence.R
import com.bagas.radiusence.data.model.PresenceUsers
import com.bagas.radiusence.databinding.ActivityPresenceFormBinding

class PresenceFormActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var binding: ActivityPresenceFormBinding
    private val args: PresenceFormActivityArgs by navArgs<PresenceFormActivityArgs>()
    private var presenceStatus = false

    private var radiusPresence: Int? = null
    private var haversineDistance: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.bagas.radiusence.databinding.ActivityPresenceFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val nim = binding.edtNim.text.toString().trim()
        val name = sharedPref?.getString("name", null)
        val latitude = args.latitude
        val longitude = args.longitude
        val location = args.location
        radiusPresence = args.radiuspresence

        Log.d("Presence", "latitude : $latitude")
        Log.d("Presence", "longitude : $longitude")

        val originCoordinate = Geo(latitude.toDouble(), longitude.toDouble())
//        val destinationCoordinate = Geo(-6.8360514, 107.9238614)
        val destinationCoordinate = Geo(-6.8359167, 107.9236944)
//        val destinationCoordinate = Geo(-6.8334671, 107.9231212)
//        val destinationCoordinate = Geo(-6.8359343, 107.9239448)
        haversineDistance = originCoordinate.haversine(destinationCoordinate)

        binding.btnTomap.setOnClickListener {
            toDistanceMap(haversineDistance!!, latitude, longitude, radiusPresence!!)
        }

        binding.txtInfo.setText(getString(R.string.text_info, radiusPresence.toString()))
        binding.edtName.setText(name.toString())
        binding.edtLat.setText(latitude)
        binding.edtLong.setText(longitude)
        binding.edtLocation.setText(location)

        binding.btnTakePresence.setOnClickListener {
            if (name == null || binding.edtName.text!!.isEmpty()) {
                binding.layoutEdtName.error = "Nama harus diisi!!"
                return@setOnClickListener
            }

            if (nim.isEmpty() || nim.equals("") || nim == "") {
                binding.layoutEdtNim.error = "Nim harus diisi!!"
                return@setOnClickListener
            }

            takePresence(nim, name, latitude.toDouble(), longitude.toDouble(), location)
            showDialog()
        }

    }

    private fun takePresence(nim: String, name: String, lat: Double, lon: Double, loc: String) {
        val originCoordinate = Geo(lat, lon)
//        val destinationCoordinate = Geo(-6.8360514, 107.9238614)
        val destinationCoordinate = Geo(-6.8359167, 107.9236944)
//        val destinationCoordinate = Geo(-6.8334671, 107.9231212)
//        val destinationCoordinate = Geo(-6.8359343, 107.9239448)
        //haversineDistance = originCoordinate.haversine(destinationCoordinate)

        if (haversineDistance!! > radiusPresence!!) {
            presenceStatus = false
            val user = PresenceUsers(
                nim = nim,
                name = name,
                latitude = lat,
                longitude = lon,
                address = loc,
                distance = haversineDistance!!,
                presenceStatus = "Failed",
            )
            database.child("Users").push().setValue(user)
                .addOnCompleteListener {
                    Toast.makeText(this, "Presence Status : Denied", Toast.LENGTH_LONG).show()
                }
        } else {
            presenceStatus = true
            val user = PresenceUsers(
                nim = nim,
                name = name,
                latitude = lat,
                longitude = lon,
                address = loc,
                distance = haversineDistance!!,
                presenceStatus = "Success"
            )
            database.child("Users").push().setValue(user)
                .addOnCompleteListener {
                    Toast.makeText(this, "Presence Status : Present", Toast.LENGTH_LONG).show()
                }
        }
    }

    @SuppressLint("InflateParams")
    private fun showDialog() {
        val dialog: Dialog = Dialog(this)
        val dialogBinding = layoutInflater.inflate(R.layout.custom_dialog, null)
        dialog.setContentView(dialogBinding)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)

        if (presenceStatus) {
            val dialogIcon = dialog.findViewById<ImageView>(R.id.dialog_icon_status)
            val dialogTitle = dialog.findViewById<TextView>(R.id.dialog_title)
            val dialogMessage = dialog.findViewById<TextView>(R.id.dialog_message)
            dialogIcon.setImageResource(R.drawable.ic_success_check)
            dialogTitle.setText(R.string.dialog_success_title)
            dialogMessage.setText(R.string.dialog_success_message)
        } else {
            val dialogIcon = dialog.findViewById<ImageView>(R.id.dialog_icon_status)
            val dialogTitle = dialog.findViewById<TextView>(R.id.dialog_title)
            val dialogMessage = dialog.findViewById<TextView>(R.id.dialog_message)
            dialogIcon.setImageResource(R.drawable.ic_failed_red)
            dialogTitle.setText(R.string.dialog_failed_title)
            dialogTitle.setTextColor(resources.getColor(R.color.redDark))
            dialogMessage.setText(R.string.dialog_failed_message)
        }

        val btnOk: AppCompatButton = dialog.findViewById(R.id.dialog_btn_ok)
        btnOk.setOnClickListener {
            dialog.dismiss()
            val toMain = Intent(this, MainActivity::class.java)
            startActivity(toMain)
            finishAffinity()
        }

        dialog.show()
    }

    private fun toDistanceMap(distance: Double, lat: String, long: String, radius: Int) {
        val intent = Intent(this, DistanceMapActivity::class.java)
        intent.putExtra(DistanceMapActivity.EXTRA_DISTANCE, distance)
        intent.putExtra(DistanceMapActivity.EXTRA_LATITUDE, lat)
        intent.putExtra(DistanceMapActivity.EXTRA_LONGITUDE, long)
        intent.putExtra(DistanceMapActivity.EXTRA_RADIUS, radius)
        startActivity(intent)
    }
}