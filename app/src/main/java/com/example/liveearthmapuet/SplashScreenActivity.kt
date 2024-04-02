package com.example.liveearthmapuet

import android.util.Log
import android.os.Bundle
import android.os.Handler
import android.content.Intent
import android.view.WindowManager
import com.blongho.country_data.World
import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.View
import com.google.firebase.FirebaseApp
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.anjlab.android.iab.v3.PurchaseInfo
import com.anjlab.android.iab.v3.BillingProcessor
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivitySplashScreenBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack

@SuppressLint("CustomSplashScreen", "LogNotTimber")
class SplashScreenActivity : BaseActivity() {
    private var handler = Handler()
    private var loadingPercentage = 0
    private lateinit var bp: BillingProcessor
    private var isAdRequestSent: Boolean = false
    lateinit var binding: ActivitySplashScreenBinding

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billing()

        loadAds()

        World.init(this)
        FirebaseApp.initializeApp(applicationContext)
        handler.post(runLoadingPercentage)


        binding.btnStart.setOnClickListener {
            start()
        }

        binding.btnStartBottom.setOnClickListener {
            start()
        }

        Handler().postDelayed({
            Log.d(Misc.logKey, "Asd ${Misc.splashNative}")
            if (Misc.isSplashLargeNative) {
                Misc.zoomInView(binding.btnStart, this@SplashScreenActivity, 300)
            } else {
                binding.btnStartBottom.visibility = View.VISIBLE
                Misc.zoomInView(binding.btnStartBottom, this@SplashScreenActivity, 300)
            }

            Misc.zoomOutView(
                binding.animLoading,
                this@SplashScreenActivity,
                300
            )
        }, 8000)

    }


    private val runLoadingPercentage: Runnable by lazy {
        return@lazy object : Runnable {
            @SuppressLint("LogNotTimber", "SetTextI18n")
            override fun run() {
                if (loadingPercentage < 100)
                    loadingPercentage += 1
                binding.tvLoadingPercentage.text = "$loadingPercentage%"
                handler.postDelayed(this, 80)
            }
        }
    }

    fun start() {
        if (Misc.getPurchasedStatus(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            Ads.loadAndShowInterstitial(this, Misc.isSplashInt, object : InterstitialCallBack {
                override fun onDismiss() {
                    if (Misc.isPremiumScreenEnabled) {
                        startActivity(
                            Intent(
                                this@SplashScreenActivity,
                                PremiumScreenActivity::class.java
                            )
                        )
                    } else {
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    }
                }
            })
        }
    }

    @SuppressLint("LogNotTimber")
    private fun billing() {
        bp = BillingProcessor(
            this,
            getString(R.string.billing_id),
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(
                    productId: String,
                    details: PurchaseInfo?
                ) {
                    Misc.setPurchasedStatus(this@SplashScreenActivity, true)
                }

                override fun onPurchaseHistoryRestored() {

                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {
                    Log.e("TAG", "onBillingError: ${error?.message}")
                }

                override fun onBillingInitialized() {
                    Log.e("TAG", "onBillingInitialized: ")
                }

            })

        bp.initialize()
    }

    fun loadAds() {

        object : CountDownTimer(1500, 2000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
            }
        }.start()


        Handler().postDelayed({
            if (Misc.splashNative.contains("off")) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(Misc.data, Misc.data)
                startActivity(intent)
            }
        }, 6000)
    }

}