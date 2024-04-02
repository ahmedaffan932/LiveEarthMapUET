package com.example.liveearthmapuet.interfaces

import android.annotation.SuppressLint
import android.app.Activity
import android.webkit.JavascriptInterface
import com.blongho.country_data.World

class WebAppInterface(private val activity: Activity, private val countryListInterface: CountryListInterface) {

    /** Show a toast from the web page  */
    @SuppressLint("LogNotTimber", "SetTextI18n")
    @JavascriptInterface
    fun selectCountryInCountry(countryId: String) {
        try{
            val country = World.getCountryFrom(countryId)

            countryListInterface.onCountryClick(country)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}