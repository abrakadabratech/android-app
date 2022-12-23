package com.oss.abraakadabraaapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.oss.abraakadabraaapp.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private lateinit var title: String
    private lateinit var description: String
    private var imageResource: Int? = null
    private var bottomImageBg: Int? = null
    private var topImage: Int? = null
    private var centerImage: Int? = null
    private var index = 0

    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)!!
            description = requireArguments().getString(ARG_PARAM2)!!
            imageResource = requireArguments().getInt(ARG_PARAM3)
            bottomImageBg = requireArguments().getInt(ARG_PARAM4)
            topImage = requireArguments().getInt(ARG_PARAM5)
            centerImage = requireArguments().getInt(ARG_PARAM6)
            index = requireArguments().getInt(ARG_PARAM7)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        with(binding) {
            tvTitle.text = title
            tvSubTitle.text = description
            ivBottomBg.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    bottomImageBg!!
                )
            )
            ivTop.setImageDrawable(ContextCompat.getDrawable(requireContext(), topImage!!))
            setCenterImage()
            if (imageResource != 0) {
                if (index == 0) {
                    lottieAnimationView.visibility = View.VISIBLE
                    lottieAnimationView1.visibility = View.INVISIBLE
                    lottieAnimationView2.visibility = View.INVISIBLE
                    lottieAnimationView.setAnimation(imageResource!!)
                }
                if (index == 1) {
                    lottieAnimationView1.visibility = View.VISIBLE
                    lottieAnimationView.visibility = View.INVISIBLE
                    lottieAnimationView2.visibility = View.INVISIBLE
                    lottieAnimationView1.setAnimation(imageResource!!)
                }
                if (index == 2) {
                    lottieAnimationView.visibility = View.INVISIBLE
                    lottieAnimationView1.visibility = View.INVISIBLE
                    lottieAnimationView2.visibility = View.VISIBLE
                    lottieAnimationView2.setAnimation(imageResource!!)
                }
            } else {
                lottieAnimationView.visibility = View.INVISIBLE
            }

        }

        return binding.root
    }

    private fun setCenterImage() {
        with(binding) {
            if (centerImage != 0) {
                ivCenter.visibility = View.VISIBLE
                ivCenter.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        centerImage!!
                    )
                )
            } else {
                ivCenter.visibility = View.INVISIBLE
            }
        }
    }

    companion object {

        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"
        private const val ARG_PARAM4 = "param4"
        private const val ARG_PARAM5 = "param5"
        private const val ARG_PARAM6 = "param6"
        private const val ARG_PARAM7 = "param7"
        fun newInstance(
            title: String,
            description: String,
            imageResource: Int?,
            bottomImageBg: Int,
            topImage: Int?,
            centerImage: Int?,
            index: Int,
        ): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, description)
            args.putInt(ARG_PARAM4, bottomImageBg)
            args.putInt(ARG_PARAM3, imageResource ?: 0)
            args.putInt(ARG_PARAM5, topImage ?: 0)
            args.putInt(ARG_PARAM6, centerImage ?: 0)
            args.putInt(ARG_PARAM7, index)
            fragment.arguments = args
            return fragment
        }
    }
}
