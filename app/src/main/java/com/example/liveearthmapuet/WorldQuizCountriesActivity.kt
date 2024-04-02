package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWorldQuizCountriesBinding
import com.example.liveearthmapuet.interfaces.*
import kotlinx.coroutines.tasks.await

class WorldQuizCountriesActivity : AppCompatActivity() {
    lateinit var binding:ActivityWorldQuizCountriesBinding
    var currentLevel = 0
    var levels = 0
    var selectedCountry: Country? = null
    var isCountrySelected = false
    private val arrCountries = ArrayList<Country>()
    var isCompleted = false
    var numberOfCorrectAnswers = 0

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled", "LogNotTimber", "SetTextI18n", "ObsoleteSdkInt")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityWorldQuizCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        binding.webView.setBackgroundColor(getColor(R.color.background_color))
        FirebaseApp.initializeApp(this)

        binding.btnConfirm.setOnClickListener {
            if (!isCompleted) {
                getResult()

                setCountryData(arrCountries[currentLevel])
                binding.btnConfirmText.text = "Next"
                binding.webView.loadUrl("javascript:selectCountry('${arrCountries[currentLevel].alpha2}');")
                binding.blockView.visibility = View.VISIBLE
                isCompleted = true
            } else {
                binding.webView.loadUrl("javascript:zoomOutByCountryId('${arrCountries[currentLevel].alpha2}');")
                currentLevel++
                getCurrentLevel()
                binding.btnConfirmText.text = "Confirm"
                binding.btnConfirm.visibility = View.INVISIBLE
                binding.textResult.visibility = View.INVISIBLE
                binding.textCorrectCountry.visibility = View.INVISIBLE
                hideCountryInfo()
                binding.blockView.visibility = View.GONE
                isCompleted = false
            }
            isCountrySelected = false
        }

        binding.blockView.setOnClickListener {
            binding.webView.loadUrl("javascript:zoomOutByCountryId('${arrCountries[currentLevel].alpha2}');")
            currentLevel++
            getCurrentLevel()
            binding.btnConfirmText.text = getString(R.string.confirm)
            binding.btnConfirm.visibility = View.INVISIBLE
            binding.textResult.visibility = View.INVISIBLE
            binding.textCorrectCountry.visibility = View.INVISIBLE
            hideCountryInfo()
            binding.blockView.visibility = View.GONE
            isCompleted = false
        }

        initGame()
        getCurrentLevel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;

