package com.youngs.beatdrum.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.youngs.beatdrum.R
import com.youngs.beatdrum.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MobileAds.initialize(requireContext())
        binding.adView.loadAd(AdRequest.Builder().build())

        viewModel.menuItems.observe(viewLifecycleOwner) { items ->
            binding.buttonContainer.removeAllViews()
            val density = resources.displayMetrics.density
            fun dp(v: Int) = (v * density).toInt()

            items.forEach { item ->
                if (item.fragmentId == R.id.mainFragment) return@forEach

                val btn = Button(requireContext()).apply {
                    text = getString(item.titleStringId)
                    isAllCaps = false
                    textSize = 17f
                    setTextColor(resources.getColor(R.color.bd_on_primary, null))
                    background = resources.getDrawable(R.drawable.bd_menu_button, null)
                    stateListAnimator = null
                    elevation = dp(3).toFloat()
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(60)
                    ).apply {
                        marginStart = dp(20)
                        marginEnd = dp(20)
                        topMargin = dp(9)
                        bottomMargin = dp(9)
                    }
                    setOnClickListener { viewModel.onMenuSelected(item) }
                }
                binding.buttonContainer.addView(btn)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
