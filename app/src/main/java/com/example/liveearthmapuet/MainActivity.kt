package com.example.liveearthmapuet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.CustomDialog
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityMainBinding
import com.example.liveearthmapuet.interfaces.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager

class MainActivity : AppCompatActivity(), PermissionsListener {
    private lateinit var myIntent: Intent
    private var isNativeDisplayed = false
    private val lsvStoragePermission = 101
    private val micPermissionRequest = 1032
    private val cameraPermissionRequest = 100
    private val noteCamStoragePermission = 936
    private val altitudeStoragePermission = 989
    private val gpsMapCamStoragePermission = 93
    private val cameraPermissionRequestForGPSCam = 10
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(this, binding.adFrameLayoutNative, Misc.dashboardNative)
        Misc.setIsFirstTime(this, false)

        permissionsManager = PermissionsManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.quitBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.bottomSheet.quitBottomSheet.setOnClickListener { }

        Handler().postDelayed({
            val a: Animation =
                AnimationUtils.loadAnimation(this, R.anim.pop_up)
            binding.llLiveEarthMap.startAnimation(a)

            a.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    val a1: Animation =
                        AnimationUtils.loadAnimation(this@MainActivity, R.anim.pop_down)
                    binding.llLiveEarthMap.startAnimation(a1)
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })
        }, 1000)

        if (intent.getStringExtra(Misc.data) != null)
            if (Misc.isSplashInt.contains("am") || Misc.isSplashInt.contains("al")) {
                val objDialog = CustomDialog(this)
                objDialog.setCancelable(false)
                objDialog.setCanceledOnTouchOutside(false)
                objDialog.window?.setBackgroundDrawableResource(R.color.nothing)
                objDialog.show()
                Handler().postDelayed({
                    objDialog.dismiss()
                    Ads.loadAndShowInterstitial(this@MainActivity, Misc.isSplashInt, null)
                }, 2000)
            }

        binding.btnPro.setOnClickListener {
            val intent = Intent(this@MainActivity, PremiumScreenActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            Firebase.analytics.logEvent("ProScreen", null)
            startActivity(intent)
        }

        binding.btnMenu.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }

        binding.llSoundMeter.setOnClickListener {
            Firebase.analytics.logEvent("SoundMeter", null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.RECORD_AUDIO),
                        micPermissionRequest
                    )
                } else {
                    val intent =
                        Intent(this@MainActivity, SoundMeterActivity::class.java)
                    startMyActivity(intent, Misc.soundMeterInt)
                }
            } else {
                val intent =
                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                startMyActivity(intent, Misc.soundMeterInt)
            }

        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.blockOnClickMain.visibility = View.VISIBLE
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.blockOnClickMain.visibility = View.GONE
                }
            }
        })

        binding.blockOnClickMain.setOnClickListener {
            binding.blockOnClickMain.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.bottomSheet.btnYes.setOnClickListener {
            finishAffinity()
        }

        binding.bottomSheet.btnNo.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        binding.llLiveCam.setOnClickListener {

            val intent = Intent(this@MainActivity, LiveCamsActivity::class.java)
            Firebase.analytics.logEvent("LiveCam", null)

            startMyActivity(intent, Misc.liveCamInt)
        }

        binding.llWorldQuiz.setOnClickListener {
            Firebase.analytics.logEvent("WorldMapQuiz", null)
            startMyActivity(
                Intent(this@MainActivity, WorldQuizActivity::class.java),
                Misc.worldQuizInt
            )
        }

        binding.llSpeedometer.setOnClickListener {
            Firebase.analytics.logEvent("Speedometer", null)
            val intent = Intent(this@MainActivity, SpeedometerActivity::class.java)
            startMyActivity(intent, Misc.speedometerInt)
        }

        binding.llGPSMapCams.setOnClickListener {
            Firebase.analytics.logEvent("GPSMapCams", null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA),
                        cameraPermissionRequestForGPSCam
                    )
                } else {
                    Misc.getStoragePermission(
                        this,
                        gpsMapCamStoragePermission,
                        object : StoragePermissionInterface {
                            override fun onPermissionGranted() {
                                val intent = Intent(
                                    this@MainActivity,
                                    NoteCamActivity::class.java
                                )
                                intent.putExtra(Misc.data, Misc.data)
                                startMyActivity(intent, Misc.noteCamInt)
                            }
                        })

                }
            } else {
                Misc.getStoragePermission(
                    this,
                    gpsMapCamStoragePermission,
                    object : StoragePermissionInterface {
                        override fun onPermissionGranted() {
                            val intent =
                                Intent(this@MainActivity, NoteCamActivity::class.java)
                            intent.putExtra(Misc.data, Misc.data)
                            startMyActivity(intent, Misc.gpsCamInt)
                        }
                    })
            }

        }

        binding.llAltitude.setOnClickListener {
            Firebase.analytics.logEvent("Altitude", null)
            Misc.getStoragePermission(
                this,
                altitudeStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(this@MainActivity, AltitudeActivity::class.java),
                            Misc.altitudeInt
                        )
                    }
                })
        }
        binding.llCompass.setOnClickListener {
            Firebase.analytics.logEvent("llCompass", null)
            startMyActivity(
                Intent(this@MainActivity, CompassActivity::class.java),
                Misc.compassInt
            )
        }

        binding.llLiveEarthMap.setOnClickListener {
            Firebase.analytics.logEvent("LiveEarthMap", null)
            Ads.loadAndShowInterstitial(
                this,
                Misc.lemInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        val intent = Intent(this@MainActivity, LiveEarthActivity::class.java)
                        startActivity(intent)
                    }
                })
        }

        binding.llNoteCam.setOnClickListener {
            Firebase.analytics.logEvent("NoteCam", null)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getCameraPermission()
            } else {
                Misc.getStoragePermission(
                    this,
                    noteCamStoragePermission,
                    object : StoragePermissionInterface {
                        override fun onPermissionGranted() {
                            startMyActivity(
                                Intent(
                                    this@MainActivity,
                                    NoteCamActivity::class.java
                                ), Misc.noteCamInt
                            )
                        }
                    })
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), cameraPermissionRequest)
        } else {
            Misc.getStoragePermission(
                this,
                noteCamStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(
                                this@MainActivity,
                                NoteCamActivity::class.java
                            ), Misc.noteCamInt
                        )
                    }
                })
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty()) {
            Toast.makeText(this,
                getString(R.string.please_give_required_permission), Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (requestCode == lsvStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMyActivity(
                    Intent(
                        this@MainActivity,
                        LiveEarthActivity::class.java
                    ), Misc.lemInt
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (requestCode == gpsMapCamStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(this@MainActivity, NoteCamActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            startMyActivity(intent, Misc.gpsCamInt)
        }

        if (requestCode == noteCamStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(
                Intent(this@MainActivity, NoteCamActivity::class.java),
                Misc.noteCamInt
            )
        }

        if (requestCode == altitudeStoragePermission && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMyActivity(
                Intent(
                    this@MainActivity,
                    AltitudeActivity::class.java
                ), Misc.altitudeInt
            )
        }

        if (requestCode == cameraPermissionRequestForGPSCam && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Misc.getStoragePermission(
                this,
                gpsMapCamStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        val intent =
                            Intent(this@MainActivity, NoteCamActivity::class.java)
                        intent.putExtra(Misc.data, Misc.data)
                        startMyActivity(intent, Misc.gpsCamInt)
                    }
                })
        }

        if (requestCode == cameraPermissionRequest && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Misc.getStoragePermission(
                this@MainActivity,
                noteCamStoragePermission,
                object : StoragePermissionInterface {
                    override fun onPermissionGranted() {
                        startMyActivity(
                            Intent(
                                this@MainActivity,
                                NoteCamActivity::class.java
                            ), Misc.noteCamInt
                        )
                    }
                })
        }

        if (requestCode == micPermissionRequest) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(this@MainActivity, SoundMeterActivity::class.java)
                startMyActivity(intent, Misc.soundMeterInt)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.recording_permission_is_required_for_sound_meter),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG)
            .show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            startMyActivity(myIntent, Misc.lemInt)
        } else {
            Toast.makeText(this, getString(R.string.location_permission_is_required), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            return
        } else {
            Ads.loadAndShowInterstitial(this, Misc.isQuitInt, object : InterstitialCallBack {
                override fun onDismiss() {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                    Ads.showNativeAd(this@MainActivity, nativeAd, Misc.quitNativeAm_Al, null)
                }

            })
        }
    }

    fun startMyActivity(intent: Intent, isEnabled: String) {
        myIntent = intent
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            Ads.loadAndShowInterstitial(
                this,
                isEnabled,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        startActivity(intent)
                    }
                })
        } else {
            permissionsManager.requestLocationPermissions(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}