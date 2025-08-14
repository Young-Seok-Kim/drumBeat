package com.youngs.beatdrum.ui.metronome

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.youngs.beatdrum.R
import com.youngs.beatdrum.databinding.FragmentMetronomeBinding

class MetronomeFragment : Fragment() {

    private var _binding: FragmentMetronomeBinding? = null
    private val binding get() = _binding!!

    private var isPlaying = false
    private var bpm = 60
    private var noteTypeMultiplier = 1 // 4분=1, 8분=2, 16분=4
    private var beatsPerMeasure = 4    // 한 마디 박자 수 (4/4 박자)
    private var currentBeat = 0        // 현재 마디 안의 박자 위치

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var toneGen: ToneGenerator

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                // 강박 / 약박 소리 구분
                if (currentBeat == 0) {
                    // 첫 박자: 높은음
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                } else {
                    // 나머지 박자: 낮은음
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                }

                // 다음 박자 계산
                currentBeat = (currentBeat + 1) % (beatsPerMeasure * noteTypeMultiplier)

                val interval = (60000 / (bpm * noteTypeMultiplier)).toLong()
                handler.postDelayed(this, interval)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMetronomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        initViews()
    }

    private fun initViews() {
        // BPM SeekBar
        binding.seekBarBpm.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bpm = progress.coerceIn(30, 240)
                if (binding.editTextBpm.text.toString() != bpm.toString()) {
                    binding.editTextBpm.setText(bpm.toString())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // BPM EditText
        binding.editTextBpm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val inputBpm = input.toIntOrNull()
                if (inputBpm != null) {
                    val clamped = inputBpm.coerceIn(30, 240)
                    if (clamped != bpm) {
                        bpm = clamped
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

        binding.editTextBpm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.editTextBpm.clearFocus()
                true
            } else false
        }

        // 음표 선택 Spinner
        val noteOptions = listOf("♩", "♪", "♬") // 4분음표, 8분음표, 16분음표
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, noteOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerNote.adapter = adapter

        binding.spinnerNote.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                noteTypeMultiplier = when (position) {
                    0 -> 1 // 4분음표
                    1 -> 2 // 8분음표
                    2 -> 4 // 16분음표
                    else -> 1
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 시작/정지 버튼
        binding.buttonStartStop.setOnClickListener {
            if (isPlaying) stopMetronome() else startMetronome()
        }
    }

    private fun startMetronome() {
        isPlaying = true
        currentBeat = 0
        binding.buttonStartStop.text = getString(R.string.stop)
        handler.post(tickRunnable)
    }

    fun stopMetronome() {
        isPlaying = false
        binding.buttonStartStop.text = getString(R.string.start)
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
