package com.example.liveearthmapuet.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdRequest.Builder
import com.example.liveearthmapuet.interfaces.LoadInterstitialCallBack


object AdMobBannerAds {
    private var adView: AdView? = null
    private var isLoaded = false


    fun load(context: Activity, callBack: LoadInterstitialCallBack) {

        if (!Misc.getPurchasedStatus(context)
        ) {
            adView = AdView(context)
            adView?.adUnitId = Misc.bannerAdId
            val adSize = getAdSize(context)
//            adView?.adSize = AdSize.MEDIUM_RECTANGLE
            val adRequest = Builder().build()
            adView?.loadAd(adRequest)

            adView!!.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    isLoaded = true
                    callBack.onLoaded()
                }

                @SuppressLint("LogNotTimber")
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(
                        "TAG",
                        "onAdFailedToLoad:Adaptive Banner  ${adError.code}: ${adError.message}"
                    )
                    isLoaded = false
                    callBack.onFailed()
                }

                override fun onAdOpened() {}
                override fun onAdClicked() {}
                override fun onAdClosed() {}
            }

        } else {
            adView = null
        }

    }

    private fun getAdSize(context: Activity): AdSize {
        val display: Display = context.windowManager.defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        val density = displayMetrics.density
        var adwidthpixels: Float = displayMetrics.widthPixels.toFloat()
        if (adwidthpixels == 0f) {
            adwidthpixels = displayMetrics.widthPixels.toFloat()
        }
        val adWith = (adwidthpixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWith)
    }


    fun show(view: FrameLayout) {
        if (adView != null && isLoaded) {
            view.visibility = View.VISIBLE
            if (adView?.parent != null) {
                (adView?.parent as ViewGroup).removeAllViews() // <- fix
            }
            view.removeAllViews()
            view.addView(adView)
        } else {
            view.visibility = View.GONE
        }
    }

}