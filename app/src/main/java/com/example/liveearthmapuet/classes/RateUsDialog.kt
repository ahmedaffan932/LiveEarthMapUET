package com.example.liveearthmapuet.classes

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.liveearthmapuet.R

class RateUsDialog
    (var c: Activity) : Dialog(c), View.OnClickListener {
    var d: Dialog? = null
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.rate_us_dialog_box)
    }

    override fun onClick(v: View) {
    }
}