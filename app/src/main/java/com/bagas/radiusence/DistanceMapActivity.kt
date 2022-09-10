package com.bagas.radiusence

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.TextJustify
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.generated.*
import com.bagas.radiusence.databinding.ActivityDistanceMapBinding
import com.mapbox.maps.plugin.annotation.annotations
import kotlin.math.roundToInt

class DistanceMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDistanceMapBinding

//    private lateinit var mapView: MapView
    private lateinit var map: MapboxMap

    companion object {
        const val EXTRA_DISTANCE = "extra_distance"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
        const val EXTRA_RADIUS = "extra_radius"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ResourceOptionsManager.getDefault(this, getString(R.string.access_token)).update {
            tileStoreUsageMode(TileStoreUsageMode.READ_ONLY)
        }
        binding = ActivityDistanceMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val distance = intent.getDoubleExtra(EXTRA_DISTANCE, 0.0)
        val latitude = intent.getStringExtra(EXTRA_LATITUDE)!!.toDouble()
        val longitude = intent.getStringExtra(EXTRA_LONGITUDE)!!.toDouble()
        val radiusPresence = intent.getIntExtra(EXTRA_RADIUS, 0).toDouble()

        Log.d("DistanceMapActivity", "Latitude : $latitude")
        Log.d("DistanceMapActivity", "Longitude : $longitude")

        setStatus(distance, radiusPresence)
        populateInfo(distance)

        val mapView = binding.mapView

        map = mapView.getMapboxMap()
        map.loadStyleUri(Style.MAPBOX_STREETS)

        val annotationApi = mapView.annotations
        val circleAnnotationManager = annotationApi.createCircleAnnotationManager(mapView)
        val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
        val polylineAnnotationManager  = annotationApi.createPolylineAnnotationManager(mapView)

        // Draw point
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(longitude, latitude))
            .withIconSize(0.08)
            .withIconImage(BitmapFactory.decodeResource(resources, R.drawable.user))
            .withTextJustify(TextJustify.CENTER)
            .withTextAnchor(TextAnchor.TOP)
            .withTextField("Me")

        pointAnnotationManager.create(pointAnnotationOptions)

        // Draw polyline
        val polylinePoints = listOf(
            Point.fromLngLat(longitude, latitude),
            Point.fromLngLat(107.9236944, -6.8359167)
        )

        val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
            .withPoints(polylinePoints)
            .withLineColor("#ee4e8b")
            .withLineWidth(5.0)
        polylineAnnotationManager.create(polylineAnnotationOptions)

        // Draw circle radius
        val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
            .withPoint(Point.fromLngLat(107.9236944, -6.8359167))
            .withCircleRadius(radiusPresence)
            .withCircleColor("#ee4e8b")
            .withCircleOpacity(0.8)
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")
        circleAnnotationManager.create(circleAnnotationOptions)

        // Camera Movement
        map.flyTo(
            cameraOptions {
                center(Point.fromLngLat(107.9236944, -6.8359167))
//                pitch(10.0)
                zoom(17.0)
                bearing(5.0)
            },
            MapAnimationOptions.mapAnimationOptions { duration(5000) }
        )

        binding.btnCheckMyposition.setOnClickListener {
            map.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(longitude, latitude))
//                    pitch(10.0)
                    zoom(15.0)
                    bearing(5.0)

                }, MapAnimationOptions.mapAnimationOptions {
                    duration(5000)
                }
            )
        }

        binding.btnCheckRadiusposition.setOnClickListener {
            map.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(107.9236944, -6.8359167))
//                    pitch(10.0)
                    zoom(15.0)
                    bearing(5.0)

                }, MapAnimationOptions.mapAnimationOptions {
                    duration(5000)
                }
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateInfo(distance: Double) {
        binding.tvDistance.text = "${distance.roundToInt()}m"
    }

    private fun setStatus(distance: Double, radius: Double) {
        if (distance > radius) {
            binding.iconStatus.setImageResource(R.drawable.ic_cancel)
            binding.tvTextStatus.text = "You are out of radius"
        } else {
            binding.iconStatus.setImageResource(R.drawable.ic_check)
            binding.tvTextStatus.text = "You are in radius"
        }
    }

    @SuppressLint("Lifecycle")
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    @SuppressLint("Lifecycle")
    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    @SuppressLint("Lifecycle")
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
}