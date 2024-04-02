package com.example.liveearthmapuet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.liveearthmapuet.classes.AdMobInterstitial
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityLoadingAdBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.LoadInterstitialCallBack

class LoadingAdActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoadingAdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        binding = ActivityLoadingAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Misc.canRequestAds) {
            AdMobInterstitial.loadInterAdmob(this, object : LoadInterstitialCallBack {
                override fun onLoaded() {
                    AdMobInterstitial.showInterstitial(
                        this@LoadingAdActivity,
                        object : InterstitialCallBack {
                            override fun onDismiss() {
                                if (Misc.isFirstTime(this@LoadingAdActivity)) {
                                    startActivity(
                                        Intent(
                                            this@LoadingAdActivity,
                                            AppLanguageSelectorActivity::class.java
                                        )
                                    )
                                } else {
                                    startActivity(
                                        Intent(
                                            this@LoadingAdActivity,
                                            MainActivity::class.java
                                        )
                                    )
                                }
                                finish()
                            }
                        })
                }

                override fun onFailed() {
                    if (Misc.isFirstTime(this@LoadingAdActivity)) {
                        startActivity(
                            Intent(
                                this@LoadingAdActivity,
                                AppLanguageSelectorActivity::class.java
                            )
                        )
                    } else {
                        startActivity(
                            Intent(
                                this@LoadingAdActivity,
                                MainActivity::class.java
                            )
                        )
                    }
                    finish()
                }
            })
        }
    }

    override fun onBackPressed() {

    }
}