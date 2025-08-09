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

        binding.textViewInfo.text = "버튼을 네 번 눌러 템포를 맞춰주세요"
        binding.buttonTap.text = "탭하여 시작"
        binding.textViewBpm.text = ""

        binding.buttonTap.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (!isPlaying) {
                tapTimes.add(currentTime)

                if (tapTimes.size == 1) {
                    binding.textViewInfo.text = "첫 번째 탭 기록됨"
                } else if (tapTimes.size in 2..4) {
                    binding.textViewInfo.text = "${tapTimes.size}번째 탭 기록됨"
                }

                if (tapTimes.size == 4) {
                    // 간격 구하기
                    val intervals = mutableListOf<Long>()
                    for (i in 1 until tapTimes.size) {
                        intervals.add(tapTimes[i] - tapTimes[i - 1])
                    }
                    val avgInterval = intervals.average().toLong()

                    if (avgInterval < 200) {
                        Toast.makeText(requireContext(), "간격이 너무 짧아요. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                        resetTaps()
                        return@setOnClickListener
                    }

                    intervalMillis = avgInterval
                    val bpm = (60000.0 / avgInterval).toInt()
                    binding.textViewBpm.text = "$bpm BPM"
                    binding.textViewInfo.text = "SelfBeat 시작!"

                    startSelfBeat(intervalMillis)
                    tapTimes.clear()
                }

            } else {
                // 메트로놈 정지
                stopSelfBeat()
                binding.textViewInfo.text = "버튼을 네 번 눌러 템포를 맞춰주세요"
                binding.textViewBpm.text = ""
                resetTaps()
            }
        }
    }

    private fun startSelfBeat(intervalMs: Long) {
        stopSelfBeat()
        isPlaying = true
        binding.buttonTap.text = "멈추기"
        handler.post(tickRunnable)
    }

    private fun stopSelfBeat() {
        isPlaying = false
        binding.buttonTap.text = "탭하여 시작"
        handler.removeCallbacks(tickRunnable)
        toneGen.stopTone()
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
