package com.example.liveearthmapuet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.interfaces.OnImageSaveCallBack
import com.google.zxing.WriterException
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityQrgenratedBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.NativeAdCallBack

class QRGeneratedActivity : AppCompatActivity() {
    private val storageReadPermissionRequest = 101
    private lateinit var qrBitmap: Bitmap
    lateinit var binding: ActivityQrgenratedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityQrgenratedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getStoragePermission()
        }


        Ads.showNativeAd(
            this,
            binding.nativeAd,
            Misc.createQRNative,
            object : NativeAdCallBack {
                override fun onLoad() {
                    binding.nativeAd.visibility = View.VISIBLE
                }
            }
        )

        val qrgEncoder =
            QRGEncoder(intent.getStringExtra(Misc.data), null, QRGContents.Type.TEXT, 600)
        qrgEncoder.colorBlack = Color.WHITE
        qrgEncoder.colorWhite = R.color.blue

        try {
            if (qrgEncoder.bitmap == null) {
                Toast.makeText(
                    this@QRGeneratedActivity,
                    getString(R.string.sorry_some_error_occurred_in_qr_creation),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return
            }
            qrBitmap = qrgEncoder.bitmap
            binding.qrImage.setImageBitmap(qrBitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(
                this@QRGeneratedActivity,
                getString(R.string.sorry_some_error_occurred_in_qr_creation),
                Toast.LENGTH_SHORT
            ).show()
            onBackPressed()
        }

        binding.btnSaveQRCode.setOnClickListener {
            Misc.saveImageToExternal(this, qrBitmap, object : OnImageSaveCallBack {
                override fun onImageSaved() {
                    Toast.makeText(
                        this@QRGeneratedActivity,
                        getString(R.string.your_qr_is_saved_in_gallery),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        binding.btnBackQR.setOnClickListener {
            onBackPressed()
        }

        binding.btnShareQRCode.setOnClickListener {
            shareQRCode()
        }


    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storageReadPermissionRequest && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getStoragePermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                storageReadPermissionRequest
            )
        }
    }

    private fun shareQRCode() {
        val bitmap = Bitmap.createBitmap(binding.qrImage.width, binding.qrImage.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        binding.qrImage.draw(canvas)
        val bitmapPath =
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "title", null)
        val bitmapUri: Uri = Uri.parse(bitmapPath)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/jpeg"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(intent, "Share QR"))
    }

//    override fun onResume() {
//        super.onResume()
//        Ads.showNativeAd(
//            this,
//            nativeAdViewQRGenerated,
//            Misc.isGenerateQrOnBackNativeEnabled,
//            object : NativeAdCallBack {
//                override fun onLoad() {
//                    nativeAdViewQRGenerated.visibility = View.VISIBLE
//                }
//            }
//        )
//    }
}