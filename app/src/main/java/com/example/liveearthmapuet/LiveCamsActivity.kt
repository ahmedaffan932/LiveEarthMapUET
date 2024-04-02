package com.example.liveearthmapuet
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.liveearthmapuet.adapters.AllCamsAdapter
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityLiveCamsBinding

class LiveCamsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLiveCamsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        binding = ActivityLiveCamsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackSpeedometer.setOnClickListener {
            onBackPressed()
        }

        val adapter = AllCamsAdapter(this@LiveCamsActivity, Misc.allCamList)
        binding.rvCams.layoutManager = GridLayoutManager(this@LiveCamsActivity, 2)
        binding.rvCams.adapter = adapter

    }





}