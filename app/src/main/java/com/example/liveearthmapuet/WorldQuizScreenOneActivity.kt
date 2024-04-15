package com.example.liveearthmapuet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWorldQuizScreenOneBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

class WorldQuizScreenOneActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorldQuizScreenOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding= ActivityWorldQuizScreenOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(
            this@WorldQuizScreenOneActivity,
            binding.nativeAd,
            Misc.quizScreenOneNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        binding.btnBackWorldQuizScreenOne.setOnClickListener {
            onBackPressed()
        }
        val intent = Intent(
            this@WorldQuizScreenOneActivity,
            WorldQuizSelectContinentActivity::class.java
        )

        binding.clCountries.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.quizCountriesInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.countries
                        startActivity(intent)
                    }
                })
        }

        binding.clFlags.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.quizCountriesInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.flags
                        startActivity(intent)
                    }
                })
        }

        binding.clCapitals.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.quizCountriesInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.capitals
                        startActivity(intent)
                    }
                })
        }

        binding.clCurrencies.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.quizCountriesInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameMode = Misc.currencies
                        startActivity(intent)
                    }
                })
        }
    }

}