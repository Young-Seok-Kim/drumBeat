package com.youngs.drumbeat.ui.metronome

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.youngs.drumbeat.databinding.FragmentMetronomeBinding

class MetronomeFragment : Fragment() {

    private var _binding: FragmentMetronomeBinding? = null
    private val binding get() = _binding!!

    private var isPlaying = false
    private var bpm = 60
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var toneGen: ToneGenerator

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP)
                val interval = (60000 / bpm).toLong()
                handler.postDelayed(this, interval)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMetronomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        initViews()
    }

    private fun initViews() {
        // SeekBar 설정 및 bpm 동기화
        binding.seekBarBpm.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bpm = progress.coerceIn(30, 240)
//                binding.textViewBpm.text = "BPM: $bpm"
                if (binding.editTextBpm.text.toString() != bpm.toString()) {
                    binding.editTextBpm.setText(bpm.toString())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // EditText에 텍스트 변경 리스너 등록하여 SeekBar와 텍스트 동기화
        binding.editTextBpm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val inputBpm = input.toIntOrNull()
                if (inputBpm != null) {
                    val clamped = inputBpm.coerceIn(30, 240)
                    if (clamped != bpm) {
                        bpm = clamped
//                        binding.textViewBpm.text = "BPM: $bpm"
                        if (binding.seekBarBpm.progress != bpm) {
                            binding.seekBarBpm.progress = bpm
                        }
                        if (inputBpm != clamped) {
                            binding.editTextBpm.setText(clamped.toString())
                            binding.editTextBpm.setSelection(binding.editTextBpm.text.length)
                        }
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 키보드 '완료' 버튼 처리
        binding.editTextBpm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.editTextBpm.clearFocus()
                true
            } else {
                false
            }
        }

        // 시작/중지 버튼 클릭 리스너
        binding.buttonStartStop.setOnClickListener {
            if (isPlaying) stopMetronome() else startMetronome()
        }
    }

    private fun startMetronome() {
        isPlaying = true
        binding.buttonStartStop.text = "Stop"
        handler.post(tickRunnable)
    }

    private fun stopMetronome() {
        isPlaying = false
        binding.buttonStartStop.text = "Start"
        handler.removeCallbacks(tickRunnable)
        toneGen.stopTone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMetronome()
        toneGen.release()
        _binding = null
    }
}