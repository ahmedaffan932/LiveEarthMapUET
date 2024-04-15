package com.example.liveearthmapuet.classes

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.liveearthmapuet.BuildConfig
import com.example.liveearthmapuet.R
import com.example.liveearthmapuet.adapters.AllCamModel
import com.example.liveearthmapuet.interfaces.OnImageSaveCallBack
import com.example.liveearthmapuet.interfaces.StoragePermissionInterface
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.mapbox.api.directions.v5.models.DirectionsRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class Misc {
    @SuppressLint("LogNotTimber")
    companion object {
        var dashboardInt: String = "am"
        var onboardingNative: String = "am_small"
        var appLanguageSelectorNative: String = "am"
        var canRequestAds = true

        var bannerAdId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/6300978111"
        } else {
            ""
        }

        var isSmallNativeSmallBtn: Boolean = false
        var isRemoveAdTagEnabled: Boolean = true
        var anyAdLoaded: MutableLiveData<Boolean> = MutableLiveData()

        var interstitialAdIdAdMob: String = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            ""
        }


        var settingNative: String = "am"

        var monthlySubscriptionId = "monthly_subscription_id"
        var yearlySubscriptionId = "yearly_subscription_id"

        var lifetimePrice = "$99.00"
        var yearlyPrice = "$79.99"
        var monthlyPrice = "$39.99"

        var isPremiumScreenInt: String = "am"
        var isPremiumScreenEnabled: Boolean = true

        var isBannerAdTop: Boolean = true
        var isSplashLargeNative: Boolean = true
        var isCompassBannerEnabled: Boolean = true
        var isNoteCamBannerEnabled: Boolean = true

        var lemInt: String = "am"
        var liveCamInt: String = "am"
        var gpsCamInt: String = "am"
        var isQuitInt: String = "am"
        var compassInt: String = "am"
        var noteCamInt: String = "am"
        var altitudeInt: String = "am"
        var isSplashInt: String = "am"
        var worldQuizInt: String = "am"
        var splashNative: String = "am"
        var startGameInt: String = "am"
        var generateQRInt: String = "am"
        var soundMeterInt: String = "am"
        var settingBackInt: String = "am"
        var compassBackInt: String = "am"
        var speedometerInt: String = "am"
        var createQRNative: String = "am"
        var dashboardNative: String = "am_small"
        var altitudeBackInt: String = "am"
        var quizCompleteInt: String = "am"
        var quizCountriesInt: String = "am"
        var viewWorldBackInt: String = "am"
        var noteCamOnBackInt: String = "am"
        var soundMeterNative: String = "am"
        var speedoMeterNative: String = "am"
        var soundMeterBackInt: String = "am"
        var quizSelectModeInt: String = "am"
        var quizCompleteNative: String = "am"
        var worldQuizOnBackInt: String = "am"
        var continentSelectInt: String = "am"
        var liveEarthOnBackInt: String = "am"
        var generateQrOnBackInt: String = "am"
        var quizCompleteBackInt: String = "am"
        var quizScreenOneNative: String = "am"
        var quizScreenOneBackInt: String = "am"
        var quizSelectModeNative: String = "am"
        var continentSelectNative: String = "am"
        var continentSelectBackInt: String = "am"
        var worldQuizActivityNative: String = "am"

        var nativeAdIdAdMobOne: String = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            ""
        }

        var gameMode: String = ""
        var levels: String = "levels"
        var gameContinent: String = ""
        const val asia: String = "asia"
        const val data: String = "data"
        const val flags: String = "flags"
        const val logKey: String = "logKey"
        const val europe: String = "europe"
        const val africa: String = "africa"
        const val oceania: String = "oceania"
        const val america: String = "america"
        const val wholeWorld: String = "world"
        const val capitals: String = "capitals"
        const val countries: String = "countries"
        private const val flash: String = "flash"
        const val currencies: String = "currencies"
        private const val lastUri: String = "lastUri"
        private const val cameraFace: String = "cameraFace"
        private const val purchasedStatus: String = "purchasedStatus"
        const val appUrl: String =
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

        var intFailedCount = 0
        var navigationLimit = 3
        var nativeFailedCount = 0
        var startingTime: Long = 0


        var location: Location? = null


        var route: DirectionsRoute? = null

        var inAppKey = if (BuildConfig.DEBUG) {
            "android.test.purchased"
        } else {
            "com.liveearth.android.map.liveearthmap.liveearthcam.streetview.gps"
        }

        fun hideShowView(view: View, activity: Activity, isVisible: Boolean): Boolean {
            if (isVisible) {
                hideView(view, activity, isVisible)
            } else {
                showView(view, activity, isVisible)
            }
            return !isVisible
        }

        fun hideView(view: View, activity: Activity, isVisible: Boolean): Boolean {
            return if (!isVisible) {
                false
            } else {
                zoomOutView(view, activity, 150)
                false
            }
        }

        fun showView(view: View, activity: Activity, isVisible: Boolean): Boolean {
            return if (isVisible) {
                true
            } else {
                view.visibility = View.VISIBLE
                zoomInView(view, activity, 150)
                true
            }
        }

        fun zoomInView(view: View, activity: Activity, duration: Int) {
            val a: Animation =
                AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
            a.duration = duration.toLong()
            view.startAnimation(a)
        }

        fun zoomOutView(view: View, activity: Activity, duration: Int) {
            val a: Animation =
                AnimationUtils.loadAnimation(activity, R.anim.zoom_out)
            a.duration = duration.toLong()
            view.startAnimation(a)
        }

        fun getFlash(activity: Activity): Boolean {
            val sharedPreferences: SharedPreferences =
                activity.getSharedPreferences(flash, AppCompatActivity.MODE_PRIVATE)
            return sharedPreferences.getBoolean(flash, false)
        }


        fun setCameraFace(activity: Activity, boolean: Boolean) {
            val sharedPreferences = activity.getSharedPreferences(
                cameraFace,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(cameraFace, boolean)
            editor.apply()
        }

        fun getCameraFace(activity: Activity): Boolean {
            val sharedPreferences: SharedPreferences =
                activity.getSharedPreferences(cameraFace, AppCompatActivity.MODE_PRIVATE)
            return sharedPreferences.getBoolean(cameraFace, true)
        }

        @SuppressLint("DefaultLocale", "SimpleDateFormat")
        fun timeMillsToHms(millis: Long): String {
            val formatter = SimpleDateFormat("dd-MM-yyyy hh:mm")

            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            return formatter.format(calendar.time)

        }

        fun saveImageToExternal(
            activity: Activity,
            bitmap: Bitmap,
            onImageSaveCallBack: OnImageSaveCallBack?
        ): Uri? {
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bitmap.width)
                put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            }
            return try {
                activity.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    activity.contentResolver.openOutputStream(uri).use { outputStream ->
                        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        } else {
                            onImageSaveCallBack?.onImageSaved()
                            return uri
                        }
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        fun getLastSavedUri(activity: Activity): String {
            val sharedPreferences: SharedPreferences =
                activity.getSharedPreferences(lastUri, AppCompatActivity.MODE_PRIVATE)
            return sharedPreferences.getString(lastUri, "o").toString()
        }

        fun setLatestUri(uri: String, activity: Activity) {
            val sharedPreferences =
                activity.getSharedPreferences(lastUri, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(lastUri, uri)
            editor.apply()
        }

        fun setPurchasedStatus(activity: Activity, boolean: Boolean) {
            val sharedPreferences = activity.getSharedPreferences(
                purchasedStatus,
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putBoolean(purchasedStatus, boolean)
            editor.apply()
        }

        fun getPurchasedStatus(activity: Activity?): Boolean {
            val sharedPreferences =
                activity!!.getSharedPreferences(purchasedStatus, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(purchasedStatus, false)
        }

        fun checkInternetConnection(activity: Activity): Boolean {
            return try {
                //Check internet connection:
                val connectivityManager: ConnectivityManager? =
                    activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

                //Means that we are connected to a network (mobile or wi-fi)
                connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state === NetworkInfo.State.CONNECTED ||
                        connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state === NetworkInfo.State.CONNECTED
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fun getNavigationCount(activity: Activity?): Int {
            val sharedPreferences =
                activity!!.getSharedPreferences("navLimit", Context.MODE_PRIVATE)
            return sharedPreferences.getInt("navLimit", 0)
        }

        fun manageNavigationLimit(activity: Activity): Boolean {
            val sharedPreferences = activity.getSharedPreferences(
                "navLimit",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putInt("navLimit", getNavigationCount(activity) + 1)
            editor.apply()

            return getNavigationCount(activity) < navigationLimit
        }

        fun getStoragePermission(
            activity: Activity,
            storageReadPermissionRequest: Int,
            storagePermissionInterface: StoragePermissionInterface
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        storageReadPermissionRequest
                    )
                } else {
                    storagePermissionInterface.onPermissionGranted()
                }
            } else {
                storagePermissionInterface.onPermissionGranted()
            }
        }




        private fun sharedPreferencesVar(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                "LEM",
                AppCompatActivity.MODE_PRIVATE
            )
        }

        private fun sharedPreferancesEditorVar(context: Context): SharedPreferences.Editor {
            return sharedPreferencesVar(context).edit()
        }

        fun setAppLanguage(context: Context, lng: String) {
            sharedPreferancesEditorVar(context).putString("lng", lng).apply()
        }


        fun getAppLanguage(context: Context): String {
            return sharedPreferencesVar(context).getString("lng", "en").toString()
        }

        fun Context.setAppLanguage() {
            val res = resources
            val dm: DisplayMetrics = res.displayMetrics
            val conf: Configuration = res.configuration
            conf.setLocale(Locale(getAppLanguage(this)))
            res.updateConfiguration(conf, dm)
        }


        fun setIsFirstTime(activity: Activity, isFirstTime: Boolean) {
            val sharedPreferences = activity.getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isFirstTime", isFirstTime)
            editor.apply()
        }


        fun isFirstTime(activity: Context): Boolean {
            val sharedPreferences =
                activity.getSharedPreferences("isFirstTime", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("isFirstTime", true)
        }

        var beachList: ArrayList<AllCamModel> = arrayListOf()
        var highwayList: ArrayList<AllCamModel> = arrayListOf()
        var zooCamList: ArrayList<AllCamModel> = arrayListOf()
        var streetList: ArrayList<AllCamModel> = arrayListOf()
        var allCamList: ArrayList<AllCamModel> = arrayListOf()
        var inde: Int = 0

        @SuppressLint("LongLogTag")
        suspend fun Context.readJsonFile() {
            return withContext(Dispatchers.IO)
            {
                val text = resources.openRawResource(R.raw.all_in_one)
                    .bufferedReader().use { it.readText() }

                val jsonParser = JsonParser()
                val jsonArr = jsonParser.parse(text).asJsonObject
                val gson = Gson()
                val listType: Type = object : TypeToken<ArrayList<AllCamModel?>?>() {}.type


                beachList = gson.fromJson<Any>(jsonArr.getAsJsonArray("beach"), listType)
                        as ArrayList<AllCamModel>
                highwayList = gson.fromJson<Any>(jsonArr.getAsJsonArray("highway"), listType)
                        as ArrayList<AllCamModel>
                zooCamList = gson.fromJson<Any>(jsonArr.getAsJsonArray("Zoo Cam"), listType)
                        as ArrayList<AllCamModel>
                streetList = gson.fromJson<Any>(jsonArr.getAsJsonArray("street"), listType)
                        as ArrayList<AllCamModel>

                setCatTitleImg(R.drawable.ic_beach_cam, "beach", beachList, 0)
                setCatTitleImg(R.drawable.ic_building, "highway", highwayList, beachList.size)
                setCatTitleImg(R.drawable.ic_street_cam, "street", streetList, highwayList.size)
                setCatTitleImg(R.drawable.ic_street_cam, "Zoo Cam", zooCamList, streetList.size)

                allCamList.addAll(beachList)
                allCamList.addAll(highwayList)
                allCamList.addAll(streetList)
                allCamList.addAll(zooCamList)
            }

        }

        @SuppressLint("LongLogTag")
        fun setCatTitleImg(res: Int, cat: String, list: ArrayList<AllCamModel>, index: Int) {
            for (i in 0 until list.size) {
                list[i].titleImage = res
                list[i].categoryName = cat
                list[i].id = inde
                inde++
            }
        }
    }
}