package com.youngs.beatdrum.ui.selfbeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.youngs.beatdrum.R
import com.youngs.beatdrum.data.Settings
import com.youngs.beatdrum.databinding.FragmentSelfBeatBinding

class SelfBeatFragment : Fragment() {

    private var _binding: FragmentSelfBeatBinding? = null
    private val binding get() = _binding!!

    private val tapTimes = mutableListOf<Long>() // 탭 시각 기록

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfBeatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MobileAds.initialize(requireContext())
        binding.adView.loadAd(AdRequest.Builder().build())

        binding.textViewBpm.text = ""

        binding.buttonTap.setOnClickListener {
            val now = System.currentTimeMillis()
            // 3초 넘게 쉬었다가 다시 누르면 새로 측정한다. 이전 탭과 섞이면 평균이 엉뚱해진다.
            if (tapTimes.isNotEmpty() && now - tapTimes.last() > 3000) {
                tapTimes.clear()
                binding.textViewBpm.text = ""
                binding.textViewInfo.text = getString(R.string.selfbeat_4click)
            }
            tapTimes.add(now)

            if (tapTimes.size < 4) return@setOnClickListener

            val lastTaps = tapTimes.takeLast(4)
            val avgInterval = (1 until lastTaps.size)
                .map { lastTaps[it] - lastTaps[it - 1] }
                .average()

            if (avgInterval < 200) {
                Toast.makeText(requireContext(), R.string.selfbeat_too_fast, Toast.LENGTH_SHORT).show()
                tapTimes.clear()
                return@setOnClickListener
            }

            // 메트로놈에서 고른 음표 단위로 탭한 것으로 본다.
            // 8분음표로 탭하면 4분음표 BPM 은 탭 간격 BPM 의 절반이다.
            val multiplier = Settings.noteMultiplier(
                Settings.prefs(requireContext()).getInt(Settings.KEY_NOTE_POS, 0)
            )
            val bpm = (60_000.0 / avgInterval / multiplier).toInt()

            binding.textViewBpm.text = getString(R.string.selfbeat_bpm_format, bpm)
            binding.textViewInfo.text = getString(R.string.selfbeat_measuring)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tapTimes.clear()
        _binding = null
    }
}
