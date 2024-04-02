package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.example.liveearthmapuet.adapters.CountryAdapter
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityAmChatrsBinding
import com.example.liveearthmapuet.interfaces.CountryListInterface
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.WebAppInterface
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

open class AmChartsActivity : BaseActivity(), SearchView.OnQueryTextListener {
    lateinit var binding: ActivityAmChatrsBinding
    lateinit var adapter: CountryAdapter
    var isCountrySelected = false

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface", "ObsoleteSdkInt", "LogNotTimber")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityAmChatrsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackViewWorld.setOnClickListener {
            onBackPressed()
        }

        setEventListener(this, object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                if (isOpen) {
                    Misc.hideView(
                        binding.clCountryInfo,
                        this@AmChartsActivity,
                        binding.clCountryInfo.visibility == View.VISIBLE
                    )
                }
            }
        })

        binding.recyclerViewCountryList.layoutManager = LinearLayoutManager(this)
        World.init(this)
        adapter = CountryAdapter(World.getAllCountries(), this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                binding.webView.loadUrl("javascript:selectCountry('${country.alpha2}');")
                setCountryData(country)
                val inputManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                if (currentFocus != null) {
                    inputManager.hideSoftInputFromWindow(
                        currentFocus!!.windowToken,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
            }
        })
        binding.recyclerViewCountryList.adapter = adapter

        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        try {
            binding.webView.setBackgroundColor(getColor(R.color.background_color))
        }catch (e: Exception){
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        binding.webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE;

        binding.webView.addJavascriptInterface(WebAppInterface(this, object : CountryListInterface {
            @SuppressLint("SetTextI18n")
            override fun onCountryClick(country: Country) {
                setCountryData(country)
            }
        }), "Android")

        binding.webView.loadUrl("file:///android_asset/world/${Misc.wholeWorld}.html")

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
                Log.d("TAG", error.description.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url!!)
                return true
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(Misc.logKey, "Page loaded.")
                binding.llPBViewWorld.visibility = View.GONE
            }
        }

        binding.checkBoxCountryList.setOnCheckedChangeListener { compoundButton, b ->
            if(compoundButton.isChecked){
                binding.simpleSearchView.visibility = View.VISIBLE
                binding.recyclerViewCountryList.visibility = View.VISIBLE
            }else {
                binding.simpleSearchView.visibility = View.GONE
                binding.recyclerViewCountryList.visibility = View.GONE
            }
        }
        binding.simpleSearchView.setOnQueryTextListener(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (isCountrySelected) {
            binding.webView.loadUrl("javascript:goToHome();")
            isCountrySelected = false
            val a: Animation =
                AnimationUtils.loadAnimation(
                    this@AmChartsActivity,
                    R.anim.slide_from_right_to_left
                )
            a.duration = 300
            a.interpolator = OvershootInterpolator()
            binding.clCountryInfo.startAnimation(a)
            Handler().postDelayed({
                binding.clCountryInfo.visibility = View.GONE
            }, 300)
        } else {
            Ads.loadAndShowInterstitial(this, Misc.viewWorldBackInt, object : InterstitialCallBack{
                override fun onDismiss() {
                    finish()
                }
            })
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(
                currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        return true
    }

    @SuppressLint("LogNotTimber")
    override fun onQueryTextChange(p0: String?): Boolean {
        adapter.filter.filter(p0)
        binding.recyclerViewCountryList.adapter = adapter
        Log.d(Misc.logKey, "onQueryTextChange")
        return true
    }

    @SuppressLint("SetTextI18n", "LogNotTimber")
    private fun setCountryData(country: Country) {

        Log.d(Misc.logKey, "Country selected.")
        isCountrySelected = true

        runOnUiThread {
            binding.flagCountryInfo.setImageResource(World.getFlagOf(country.alpha2))
            binding.countryNameInfo.text = country.name
            binding.countryCapitalInfo.text = "Capital: ${country.capital}"
            binding.countryPopulationInfo.text = "Population: ${country.population}"
            binding.countryAreaInfo.text = "Area: ${country.area} km²"
            binding.countryCurrencyInfo.text =
                "Currency: ${country.currency.name} (${country.currency.symbol})"
            binding.clCountryInfo.visibility = View.VISIBLE
        }
        val a: Animation =
            AnimationUtils.loadAnimation(
                this@AmChartsActivity,
                R.anim.slide_from_left_to_right
            )
        a.duration = 300
        a.interpolator = OvershootInterpolator()
        binding.clCountryInfo.startAnimation(a)
    }


}