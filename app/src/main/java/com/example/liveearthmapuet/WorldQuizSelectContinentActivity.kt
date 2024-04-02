package com.example.liveearthmapuet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWorldQuizSelectContinetBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

class WorldQuizSelectContinentActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorldQuizSelectContinetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityWorldQuizSelectContinetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(
            this@WorldQuizSelectContinentActivity,
            binding.nativeAd,
            Misc.continentSelectNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        binding.btnBackWorldQuizContinent.setOnClickListener {
            onBackPressed()
        }

        val intent =
            Intent(this@WorldQuizSelectContinentActivity, WordQuizModeSelectorActivity::class.java)
        binding.clWholeWorld.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.continentSelectInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.wholeWorld
                        startActivity(intent)
                    }
                })
        }

        binding.clAsia.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.continentSelectInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.asia
                        startActivity(intent)
                    }
                })
        }

        binding.clEurope.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.continentSelectInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.europe
                        startActivity(intent)
                    }
                })
        }

        binding.clAfrica.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.continentSelectInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.africa
                        startActivity(intent)
                    }
                })
        }

        binding.clAmerica.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.continentSelectInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.america
                        startActivity(intent)
                    }
                })
        }
        binding.clOceania.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.continentSelectInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        Misc.gameContinent = Misc.oceania
                        startActivity(intent)
                    }
                })
        }
    }

    override fun onBackPressed() {
        Ads.loadAndShowInterstitial(this, Misc.quizSelectModeInt, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}