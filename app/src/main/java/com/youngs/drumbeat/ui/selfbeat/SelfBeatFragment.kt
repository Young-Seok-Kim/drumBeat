package com.youngs.drumbeat.ui.selfbeat

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.youngs.drumbeat.databinding.FragmentSelfBeatBinding

class SelfBeatFragment : Fragment() {

    private var _binding: FragmentSelfBeatBinding? = null
    private val binding get() = _binding!!

    private val tapTimes = mutableListOf<Long>() // 클릭 시각 기록 리스트
    private var intervalMillis: Long = 0L
    private var isPlaying = false

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var toneGen: ToneGenerator

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (isPlaying && intervalMillis > 0) {
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP)
                handler.postDelayed(this, intervalMillis)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfBeatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        binding.textViewInfo.text = "버튼을 네 번 이상 눌러 템포를 맞춰주세요"
        binding.textViewBpm.text = ""

        // "탭하여 측정" 버튼 클릭
        binding.buttonTap.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            // 터치 시간 기록
            tapTimes.add(currentTime)

            if (tapTimes.size >= 4) {
                val lastTaps = tapTimes.takeLast(4)
                val intervals = mutableListOf<Long>()
                for (i in 1 until lastTaps.size) {
                    intervals.add(lastTaps[i] - lastTaps[i - 1])
                }
                val avgInterval = intervals.average().toLong()

                if (avgInterval < 200) {
                    Toast.makeText(requireContext(), "간격이 너무 짧아요. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    tapTimes.clear()
                    stopSelfBeat()
                    return@setOnClickListener
                }

                intervalMillis = avgInterval
                val bpm = (60000.0 / avgInterval).toInt()
                binding.textViewBpm.text = "$bpm BPM"
                binding.textViewInfo.text = "측정 중... (최근 4회 평균)"

                if (!isPlaying) {
                    startSelfBeat(intervalMillis)
                }
            }
        }

        // "멈추기" 버튼 클릭
        binding.buttonStop.setOnClickListener {
            stopSelfBeat()
            binding.textViewInfo.text = "버튼을 네 번 이상 눌러 템포를 맞춰주세요"
            binding.textViewBpm.text = ""
        }
    }

    private fun startSelfBeat(intervalMs: Long) {
        isPlaying = true
        binding.buttonTap.isEnabled = true
        binding.buttonStop.visibility = View.VISIBLE
        handler.post(tickRunnable)
    }

    private fun stopSelfBeat() {
        isPlaying = false
        handler.removeCallbacks(tickRunnable)
        toneGen.stopTone()
        tapTimes.clear()
        binding.buttonStop.visibility = View.GONE
    }

    private fun resetTaps() {
        tapTimes.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopSelfBeat()
        toneGen.release()
        _binding = null
    }
}
