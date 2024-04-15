package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityAltitudeBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.OnImageSaveCallBack
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.tarek360.instacapture.Instacapture
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener

class AltitudeActivity : AppCompatActivity(), PermissionsListener,
    OnMapReadyCallback, MapboxMap.OnMapClickListener {
    lateinit var binding: ActivityAltitudeBinding
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapBoxStyle: Style
    private lateinit var locationCallback: LocationCallback
    var isCurrentLocation = true
    private lateinit var droppedMarkerLayer: Layer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))
        binding = ActivityAltitudeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.btnScreenShot.setOnClickListener {

            Instacapture.capture(this, object : SimpleScreenCapturingListener() {
                override fun onCaptureComplete(bitmap: Bitmap) {

                    Misc.saveImageToExternal(
                        this@AltitudeActivity,
                        bitmap,
                        object : OnImageSaveCallBack {
                            override fun onImageSaved() {
                                Toast.makeText(
                                    this@AltitudeActivity,
                                    "Screen Shot Saved in Gallery. ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })

                }
            })
        }

        binding.btnZoomIn.setOnClickListener {
            val position = CameraPosition.Builder()
                .target(mapboxMap.cameraPosition.target)
                .zoom(mapboxMap.cameraPosition.zoom + 1)
                .tilt(mapboxMap.cameraPosition.tilt)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
        }

        binding.btnBackAltitude.setOnClickListener {
            onBackPressed()
        }

        binding.btnZoomOut.setOnClickListener {
            val position = CameraPosition.Builder()
                .target(mapboxMap.cameraPosition.target)
                .zoom(mapboxMap.cameraPosition.zoom - 1)
                .tilt(mapboxMap.cameraPosition.tilt)
                .build()
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
        }

        binding.tvDefault.setTextColor(ContextCompat.getColor(this, R.color.yellow))
        binding.llDefault.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.OUTDOORS)
            binding.tvDefault.setTextColor(ContextCompat.getColor(this, R.color.yellow))
        }

        binding.llSatellite.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.SATELLITE)
            binding.tvSatellite.setTextColor(ContextCompat.getColor(this, R.color.yellow))
        }
        binding.llTerrain.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.SATELLITE_STREETS)
            binding.tvTerrain.setTextColor(ContextCompat.getColor(this, R.color.yellow))
        }
        binding.llHybrid.setOnClickListener {
            setBtnTextWhiteColor()
            setMapBoxStyle(Style.DARK)
            binding.tvHybrid.setTextColor(ContextCompat.getColor(this, R.color.yellow))
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    override fun onPermissionResult(granted: Boolean) {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        setMapBoxStyle(Style.OUTDOORS)
    }

    override fun onMapClick(point: LatLng): Boolean {
//        Log.d(Misc.logKey, point.toString())
        return true
    }


    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        mapView.onResume()

        val bannerAdFrameLayout = if (Misc.isBannerAdTop) {
            binding.bannerAdFrameLayoutTop
        } else {
            binding.bannerAdFrameLayoutBottom
        }

        Ads.showBannerAd(Misc.isCompassBannerEnabled, bannerAdFrameLayout)

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
                        val loc = p0.lastLocation
                        if (isCurrentLocation) {
                            binding.tvAltitude.text = loc.altitude.toString()
                            binding.tvLatLngAltitude.text =
                                "Latitude:${loc.latitude}, Longitude: ${loc.longitude}"

                            val p = LatLng()
                            p.latitude = loc.latitude
                            p.longitude = loc.longitude
                            animateCamera(p, 14.0)
//                            setMarker(p)
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
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
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


    private fun setBtnTextWhiteColor() {
        binding.tvDefault.setTextColor(Color.parseColor("#ffffff"))
        binding.tvHybrid.setTextColor(Color.parseColor("#ffffff"))
        binding.tvSatellite.setTextColor(Color.parseColor("#ffffff"))
        binding.tvTerrain.setTextColor(Color.parseColor("#ffffff"))
    }

}