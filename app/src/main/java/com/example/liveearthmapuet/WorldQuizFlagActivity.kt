package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.blongho.country_data.Country
import com.blongho.country_data.World
import com.google.firebase.FirebaseApp
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityWorldQuizFlagBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack

class WorldQuizFlagActivity : AppCompatActivity() {
    var currentLevel = 0
    var levels = 0
    var isCountrySelected = false
    private val arrCountries = ArrayList<Country>()
    var isCompleted = false
    var numberOfCorrectAnswers = 0
    lateinit var binding: ActivityWorldQuizFlagBinding

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled", "LogNotTimber", "SetTextI18n", "ObsoleteSdkInt")
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        binding = ActivityWorldQuizFlagBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        initGame()
        getCurrentLevel()

    }


    @SuppressLint("SetTextI18n")
    fun getCurrentLevel() {
        enableAnswersClickListeners(true)
        binding.btnNext.visibility = View.INVISIBLE
        try {
            binding.textLevel.text = "Level: ${currentLevel + 1}/${levels}"
            binding.flagCountryGame.visibility = View.VISIBLE
            binding.flagCountryGame.setImageResource(arrCountries[currentLevel].flagResource)
            binding.findCountry.text = "This is Flag of ..."
            val arr = ArrayList<Country>()
            for (country in arrCountries) {
                arr.add(country)
            }
            arr.remove(arrCountries[currentLevel])
            arr.shuffle()
            binding.flagAnswerOne.text = arr[0].name
            binding.flagAnswerTwo.text = arr[1].name
            binding.flagAnswerThree.text = arr[2].name
            binding.flagAnswerFour.text = arr[3].name

            when ((1..4).random()) {
                1 -> {
                    binding.flagAnswerOne.text = arrCountries[currentLevel].name
                }
                2 -> {
                    binding.flagAnswerTwo.text = arrCountries[currentLevel].name
                }
                3 -> {
                    binding.flagAnswerThree.text = arrCountries[currentLevel].name
                }
                else -> {
                    binding.flagAnswerFour.text = arrCountries[currentLevel].name
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
                                this@WorldQuizFlagActivity,
                                WorldQuizCompletedActivity::class.java
                            )
                        intent.putExtra(Misc.levels, levels)
                        intent.putExtra(Misc.data, numberOfCorrectAnswers)
                        startActivity(intent)
                    }
                }
            )
        }
    }

    @SuppressLint("SetTextI18n", "ServiceCast")
    fun getResult(selectedCountry: String) {
        enableAnswersClickListeners(false)
        if (selectedCountry == arrCountries[currentLevel].name) {
            numberOfCorrectAnswers++
            binding.animResult.visibility = View.VISIBLE
            binding.animResult.setAnimation(R.raw.ok_anim)
            binding.animResult.playAnimation()
            Handler().postDelayed({
                binding.animResult.visibility = View.GONE
            }, 1500)
        } else {
            binding.animResult.visibility = View.VISIBLE
            val v: Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                v.vibrate(500)
            }
            binding.animResult.setAnimation(R.raw.not_ok_anim)
            binding.animResult.playAnimation()
            Handler().postDelayed({
                binding.animResult.visibility = View.GONE
            }, 1500)

        }
        binding.findCountry.text =
            "This is flag of ${arrCountries[currentLevel].name}"

        binding.btnNext.visibility = View.VISIBLE
        binding.btnNext.setOnClickListener {
            currentLevel++
            getCurrentLevel()
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
//            for (a in arr) {
//                Log.d(Misc.logKey, a.area.toString())
//            }
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
//    fun checkSelection(selectedCountry: String) {
//        if (selectedCountry == arrCountries[currentLevel].name) {
//            Log.d(Misc.logKey, "Correct")
//            currentLevel++
//            getCurrentLevel()
//        } else {
//            Log.e(Misc.logKey, "False")
//            currentLevel++
//            getCurrentLevel()
//        }
////        getResult()
//
//    }

    private fun enableAnswersClickListeners(isEnable: Boolean){
        if(isEnable) {
            binding.flagAnswerOne.setOnClickListener {
                getResult((it as TextView).text.toString())
            }

            binding.flagAnswerTwo.setOnClickListener {
                getResult((it as TextView).text.toString())
            }

            binding.flagAnswerThree.setOnClickListener {
                getResult((it as TextView).text.toString())
            }

            binding.flagAnswerFour.setOnClickListener {
                getResult((it as TextView).text.toString())
            }
        }else{
            binding.flagAnswerOne.setOnClickListener {
            }

            binding.flagAnswerTwo.setOnClickListener {
            }

            binding.flagAnswerThree.setOnClickListener {
            }

            binding.flagAnswerFour.setOnClickListener {
            }
        }
    }

}