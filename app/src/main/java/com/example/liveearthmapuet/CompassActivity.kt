package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Misc
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.databinding.ActivityCompassBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack

import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import edu.arbelkilani.compass.CompassListener
import java.io.IOException
import java.util.*


class CompassActivity() : AppCompatActivity(), OnMapReadyCallback,
    MapboxMap.OnMapClickListener {
    private val point: LatLng = LatLng()
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private val REQUEST_CODE_AUTOCOMPLETE = 1
    private lateinit var locationCallback: LocationCallback
    var isCurrentLocation = true
    private lateinit var mapBoxStyle: Style
    private lateinit var droppedMarkerLayer: Layer
    private lateinit var binding: ActivityCompassBinding

    companion object {
        private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityCompassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.btnBackCompass.setOnClickListener {
            onBackPressed()
        }

        val pm = packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {
            Toast.makeText(this,
                getString(R.string.your_device_does_not_support_compass), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        binding.compass1.setListener(object : CompassListener {
            @SuppressLint("LogNotTimber")
            override fun onSensorChanged(event: SensorEvent) {
                Log.d("TAG", "onSensorChanged : $event")
            }

            @SuppressLint("LogNotTimber")
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.d("TAG", "onAccuracyChanged : sensor : $sensor")
                Log.d("TAG", "onAccuracyChanged : accuracy : $accuracy")
            }

        })

        binding.btnGetCurrentLocationCompass.setOnClickListener {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {
                isCurrentLocation = true
            }
        }

    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        setMapBoxStyle(Style.OUTDOORS)
    }

    @SuppressLint("LogNotTimber")
    override fun onMapClick(point: LatLng): Boolean {
        Log.d("TAG", "OnMapClickCompass")
        return true
    }

    @SuppressLint("MissingPermission")
    private fun setMapBoxStyle(styleName: String) {
        mapboxMap.setStyle(
            styleName
        ) { style ->
            mapBoxStyle = style
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps()
            } else {
                locationCallback = object : LocationCallback() {
                    @SuppressLint("SetTextI18n", "LogNotTimber")
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                        Log.d(Misc.logKey, p0.toString())
                        point.latitude = p0.lastLocation.latitude
                        point.longitude = p0.lastLocation.longitude
                        if (isCurrentLocation && this@CompassActivity::mapboxMap.isInitialized) {
                            animateCamera(point, 14.0)
                            getAddress(point)
                        }
                    }
                }

                val locationRequest = LocationRequest.create()
                locationRequest.fastestInterval = 1000
                locationRequest.interval = 5000
                LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
            }

            mapboxMap.addOnMapClickListener(this)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun animateCamera(p: LatLng, zoom: Double) {
        mapboxMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder()
                    .target(
                        LatLng(
                            p.latitude,
                            p.longitude
                        )
                    )
                    .zoom(zoom)
                    .build()
            ), 4000
        )

    }


    @SuppressLint("LogNotTimber", "SetTextI18n")
    private fun getAddress(p: LatLng): String? {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        return try {
            addresses = geocoder.getFromLocation(p.latitude, p.longitude, 1)
            if (!(addresses == null || addresses.isEmpty())) {
                mapboxMap.getStyle { style ->
                    binding.tvAddressCompass.text = addresses[0].getAddressLine(0)
                    binding.tvLatLngCompass.text = "Latitude:${p.latitude}, Longitude:${p.longitude}"
                    Log.d(Misc.logKey, addresses[0].getAddressLine(0))
                }
                addresses[0].countryName
            } else "null"
        } catch (ignored: IOException) {
            ignored.printStackTrace()
            "Exception"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            isCurrentLocation = false

            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            point.latitude = (selectedCarmenFeature.geometry() as Point?)!!.latitude()
            point.longitude = (selectedCarmenFeature.geometry() as Point?)!!.longitude()
            animateCamera(point, 14.0)
            getAddress(point)
        }
    }


    @SuppressLint("MissingPermission")
    public override fun onResume() {
        super.onResume()
        val bannerAdFrameLayout = if (Misc.isBannerAdTop) {
            binding.bannerAdFrameLayoutTop
        } else {
            binding.bannerAdFrameLayoutBottom
        }

        Ads.showBannerAd(Misc.isCompassBannerEnabled, bannerAdFrameLayout)

        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.your_gps_seems_to_be_disabled_do_you_want_to_enable_it))
            .setCancelable(false)
            .setPositiveButton(
                "Yes"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton(
                "No"
            ) { dialog, id -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun setMarker(point: LatLng) {
        if (mapBoxStyle.getLayer(LiveEarthActivity.DROPPED_MARKER_LAYER_ID) != null) {
            val source = mapBoxStyle.getSourceAs<GeoJsonSource>("dropped-marker-source-id")
            source?.setGeoJson(
                Point.fromLngLat(
                    point.longitude,
                    point.latitude
                )
            )
            droppedMarkerLayer = mapBoxStyle.getLayer(LiveEarthActivity.DROPPED_MARKER_LAYER_ID)!!
            droppedMarkerLayer.setProperties(PropertyFactory.visibility(Property.VISIBLE))

        }
    }

}