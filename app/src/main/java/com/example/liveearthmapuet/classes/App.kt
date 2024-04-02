package com.example.liveearthmapuet.classes

import android.app.Application
import com.example.liveearthmapuet.classes.Misc.Companion.readJsonFile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            readJsonFile()
        }

    }
}