package com.example.liveearthmapuet.classes

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.liveearthmapuet.R

class EmailUsDialogBox(var c: Activity) : Dialog(c), View.OnClickListener {
    var d: Dialog? = null
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.email_us_dialog)
    }

    override fun onClick(v: View) {

    }
}