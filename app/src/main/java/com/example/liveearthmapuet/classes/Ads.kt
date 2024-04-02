package com.example.liveearthmapuet.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.FrameLayout
import com.example.liveearthmapuet.R
import com.google.android.gms.ads.*
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.LoadInterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

@SuppressLint("LogNotTimber")
object Ads {
    var isShowingInt = false

    var isSplashNativeDisplayed = false


    fun loadAndShowInterstitial(
        activity: Activity,
        isEnabled: String,
        callBack: InterstitialCallBack?
    ) {
        if (isEnabled.contains("am")) {
            if (AdMobInterstitial.interAdmob != null) {
                AdMobInterstitial.showInterstitial(activity, callBack)
            } else {
                val objDialog = CustomDialog(activity)
                objDialog.setCancelable(false)
                objDialog.setCanceledOnTouchOutside(false)
                objDialog.window?.setBackgroundDrawableResource(R.color.nothing)
                objDialog.show()

                AdMobInterstitial.loadInterAdmob(activity, object : LoadInterstitialCallBack {
                    override fun onLoaded() {
                        objDialog.dismiss()
                        AdMobInterstitial.showInterstitial(activity, callBack)
                    }

                    override fun onFailed() {
                        objDialog.dismiss()
                        callBack?.onDismiss()
                    }
                })
            }
        } else {
            callBack?.onDismiss()
        }
    }

    fun showNativeAd(
        activity: Activity,
        adFrameLayout: FrameLayout,
        isEnabled: String,
        callBack: NativeAdCallBack? = null
    ) {
        if (isEnabled.contains("am")) {
            showNativeAdAdMob(activity, adFrameLayout, isEnabled, callBack)
        }
    }


    private fun showNativeAdAdMob(
        activity: Activity,
        amLayout: FrameLayout,
        isEnabled: String,
        callBack: NativeAdCallBack?
    ) {
        if (isEnabled.contains("small")) {
            AdMobNative.showSmallNativeAd(activity, amLayout, callBack)
        } else {
            AdMobNative.showNativeAd(activity, amLayout, callBack)
        }
    }


    fun showBannerAd(isEnabled: Boolean, frameLayout: FrameLayout) {
        if (isEnabled) {
            AdMobBannerAds.show(frameLayout)
        }
    }
}