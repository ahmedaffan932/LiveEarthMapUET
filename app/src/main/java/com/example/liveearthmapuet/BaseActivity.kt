package com.example.liveearthmapuet


import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager

open class BaseActivity : AppCompatActivity() {


    private val keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener =
        ViewTreeObserver.OnGlobalLayoutListener {
            val heightDiff: Int = rootLayout!!.rootView.height - rootLayout!!.height
            val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
            val broadcastManager = LocalBroadcastManager.getInstance(this@BaseActivity)
            if (heightDiff <= contentViewTop) {
                onHideKeyboard()
                val intent = Intent("KeyboardWillHide")
                broadcastManager.sendBroadcast(intent)
            } else {
                val keyboardHeight = heightDiff - contentViewTop
                onShowKeyboard(keyboardHeight)
                val intent = Intent("KeyboardWillShow")
                intent.putExtra("KeyboardHeight", keyboardHeight)
                broadcastManager.sendBroadcast(intent)
            }
        }

    private var keyboardListenersAttached = false
    protected var rootLayout: ViewGroup? = null

    protected open fun onShowKeyboard(keyboardHeight: Int) {}
    protected open fun onHideKeyboard() {}

    protected fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }
        rootLayout = findViewById<View>(R.id.clRoot) as ViewGroup
        rootLayout?.getViewTreeObserver()?.addOnGlobalLayoutListener(keyboardLayoutListener)
        keyboardListenersAttached = true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (keyboardListenersAttached) {
            rootLayout?.viewTreeObserver?.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }
}