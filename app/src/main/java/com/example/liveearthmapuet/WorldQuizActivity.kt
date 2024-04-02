package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWorldQuizBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

class WorldQuizActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorldQuizBinding

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
       binding = ActivityWorldQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(this, binding.nativeAd, Misc.worldQuizActivityNative, object: NativeAdCallBack{
            override fun onLoad() {
                binding.nativeAd.visibility = View.VISIBLE
            }
        })

        binding.btnCloseGame.setOnClickListener {
            onBackPressed()
        }

        binding.btnViewWorld.setOnClickListener {
            val intent = Intent(this@WorldQuizActivity, AmChartsActivity::class.java)
            startActivity(intent)
        }

        binding.btnPlayGame.setOnClickListener {
            val intent =
                Intent(this@WorldQuizActivity, WorldQuizScreenOneActivity::class.java)
            intent.putExtra(Misc.data, "sda")
            startActivity(intent)
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Ads.loadAndShowInterstitial(this, Misc.worldQuizOnBackInt, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}