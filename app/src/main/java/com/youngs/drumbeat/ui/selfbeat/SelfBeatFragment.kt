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

    private var lastTapTime: Long = 0L
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

        binding.textViewInfo.text = "버튼을 두 번 눌러 템포를 맞춰주세요"
        binding.buttonTap.text = "탭하여 시작"
        binding.textViewBpm.text = ""

        binding.buttonTap.setOnClickListener {
            val currentTime = System.currentTimeMillis()

            if (!isPlaying) {
                if (lastTapTime == 0L) {
                    lastTapTime = currentTime
                    binding.textViewInfo.text = "첫 번째 탭이 기록됐어요!\n다시 한 번 눌러주세요."
                } else {
                    intervalMillis = currentTime - lastTapTime
                    lastTapTime = 0L

                    if (intervalMillis < 200) {
                        Toast.makeText(requireContext(), "너무 짧은 간격입니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
                        binding.textViewInfo.text = "버튼을 두 번 눌러 템포를 맞춰주세요"
                        return@setOnClickListener
                    }

                    val bpm = (60000.0 / intervalMillis).toInt()
                    binding.textViewBpm.text = "$bpm BPM"
                    binding.textViewInfo.text = "SelfBeat가 시작됩니다!"
                    startSelfBeat(intervalMillis)
                }
            } else {
                stopSelfBeat()
                binding.textViewInfo.text = "버튼을 두 번 눌러 템포를 맞춰주세요"
                binding.textViewBpm.text = ""
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

    override fun onDestroyView() {
        super.onDestroyView()
        stopSelfBeat()
        toneGen.release()
        _binding = null
    }
}
