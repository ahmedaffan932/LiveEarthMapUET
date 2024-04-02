package com.example.liveearthmapuet.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.liveearthmapuet.OnboardingFragment

class OnboardingViewPagerAdaptor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    val arr = arrayOf("", "", "")
    override fun getCount(): Int {
        return arr.size
    }

    override fun getItem(position: Int): Fragment {
        return OnboardingFragment.newInstance(position)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return arr[position]
    }
}