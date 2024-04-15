package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.blongho.country_data.World
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWordQuizModeSelectorBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

class WordQuizModeSelectorActivity : AppCompatActivity() {
    var easyLevel = 10
    var mediumLevel = 25
    var hardLevel = 0
    lateinit var binding: ActivityWordQuizModeSelectorBinding

    @SuppressLint("LogNotTimber", "SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityWordQuizModeSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(
            this@WordQuizModeSelectorActivity,
            binding.nativeAd,
            Misc.quizSelectModeNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        World.init(this)
        val arr = World.getAllCountries()
        if (Misc.gameContinent != Misc.wholeWorld) {
            binding.clPro.visibility = View.GONE
            val a =
                arr.filter { con -> con.continent.contains(Misc.gameContinent, ignoreCase = true) }
            hardLevel = a.size
            binding.textEasy.text = "0 / $easyLevel"
            binding.textMedium.text = "0 / $mediumLevel"
            binding.textHard.text = "0 / $hardLevel"

        } else {
            easyLevel = 30
            mediumLevel = 70
            hardLevel = 150
        }

        if (Misc.gameContinent == Misc.oceania) {
            binding.clHard.visibility = View.GONE
        }

        binding.clEasy.setOnClickListener {
            startGame(easyLevel)
        }
        binding.clMedium.setOnClickListener {
            startGame(mediumLevel)
        }
        binding.clHard.setOnClickListener {
            startGame(hardLevel)
        }
        binding.clPro.setOnClickListener {
            startGame(236)
        }

        binding.btnBackWorldQizModeSelector.setOnClickListener {
            onBackPressed()
        }

    }

    private fun startGame(levels: Int) {
        if (Misc.gameMode == Misc.flags) {
            val intent = Intent(this, WorldQuizFlagActivity::class.java)
            intent.putExtra(Misc.data, levels)
            Ads.loadAndShowInterstitial(this, Misc.startGameInt, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(intent)
                }
            })
        } else {
            val intent = Intent(this, WorldQuizCountriesActivity::class.java)
            intent.putExtra(Misc.data, levels)
            Ads.loadAndShowInterstitial(this, Misc.startGameInt, object : InterstitialCallBack {
                override fun onDismiss() {
                    startActivity(intent)
                }
            })
        }
    }

}