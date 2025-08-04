package com.youngs.drumbeat

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import com.youngs.drumbeat.databinding.FragmentMetronomeBinding

class MetronomeDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentMetronomeBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMetronomeBinding.inflate(inflater, container, false)
        toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.seekBarBpm.setOnSeekBarChangeListener(object :
            android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                bpm = progress.coerceIn(30, 240)
                binding.textViewBpm.text = "BPM: $bpm"
                if (binding.editTextBpm.text.toString() != bpm.toString()) {
                    binding.editTextBpm.setText(bpm.toString())
                }
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.editTextBpm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val inputBpm = input.toIntOrNull()
                if (inputBpm != null) {
                    val clamped = inputBpm.coerceIn(30, 240)
                    if (clamped != bpm) {
                        bpm = clamped
                        binding.textViewBpm.text = "BPM: $bpm"
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
            } else {
                false
            }
        }

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
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}