        binding.webView.addJavascriptInterface(WebAppInterface(this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                runOnUiThread {
                    selectCountry(country)
                }
            }
        }), "Android")


        binding.webView.loadUrl("file:///android_asset/world/${Misc.gameContinent}.html")

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", error.description.toString())
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.llPBGame.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCountryData(country: Country) {
        runOnUiThread {
            binding.flagCountryInfo.setImageResource(World.getFlagOf(country.alpha2))
            binding.countryNameInfo.text = country.name
            binding.countryCapitalInfo.text = "Capital: ${country.capital}"
            binding.countryPopulationInfo.text = "Population: ${country.population}"
            binding.countryAreaInfo.text = "Area: ${country.area} km²"
            binding.countryCurrencyInfo.text =
                "Currency: ${country.currency.name} (${country.currency.symbol})"
            binding.clCountryInfo.visibility = View.VISIBLE
            val a: Animation =
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_from_left_to_right
                )
            a.interpolator = OvershootInterpolator()
            a.duration = 300
            binding.clCountryInfo.startAnimation(a)
        }

    }

    fun selectCountry(country: Country) {
        selectedCountry = country
        isCountrySelected = true
        binding.btnConfirm.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    fun getCurrentLevel() {
        try {
            binding.textLevel.text = "Level: ${currentLevel + 1}/${levels}"
            when (Misc.gameMode) {
                Misc.countries -> {
                    binding.findCountry.text = "Find: ${arrCountries[currentLevel].name}"
                }
                Misc.capitals -> {
                    binding.findCountry.text = "${arrCountries[currentLevel].capital} is capital of ..."
                }
                Misc.currencies -> {
                    binding.findCountry.text =
                        "${arrCountries[currentLevel].currency.name} (${arrCountries[currentLevel].currency.symbol}) is currency of ..."
                }
                Misc.flags -> {
                    binding.flagCountryGame.visibility = View.VISIBLE
                    binding.flagCountryGame.setImageResource(arrCountries[currentLevel].flagResource)
                    binding.findCountry.text = "This is Flag of ..."
                }
            }
        } catch (e: Exception) {
            Ads.loadAndShowInterstitial(
                this,
                Misc.quizCompleteInt,
                object : InterstitialCallBack {
                    override fun onDismiss() {
                        finish()
                        val intent =
                            Intent(
                                this@WorldQuizCountriesActivity,
                                WorldQuizCompletedActivity::class.java
                            )
                        intent.putExtra(Misc.levels, levels)
                        intent.putExtra(Misc.data, numberOfCorrectAnswers)
                        startActivity(intent)
                    }
                })
        }
    }

    @SuppressLint("SetTextI18n", "ServiceCast")
    fun getResult() {
        if (selectedCountry == arrCountries[currentLevel]) {
            binding.textResult.text = "Correct !"
            binding.textResult.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.textResult.setTextColor(getColor(R.color.green))
            }
            numberOfCorrectAnswers++
            binding.animResult.visibility = View.VISIBLE
            binding.animResult.setAnimation(R.raw.ok_anim)
            binding.animResult.playAnimation()
            Handler().postDelayed({
                binding.animResult.visibility = View.GONE
            }, 1500)
        } else {
            binding.textResult.text = "Wrong !"
            binding.textResult.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.textResult.setTextColor(getColor(R.color.red))
            }
            binding.animResult.visibility = View.VISIBLE
            binding.textCorrectCountry.text = "You selected ${selectedCountry?.name}"
            binding.textCorrectCountry.visibility = View.VISIBLE
            val v: Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Vibrate for 500 milliseconds
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            binding.animResult.setAnimation(R.raw.not_ok_anim)
            binding.animResult.playAnimation()
            Handler().postDelayed({
                binding.animResult.visibility = View.GONE
            }, 1500)

        }
        if (Misc.gameMode == Misc.capitals) {
            binding.findCountry.text =
                "${arrCountries[currentLevel].capital} is capital of ${arrCountries[currentLevel].name}"
        } else if (Misc.gameMode == Misc.flags) {
            binding.findCountry.text =
                "his is flag of ${arrCountries[currentLevel].name}"
        } else if (Misc.gameMode == Misc.currencies) {
            binding.findCountry.text =
                "${arrCountries[currentLevel].currency.name} (${arrCountries[currentLevel].currency.symbol}) is currency of ${arrCountries[currentLevel].name}"
        }
    }

    fun hideCountryInfo() {
        val a: Animation =
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_from_right_to_left
            )
        a.duration = 300
        binding.clCountryInfo.startAnimation(a)
        Handler().postDelayed({
            binding.clCountryInfo.visibility = View.INVISIBLE
        }, 300)
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    fun initGame() {
        try {
            arrCountries.clear()
            levels = intent.getIntExtra(Misc.data, 0)

            World.init(this)
            var arr = if (Misc.gameContinent != Misc.wholeWorld) {
                World.getAllCountries()
                    .filter { con -> con.continent.contains(Misc.gameContinent, ignoreCase = true) }
            } else {
                World.getAllCountries()
            }
            Log.d(Misc.logKey, arr.size.toString())
            if (levels < 120)
                arr = arr.sortedByDescending { it.area }
            for (a in arr) {
                Log.d(Misc.logKey, a.area.toString())
            }
            var i = 0
            while (arrCountries.size < levels) {
                val tempCountry = arr[i]
                if (tempCountry.alpha2 != "xx") {
                    arrCountries.add(tempCountry)
                    i++
                } else {
                    i++
                }
            }
            Log.d(Misc.logKey, arrCountries.toString())
            arrCountries.shuffle()

            binding.textLevel.text = "Level: ${currentLevel + 1}/${levels}"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("LogNotTimber")
    private suspend fun getMapDotHtml(mapName: String): String? {
        return try {
            val sharedPref = getSharedPreferences("SavedLanguages", Context.MODE_PRIVATE)

            var valueString = sharedPref?.getString(mapName, null)

            if (valueString != null) {
                Log.d("Getting Language", "Getting value from SP")
                return valueString
            }

            val storage: FirebaseStorage =
                FirebaseStorage.getInstance()

            val islandRef = storage.reference.child("/$mapName.html")
            val fiftyKBs: Long = 1024 * 50
            Log.d("Getting Language", "Getting value from FB")
            valueString = String(islandRef.getBytes(fiftyKBs).await())
            sharedPref?.edit()?.putString(mapName, valueString)?.apply()

            binding.webView.loadData(valueString, "text/html", "UTF-8")
            Log.d(Misc.logKey, valueString)
            valueString
        } catch (e: Exception) {
            e.printStackTrace()
            "Unable to fetch value, please check your internet."
        }
    }

    override fun onBackPressed() {
        if (isCountrySelected) {
            binding.btnConfirm.visibility = View.INVISIBLE
            binding.webView.loadUrl("javascript:zoomOutByCountryId('${arrCountries[currentLevel].alpha2}');")
            isCountrySelected = false
        } else if(isCompleted){
            binding.webView.loadUrl("javascript:zoomOutByCountryId('${arrCountries[currentLevel].alpha2}');")
            currentLevel++
            getCurrentLevel()
            binding.btnConfirmText.text = "Confirm"
            binding.btnConfirm.visibility = View.INVISIBLE
            binding.textResult.visibility = View.INVISIBLE
            binding.textCorrectCountry.visibility = View.INVISIBLE
            hideCountryInfo()
            binding.blockView.visibility = View.GONE
            isCompleted = false
            isCountrySelected = false
        }else{
//            Misc.onBackPress(this, Misc.isPlayGameBackIntEnabled, object : OnBackPressCallBack {
//                override fun onBackPress() {
//                    finish()
//                }
//            })
            super.onBackPressed()
        }
    }

}