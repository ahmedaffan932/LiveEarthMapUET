package com.example.liveearthmapuet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.liveearthmapuet.databinding.FragmentOnboardingBinding

private const val ARG_PARAM1 = "arg1"

class OnboardingFragment : Fragment() {
    lateinit var binding: FragmentOnboardingBinding
    private var param1: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)

        when (param1) {
            0 -> {
                binding.clMain.setBackgroundResource(R.drawable.bg_on_boarding_one)
            }

            1 -> {
                binding.clMain.setBackgroundResource(R.drawable.bg_on_boarding_two)
            }

            2 -> {
                binding.clMain.setBackgroundResource(R.drawable.bg_on_boarding_three)
            }

            else -> {
                binding.clMain.setBackgroundResource(R.drawable.bg_on_boarding_one)
            }
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

}