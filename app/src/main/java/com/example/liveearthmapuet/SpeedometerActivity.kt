package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivitySpeedometerBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack
import java.util.concurrent.TimeUnit

@SuppressLint("LogNotTimber")
class SpeedometerActivity : AppCompatActivity() {
    lateinit var binding: ActivitySpeedometerBinding
    private lateinit var locationManager: LocationManager
    private lateinit var listener: LocationListener
    private var isStarted = false
    var maxSpeed = 0
    var distance = 0.0
    var previousLocation: Location? = null
    var previousTime: Long = 0
    private val handler: Handler = Handler()

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySpeedometerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackSpeedometer.setOnClickListener {
            onBackPressed()
        }
        Ads.showNativeAd(
            this,
            binding.nativeAd,
            Misc.speedoMeterNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        binding.btnStart.setOnClickListener {
            if (!isStarted) {
                maxSpeed = 0
                distance = 0.0
                Misc.startingTime = System.currentTimeMillis()
                previousLocation = null
                handler.post(runTimer)
                isStarted = true
                binding.textStart.text = getString(R.string.stop)
                binding.imgBtnStart.setImageResource(R.drawable.ic_baseline_stop_24)
            } else {
                handler.removeCallbacks(runTimer)
                isStarted = false
                binding.textStart.text = getString(R.string.start)
                binding.textSpeedDigital.text = "0"

                binding.imgBtnStart.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                location.latitude
                location.longitude
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {
            }
        }
        locationManager.requestLocationUpdates("gps", 0, 0f, listener)
    }

    @SuppressLint("DefaultLocale")
    private fun setTime(textView: TextView) {
        val millis: Long = System.currentTimeMillis() - Misc.startingTime
        val hms = java.lang.String.format(
            "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
            TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    millis
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    millis
                )
            )
        )
        textView.text = hms
    }


    private val runTimer: Runnable by lazy {
        return@lazy object : Runnable {
            override fun run() {
                setTime(binding.textTimeDigital)
                handler.postDelayed(this, 1000)
            }
        }
    }


    override fun onBackPressed() {
        Ads.loadAndShowInterstitial(this, Misc.soundMeterBackInt, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }

}