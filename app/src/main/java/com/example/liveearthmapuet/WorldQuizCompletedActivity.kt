package com.example.liveearthmapuet

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWorldQuizCompletedBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack


class WorldQuizCompletedActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorldQuizCompletedBinding
    @SuppressLint("SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityWorldQuizCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(
            this@WorldQuizCompletedActivity,
            binding.nativeAd,
            Misc.quizCompleteNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        binding.textCorrectAnswers.text = "Correct Answer: ${intent.getIntExtra(Misc.data, 0)}"
        binding.textTotalLevels.text = "Total levels: ${intent.getIntExtra(Misc.levels, 0)}"
        binding.textFalseAnswer.text = "False Answers: ${
            (intent.getIntExtra(Misc.levels, 0) - intent.getIntExtra(
                Misc.data,
                0
            ))
        }"

        binding.btnBackWorldQuizCompleted.setOnClickListener {
            onBackPressed()
        }

        binding.btnback.setOnClickListener {
            onBackPressed()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Ads.loadAndShowInterstitial(this, Misc.quizCompleteBackInt, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}