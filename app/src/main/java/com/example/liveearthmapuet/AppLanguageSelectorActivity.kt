package com.example.liveearthmapuet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.AdMobNative
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityAppLanguageSelectorBinding
import com.example.liveearthmapuet.interfaces.LoadInterstitialCallBack


class AppLanguageSelectorActivity : AppCompatActivity() {
    lateinit var binding: ActivityAppLanguageSelectorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        binding = ActivityAppLanguageSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (Misc.getAppLanguage(this)) {
            "ar" -> {
                binding.rbArabic.isChecked = true
            }

            "fr" -> {
                binding.rbFrench.isChecked = true
            }

            "de" -> {
                binding.rbGerman.isChecked = true
            }

            "es" -> {
                binding.rbSpanish.isChecked = true
            }

            "ko" -> {
                binding.rbKorean.isChecked = true
            }

            "hi" -> {
                binding.rbHindi.isChecked = true
            }

            "tr" -> {
                binding.rbTurkish.isChecked = true
            }

            else -> {
                binding.rbEnglish.isChecked = true
            }
        }

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            setAppLanguage(this, findViewById<View>(checkedId).tag.toString())
        }

        binding.btnSave.setOnClickListener {
            startActivity(Intent(this, OnBoardingActivity::class.java))
        }

        AdMobNative.loadNativeOne(this, object : LoadInterstitialCallBack {
            override fun onLoaded() {
                binding.nativeAdFrameLayout.removeAllViews()
                Ads.showNativeAd(
                    this@AppLanguageSelectorActivity,
                    binding.nativeAdFrameLayout,
                    Misc.appLanguageSelectorNative
                )
            }

            override fun onFailed() {
                binding.nativeAdFrameLayout.visibility = View.GONE
            }
        })
    }
}