package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivitySoundMeterBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

class SoundMeterActivity : AppCompatActivity() {
    lateinit var binding: ActivitySoundMeterBinding
    private val mRecorder = MediaRecorder()
    private val handler: Handler = Handler()
    private val arr = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySoundMeterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Ads.showNativeAd(
            this@SoundMeterActivity,
            binding.nativeAd,
            Misc.soundMeterNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        binding.btnBackSoundMeter.setOnClickListener {
            onBackPressed()
        }

        binding.btnInfo.setOnClickListener {
//            Ads.showInterstitial(this, Misc.isBtnClickIntEnable, object : InterstitialCallBack {
//                override fun onDismiss() {
            AlertDialog.Builder(this@SoundMeterActivity)
                .setTitle("Example:")
                .setMessage(
                    "20 dB-Whisper\n" +
                            "40 dB-Quite library\n" +
                            "60 dB-Conversation\n" +
                            "80 dB-Loud Music\n" +
                            "100 dB-Motorcycle\n" +
                            "120 dB-Threshold of pain"
                )
                .setPositiveButton("Ok")
                { dialog, which ->
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
//                }
//            })
        }

        binding.btnReset.setOnClickListener {
//            Ads.showInterstitial(this, Misc.isBtnClickIntEnable, object : InterstitialCallBack {
//                override fun onDismiss() {
            handler.removeCallbacks(runSoundMeter)
            binding.speedAnalog.speedTo(0.toFloat(), 100)
            binding.textAvg.text = "0"
            binding.textMax.text = "0"
            binding.textMin.text = "0"
            arr.clear()

            Handler().postDelayed({
                handler.post(runSoundMeter)
            }, 1000)
//                }
//            })
        }


    }


    fun getAmplitude(): Int {
        return mRecorder.maxAmplitude
    }

    private fun startRecording() {
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder.setOutputFile("${externalCacheDir?.absolutePath}/${System.currentTimeMillis()}.3gp")
        mRecorder.prepare()
        mRecorder.start()

    }

    private val runSoundMeter: Runnable by lazy {
        return@lazy object : Runnable {
            @SuppressLint("LogNotTimber")
            override fun run() {
                val value = (20 * kotlin.math.log10(getAmplitude().toDouble())).toInt()
                Log.d("logKey", value.toString())
                if (value > 0) {
                    binding.speedAnalog.speedTo(value.toFloat(), 100)
                    arr.add(value)
                    binding.textAvg.text = arr.average().toInt().toString()
                    binding.textMax.text = arr.maxOrNull().toString()
                    binding.textMin.text = arr.minOrNull().toString()
                }

                handler.postDelayed(this, 10)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startRecording()
        handler.post(runSoundMeter)

    }

    override fun onPause() {
        super.onPause()
        mRecorder.stop()
        handler.removeCallbacks(runSoundMeter)
    }

}