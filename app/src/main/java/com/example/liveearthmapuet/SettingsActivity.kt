package com.example.liveearthmapuet

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.EmailUsDialogBox
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.classes.RateUsDialog
import com.example.liveearthmapuet.databinding.ActivitySettingsBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack

class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Ads.showNativeAd(this, binding.adFrameLayoutNative, Misc.settingNative, null)

        binding.tvUpgradeToPremium.setOnClickListener {
//            Misc.startActivity(this, Misc.isProScreenIntEnabled, object : StartActivityCallBack{
//                override fun onStart() {
            val intent = Intent(this@SettingsActivity, PremiumScreenActivity::class.java)
            intent.putExtra(Misc.data, Misc.data)
            startActivity(intent)
//                }
//            })
        }

        binding.llShareApp.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Have a look to this interesting application:- \n \n${Misc.appUrl}"
            )
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        binding.llPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://sites.google.com/view/npc-studios-lem/home")
            )
            startActivity(intent)
        }


        binding.llMoreApps.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=NPC+Studios")
            )
            startActivity(intent)
        }


        binding.btnBackSettings.setOnClickListener {
            onBackPressed()
        }

        binding.llFeedback.setOnClickListener {
            val objEmailUsDialog = EmailUsDialogBox(this)
            objEmailUsDialog.show()
            val window: Window = objEmailUsDialog.window!!
            window.setLayout(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(R.color.nothing)
            objEmailUsDialog.findViewById<ConstraintLayout>(R.id.btnPublishFeedback)
            objEmailUsDialog.findViewById<ConstraintLayout>(R.id.btnPublishFeedback)
                .setOnClickListener {
                    val sub = objEmailUsDialog.findViewById<TextView>(R.id.etTopic).text.toString()
                    val i = Intent(Intent.ACTION_SEND)
                    i.type = "message/rfc822"
                    i.putExtra(Intent.EXTRA_EMAIL, arrayOf("xtreamappssolutions@gmail.com"))
                    i.putExtra(
                        Intent.EXTRA_TEXT,
                        objEmailUsDialog.findViewById<EditText>(R.id.etFeedbackBody).text
                    )
                    i.putExtra(Intent.EXTRA_SUBJECT, sub)
                    i.setPackage("com.google.android.gm")

                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."))
                        objEmailUsDialog.dismiss()
                    } catch (ex: ActivityNotFoundException) {
                        Toast.makeText(
                            this,
                            getString(R.string.some_error_occurred_in_sending_email),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            objEmailUsDialog.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                objEmailUsDialog.dismiss()
            }
        }


        binding.llRateUs.setOnClickListener {
            val objRateUsDialog = RateUsDialog(this)
            objRateUsDialog.show()
            val window: Window = objRateUsDialog.window!!
            window.setLayout(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(R.color.nothing)

            objRateUsDialog.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                objRateUsDialog.dismiss()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateA).setOnClickListener {
                objRateUsDialog.dismiss()
                Toast.makeText(this, getString(R.string.thanks), Toast.LENGTH_SHORT).show()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateB).setOnClickListener {
                objRateUsDialog.dismiss()
                Toast.makeText(this, getString(R.string.thanks), Toast.LENGTH_SHORT).show()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateC).setOnClickListener {
                objRateUsDialog.dismiss()
                Toast.makeText(this, getString(R.string.thanks), Toast.LENGTH_SHORT).show()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateD).setOnClickListener {
                rateUs()
            }
            objRateUsDialog.findViewById<ImageView>(R.id.btnRateE).setOnClickListener {
                rateUs()
            }
            objRateUsDialog.findViewById<ConstraintLayout>(R.id.btnRateUs).setOnClickListener {
                rateUs()
            }
        }
    }

    private fun rateUs() {
        val uri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
                )
            )
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Ads.loadAndShowInterstitial(this, Misc.settingBackInt, object : InterstitialCallBack {
            override fun onDismiss() {
                finish()
            }
        })
    }
}