package com.example.liveearthmapuet

import android.os.Bundle
import android.os.PersistableBundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityNavigationBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener

class NavigationActivity : AppCompatActivity(), OnNavigationReadyCallback, NavigationListener {
    private lateinit var binding: ActivityNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Mapbox.getInstance(this, resources.getString(R.string.mapbox_access_token))
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.onCreate(savedInstanceState)
        binding.navigationView.initialize(this)
    }

    override fun onStart() {
        super.onStart()
        binding.navigationView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.navigationView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        binding.navigationView.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        binding.navigationView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.navigationView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.navigationView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.navigationView.onLowMemory()
    }

    private fun startNavigation() {
        if (Misc.route == null) {
            return
        }
        val options = NavigationViewOptions.builder()
            .directionsRoute(Misc.route)
            .shouldSimulateRoute(false)
            .navigationListener(this)
            .build()
        binding.navigationView.startNavigation(options)
    }

    override fun onNavigationReady(isRunning: Boolean) {
        startNavigation()
    }

    override fun onCancelNavigation() {
        if (binding.navigationView != null) {
            binding.navigationView.stopNavigation()
        }
        finish()
    }

    override fun onNavigationFinished() {}

    override fun onNavigationRunning() {}

    override fun onBackPressed() {
        binding.navigationView.stopNavigation()
//        Misc.onBackPress(this, Misc.isNavigationBackIntEnabled, object : OnBackPressCallBack {
//            override fun onBackPress() {
//                finish()
//            }
//        })
//        finish()
        super.onBackPressed()
    }
}