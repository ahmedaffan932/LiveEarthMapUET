package com.example.liveearthmapuet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.liveearthmapuet.adapters.OnboardingViewPagerAdaptor
import com.example.liveearthmapuet.classes.AdMobInterstitial
import com.example.liveearthmapuet.classes.Ads
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityOnBoardingBinding
import com.example.liveearthmapuet.interfaces.InterstitialCallBack
import com.example.liveearthmapuet.interfaces.LoadInterstitialCallBack
import com.google.android.material.tabs.TabLayout

class OnBoardingActivity : AppCompatActivity() {
    lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSkip.setOnClickListener {
            startNextActivity()
        }

        binding.btnNext.setOnClickListener {
            if (binding.tablayoutPoint.selectedTabPosition < 2) {
                binding.tablayoutPoint.selectTab(binding.tablayoutPoint.getTabAt(binding.tablayoutPoint.selectedTabPosition + 1))
            } else {
                startNextActivity()
            }
        }

        val pagerAdapter = OnboardingViewPagerAdaptor(supportFragmentManager)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = 0
        binding.tablayoutPoint.setupWithViewPager(binding.viewPager)

        binding.tablayoutPoint.addOnTabSelectedListener(
            object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    when (tab.position) {
                        0 -> {
                            binding.tvTitle.text = getString(R.string.satellite_view)
                            binding.tvDescription.text =
                                getString(R.string.satellite_view_maps_offer_comprehensive_aerial_imagery_of_the_earth_s_surface_facilitating_navigation_urban_planning_and_environmental_monitoring)


                            binding.btnNext.text = getString(R.string.next)
                        }

                        1 -> {
                            binding.tvTitle.text = getString(R.string.traffic_map)
                            binding.tvDescription.text =
                                getString(R.string.traffic_view_maps_provide_up_to_the_minute_updates_on_traffic_conditions_empowering_commuters_to_navigate_with_precision_circumvent_congestion_and_enjoy_seamless_journeys)


                            binding.btnNext.text = getString(R.string.next)
                        }

                        else -> {
                            binding.tvTitle.text = getString(R.string.area_measurement)
                            binding.tvDescription.text =
                                getString(R.string.area_measurement_maps_empower_users_to_precisely_calculate_land_size_offering_critical_support_for_urban_planning_agriculture_and_environmental_management_initiatives)

                            binding.btnNext.text = getString(R.string.continue_)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

        Ads.showNativeAd(this, binding.nativeAdFrameLayout, Misc.onboardingNative)

    }

    private fun startNextActivity(){
        Ads.loadAndShowInterstitial(this, Misc.dashboardInt, object : InterstitialCallBack {
            override fun onDismiss() {
                startActivity(Intent(this@OnBoardingActivity, MainActivity::class.java))
            }
        })
    }
}