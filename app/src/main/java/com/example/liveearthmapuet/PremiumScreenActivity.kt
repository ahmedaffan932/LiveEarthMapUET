package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import kotlin.system.exitProcess
import androidx.core.content.ContextCompat
import com.example.liveearthmapuet.classes.Misc
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.databinding.ActivityPremiumScreenBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.google.common.collect.ImmutableList

class PremiumScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPremiumScreenBinding
    var inAppPosition = 1
    private lateinit var billingClient: BillingClient
    private var productDetailsList = ArrayList<ProductDetails>()
    private var monthly = false
    private var yearly = true
    private var lifetime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPremiumScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        billing()
        setListeners()

        binding.lifetimePrice.text =
            if (Misc.lifetimePrice != "") {
                Misc.lifetimePrice
            } else {
                "$99.00"
            }
        binding.yearlyPrice.text =
            if (Misc.yearlyPrice != "") {
                Misc.yearlyPrice
            } else {
                "$79.99"
            }
        binding.monthlyPrice.text =
            if (Misc.monthlyPrice != "") {
                Misc.monthlyPrice
            } else {
                "$39.99"
            }

    }

    private fun setListeners() {
        object : CountDownTimer(
            2500,
            1000
        ) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                binding.close.visibility = View.VISIBLE
            }
        }.start()

        binding.purchaseButton.setOnClickListener {
            launchPurchaseFlow(productDetailsList, inAppPosition)
        }

        binding.monthly.setOnClickListener {
            inAppPosition = 0
            monthly = true
            yearly = false
            lifetime = false

            binding.monthly.background =
                ContextCompat.getDrawable(this, R.drawable.selected_price_tag)
            binding.yearly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.lifetime.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            launchPurchaseFlow(productDetailsList, inAppPosition)
        }

        binding.yearly.setOnClickListener {
            inAppPosition = 1
            monthly = false
            yearly = true
            lifetime = false

            binding.monthly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.yearly.background =
                ContextCompat.getDrawable(this, R.drawable.selected_price_tag)
            binding.lifetime.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            launchPurchaseFlow(productDetailsList, inAppPosition)
        }

        binding.lifetime.setOnClickListener {
            inAppPosition = 2
            monthly = false
            yearly = false
            lifetime = true

            binding.monthly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.yearly.background =
                ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
            binding.lifetime.background =
                ContextCompat.getDrawable(this, R.drawable.selected_price_tag)

            launchPurchaseFlow(productDetailsList, inAppPosition)

        }


        binding.close.setOnClickListener {
            Ads.loadAndShowInterstitial(
                this,
                Misc.isPremiumScreenInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        if (intent.getStringExtra(Misc.data) != null) {
                            finish()
                        } else {
                            finish()
                            startActivity(
                                Intent(
                                    this@PremiumScreenActivity,
                                    MainActivity::class.java
                                )
                            )
                        }
                    }
                })
        }

        monthly = true
        yearly = false
        lifetime = false

        binding.lifetime.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
        binding.yearly.background =
            ContextCompat.getDrawable(this, R.drawable.unselected_price_tag)
        binding.monthly.background =
            ContextCompat.getDrawable(this, R.drawable.selected_price_tag)


    }

    fun restartApplication(context: Context) {
        val intent = Intent(context.applicationContext, SplashScreenActivity::class.java)
        val pendingIntentId = 1122
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                context,
                pendingIntentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                pendingIntentId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val alarmManager =
            context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC, System.currentTimeMillis() + 100] = pendingIntent
        exitProcess(0)
    }

    override fun onBackPressed() {
        if (intent.getStringExtra(Misc.data) != null) {
            finish()
        }
    }

    private fun billing() {
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                    for (purchase in list) {
                        verifySubPurchase(purchase)
                    }
                }
            }.build()

        establishConnection()
    }


    var connectionFailedCount = 0
    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@NonNull billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                if (Misc.checkInternetConnection(this@PremiumScreenActivity)) {
                    if (connectionFailedCount < 4)
                        establishConnection()

                    connectionFailedCount++
                } else {
                    Toast.makeText(
                        this@PremiumScreenActivity,
                        resources.getString(R.string.please_check_your_internet_connection_and_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    fun showProducts() {
        val productList: ImmutableList<QueryProductDetailsParams.Product> =
            ImmutableList.of(
                //Product 1
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(Misc.monthlySubscriptionId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),  //Product 3
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(Misc.yearlySubscriptionId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(Misc.inAppKey)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),  //Product 2
            )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient.queryProductDetailsAsync(
            params
        ) { billingResult: BillingResult?, prodDetailsList: List<ProductDetails?>? ->
            productDetailsList.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d(Misc.logKey, "posted delayed")
                if (prodDetailsList != null) {
                    for ((i, item) in prodDetailsList.withIndex()) {
                        Log.d(Misc.logKey, "item name ${item?.name} $i")
                        item?.let { productDetailsList.add(it) }
                    }
                }
            }, 2000)
        }
    }

    private fun launchPurchaseFlow(arrProductDetails: ArrayList<ProductDetails>, position: Int) {
        try {
            val productDetails = arrProductDetails[position]
            assert(productDetails.subscriptionOfferDetails != null)
            val productDetailsParamsList = ImmutableList.of(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            val billingResult = billingClient.launchBillingFlow(this, billingFlowParams)
        } catch (e: Exception) {
            Toast.makeText(this, "Please check your internet and try again.", Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }

    }

    private fun verifySubPurchase(purchases: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Toast.makeText(
                    this,
                    getString(R.string.subscription_activated),
                    Toast.LENGTH_SHORT
                ).show()
                Misc.setPurchasedStatus(this, true)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

}