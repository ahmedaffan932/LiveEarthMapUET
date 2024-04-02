package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityPreSplashScreenBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class PreSplashScreenActivity : AppCompatActivity() {
    lateinit var binding: ActivityPreSplashScreenBinding
    private lateinit var consentInformation: ConsentInformation

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        binding = ActivityPreSplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getRemoteConfigValues()

        val debugSettings = ConsentDebugSettings.Builder(this)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId("324810A5D07FF47ED2E42E54FD1A1556")
            .build()

        val params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    this
                ) { loadAndShowError ->

                    if (loadAndShowError != null) {
                        // Consent gathering failed.
                        Log.d(Misc.logKey, String.format("%s: %s", loadAndShowError.message))
                        showStartButton()
                        Misc.canRequestAds = false

                    }

                    if (consentInformation.canRequestAds()) {
                        MobileAds.initialize(this) {}
                        showStartButton()
                        Misc.canRequestAds = true
                    } else {
                        showStartButton()
                        Misc.canRequestAds = false
                    }
                }
            },
            { requestConsentError ->
                Log.w(
                    Misc.logKey, String.format(
                        "%s: %s",
                        requestConsentError.errorCode,
                        requestConsentError.message
                    )
                )
                showStartButton()
                Misc.canRequestAds = false

            }
        )
    }

    private fun showStartButton() {
        binding.animContinue.setAnimation("anim_continue_btn.json")
        binding.animContinue.loop(true)
        binding.animContinue.playAnimation()
        binding.animContinue.setOnClickListener {
            startActivity(Intent(this, LoadingAdActivity::class.java))
        }
    }

    private fun getRemoteConfigValues(): Boolean {
        return try {
            val mFRC = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
            }
            mFRC.setConfigSettingsAsync(configSettings)
            mFRC.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && !BuildConfig.DEBUG) {
                        mFRC.activate()
                        Misc.dashboardInt = mFRC.getString("dashboardInt")
                        Misc.onboardingNative = mFRC.getString("onboardingNative")
                        Misc.appLanguageSelectorNative = mFRC.getString("appLanguageSelectorNative")
                        Misc.settingNative = mFRC.getString("settingNative")
                        Misc.monthlySubscriptionId = mFRC.getString("monthlySubscriptionId")
                        Misc.yearlySubscriptionId = mFRC.getString("yearlySubscriptionId")
                        Misc.lifetimePrice = mFRC.getString("lifetimePrice")
                        Misc.yearlyPrice = mFRC.getString("yearlyPrice")
                        Misc.monthlyPrice = mFRC.getString("monthlyPrice")
                        Misc.isPremiumScreenInt = mFRC.getString("isPremiumScreenInt")
                        Misc.lemInt = mFRC.getString("lemInt")
                        Misc.liveCamInt = mFRC.getString("liveCamInt")
                        Misc.gpsCamInt = mFRC.getString("gpsCamInt")
                        Misc.isQuitInt = mFRC.getString("isQuitInt")
                        Misc.compassInt = mFRC.getString("compassInt")
                        Misc.noteCamInt = mFRC.getString("noteCamInt")
                        Misc.altitudeInt = mFRC.getString("altitudeInt")
                        Misc.isSplashInt = mFRC.getString("isSplashInt")
                        Misc.worldQuizInt = mFRC.getString("worldQuizInt")
                        Misc.splashNative = mFRC.getString("splashNative")
                        Misc.startGameInt = mFRC.getString("startGameInt")
                        Misc.generateQRInt = mFRC.getString("generateQRInt")
                        Misc.soundMeterInt = mFRC.getString("soundMeterInt")
                        Misc.settingBackInt = mFRC.getString("settingBackInt")
                        Misc.compassBackInt = mFRC.getString("compassBackInt")
                        Misc.speedometerInt = mFRC.getString("speedometerInt")
                        Misc.createQRNative = mFRC.getString("createQRNative")
                        Misc.dashboardNative = mFRC.getString("dashboardNative")
                        Misc.altitudeBackInt = mFRC.getString("altitudeBackInt")
                        Misc.quizCompleteInt = mFRC.getString("quizCompleteInt")
                        Misc.quizCountriesInt = mFRC.getString("quizCountriesInt")
                        Misc.viewWorldBackInt = mFRC.getString("viewWorldBackInt")
                        Misc.noteCamOnBackInt = mFRC.getString("noteCamOnBackInt")
                        Misc.soundMeterNative = mFRC.getString("soundMeterNative")
                        Misc.speedoMeterNative = mFRC.getString("speedoMeterNative")
                        Misc.soundMeterBackInt = mFRC.getString("soundMeterBackInt")
                        Misc.quizSelectModeInt = mFRC.getString("quizSelectModeInt")
                        Misc.quizCompleteNative = mFRC.getString("quizCompleteNative")
                        Misc.worldQuizOnBackInt = mFRC.getString("worldQuizOnBackInt")
                        Misc.continentSelectInt = mFRC.getString("continentSelectInt")
                        Misc.liveEarthOnBackInt = mFRC.getString("liveEarthOnBackInt")
                        Misc.generateQrOnBackInt = mFRC.getString("generateQrOnBackInt")
                        Misc.quizCompleteBackInt = mFRC.getString("quizCompleteBackInt")
                        Misc.quizScreenOneNative = mFRC.getString("quizScreenOneNative")
                        Misc.quizScreenOneBackInt = mFRC.getString("quizScreenOneBackInt")
                        Misc.quizSelectModeNative = mFRC.getString("quizSelectModeNative")
                        Misc.continentSelectNative = mFRC.getString("continentSelectNative")
                        Misc.continentSelectBackInt = mFRC.getString("continentSelectBackInt")
                        Misc.worldQuizActivityNative = mFRC.getString("worldQuizActivityNative")

                        mFRC.reset()
                    }
                }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}