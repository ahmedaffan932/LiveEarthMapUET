package com.example.liveearthmapuet.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.LoadInterstitialCallBack

@SuppressLint("LogNotTimber")
object AdMobInterstitial {
    var interAdmob: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    //load Admob Interstitial
    fun loadInterAdmob(activity: Activity, callback: LoadInterstitialCallBack? = null) {
        if (!Misc.getPurchasedStatus(activity) && interAdmob == null) {
            val admobRequest = AdRequest.Builder().build()
            com.google.android.gms.ads.interstitial.InterstitialAd.load(
                activity,
                Misc.interstitialAdIdAdMob,
                admobRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(Misc.logKey, adError.message + adError.code)
                        interAdmob = null
                        callback?.onFailed()
                    }

                    override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                        Log.d(Misc.logKey, "Ad was loaded.")
                        interAdmob = interstitialAd
                        Misc.anyAdLoaded.value = true
                        callback?.onLoaded()
                    }
                })
        } else {
            callback?.onLoaded()
        }
    }

    fun showInterstitial(activity: Activity, callback: InterstitialCallBack? = null) {
        interAdmob?.show(activity)
        interAdmob?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("interAdmobShow", "Ad was dismissed.")
                callback?.onDismiss()
                interAdmob = null
                Ads.isShowingInt = false
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                Log.d("interAdmobShow", "Ad failed to show." + p0.message + p0.code)
                Ads.isShowingInt = false
                interAdmob = null
                callback?.onDismiss()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("interAdmobShow", "Ad showed fullscreen content.")
                Ads.isShowingInt = true
            }
        }
    }

}