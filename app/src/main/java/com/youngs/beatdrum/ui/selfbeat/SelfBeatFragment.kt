package com.youngs.beatdrum.ui.selfbeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.youngs.beatdrum.R
import com.youngs.beatdrum.data.Settings
import com.youngs.beatdrum.databinding.FragmentSelfBeatBinding

class SelfBeatFragment : Fragment() {

    private var _binding: FragmentSelfBeatBinding? = null
    private val binding get() = _binding!!

    private val tapTimes = mutableListOf<Long>() // 탭 시각 기록
    private var lastBpm: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfBeatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonTap.setOnClickListener {
            val now = System.currentTimeMillis()
            // 3초 넘게 쉬었다가 다시 누르면 새로 측정한다. 이전 탭과 섞이면 평균이 엉뚱해진다.
            if (tapTimes.isNotEmpty() && now - tapTimes.last() > 3000) {
                tapTimes.clear()
                binding.textViewBpm.text = getString(R.string.selfbeat_bpm_placeholder)
                binding.textViewInfo.text = getString(R.string.selfbeat_4click)
                binding.buttonApply.visibility = View.INVISIBLE
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
            lastBpm = bpm
            binding.buttonApply.visibility = View.VISIBLE
        }

        // 잰 값을 메트로놈 BPM 으로. 같은 설정 키를 메트로놈이 듣고 있어서 바로 반영된다.
        binding.buttonApply.setOnClickListener {
            val bpm = lastBpm?.coerceIn(30, 240) ?: return@setOnClickListener
            Settings.prefs(requireContext()).edit { putInt(Settings.KEY_BPM, bpm) }
            Toast.makeText(requireContext(), getString(R.string.selfbeat_applied, bpm), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tapTimes.clear()
        _binding = null
    }
}
