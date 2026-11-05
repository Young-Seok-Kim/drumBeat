package com.youngs.beatdrum.ui.metronome

import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.youngs.beatdrum.R
import com.youngs.beatdrum.data.Settings
import com.youngs.beatdrum.databinding.FragmentMetronomeBinding

class MetronomeFragment : Fragment() {

    private var _binding: FragmentMetronomeBinding? = null
    private val binding get() = _binding!!

    private var isPlaying = false
    private var bpm = Settings.DEFAULT_BPM
    private var noteTypeMultiplier = 1 // 4분=1, 8분=2, 16분=4
    private var beatsPerMeasure = 4    // 한 마디 박자 수
    private var currentBeat = 0        // 현재 마디 안의 박자 위치

    // 시작 시각 기준으로 n번째 틱의 절대 시각을 계산해 예약한다.
    // postDelayed 로 이어 붙이면 매 틱의 처리 지연이 누적돼 1분만 지나도 박자가 밀린다.
    private var startUptime = 0L
    private var tickIndex = 0L

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var prefs: SharedPreferences
    private lateinit var soundPool: SoundPool
    private var soundHigh = 0
    private var soundLow = 0

    private val tickRunnable = object : Runnable {
        override fun run() {
            if (!isPlaying) return
            if (currentBeat == 0) {
                soundPool.play(soundHigh, 1f, 1f, 1, 0, 1f)
            } else {
                soundPool.play(soundLow, 0.7f, 0.7f, 1, 0, 1f)
            }
            currentBeat = (currentBeat + 1) % (beatsPerMeasure * noteTypeMultiplier)

            tickIndex++
            handler.postAtTime(this, startUptime + tickIndex * intervalMillis())
        }
    }

    // 초견 화면에서 박자표를 바꾸면 메트로놈 스피너도 따라간다 (같은 설정 키를 본다).
    // BPM 측정 화면의 "메트로놈에 적용"도 같은 방식으로 KEY_BPM 으로 들어온다.
    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (_binding == null) return@OnSharedPreferenceChangeListener
        when (key) {
            Settings.KEY_TIME_SIG_POS -> {
                val pos = prefs.getInt(key, Settings.DEFAULT_TIME_SIG_POS)
                if (binding.spinnerTimeSignature.selectedItemPosition != pos) {
                    binding.spinnerTimeSignature.setSelection(pos)
                }
            }
            Settings.KEY_BPM -> {
                val value = prefs.getInt(key, Settings.DEFAULT_BPM)
                if (value != bpm) {
                    setBpm(value)
                    binding.seekBarBpm.progress = bpm
                    binding.editTextBpm.setText(bpm.toString())
                }
            }
        }
    }

    private fun intervalMillis(): Long = 60_000L / (bpm * noteTypeMultiplier)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMetronomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = Settings.prefs(requireContext())
        prefs.registerOnSharedPreferenceChangeListener(prefListener)

        // ToneGenerator 비프음 대신 짧은 클릭 샘플. 지연이 적고 드럼 연습용으로 또렷하다.
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()
        soundHigh = soundPool.load(requireContext(), R.raw.click_high, 1)
        soundLow = soundPool.load(requireContext(), R.raw.click_low, 1)

        initViews()
        restoreSettings()
    }

    private fun restoreSettings() {
        bpm = prefs.getInt(Settings.KEY_BPM, Settings.DEFAULT_BPM).coerceIn(30, 240)
        val notePos = prefs.getInt(Settings.KEY_NOTE_POS, 0)
        val timeSigPos = prefs.getInt(Settings.KEY_TIME_SIG_POS, Settings.DEFAULT_TIME_SIG_POS)
        noteTypeMultiplier = Settings.noteMultiplier(notePos)
        beatsPerMeasure = Settings.beatsPerMeasure(timeSigPos)

        binding.seekBarBpm.progress = bpm
        binding.editTextBpm.setText(bpm.toString())
        binding.spinnerNote.setSelection(notePos)
        binding.spinnerTimeSignature.setSelection(timeSigPos)
    }

    private fun initViews() {
        binding.seekBarBpm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                setBpm(progress)
                if (binding.editTextBpm.text.toString() != bpm.toString()) {
                    binding.editTextBpm.setText(bpm.toString())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.editTextBpm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val inputBpm = s.toString().toIntOrNull() ?: return
                val clamped = inputBpm.coerceIn(30, 240)
                if (clamped != bpm) {
                    setBpm(clamped)
                    if (binding.seekBarBpm.progress != bpm) {
                        binding.seekBarBpm.progress = bpm
                    }
                }
                if (inputBpm != clamped) {
                    binding.editTextBpm.setText(clamped.toString())
                    binding.editTextBpm.setSelection(binding.editTextBpm.text.length)
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

        binding.spinnerNote.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                noteTypeMultiplier = Settings.noteMultiplier(position)
                prefs.edit { putInt(Settings.KEY_NOTE_POS, position) }
                restartIfPlaying()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.spinnerTimeSignature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                beatsPerMeasure = Settings.beatsPerMeasure(position)
                if (prefs.getInt(Settings.KEY_TIME_SIG_POS, Settings.DEFAULT_TIME_SIG_POS) != position) {
                    prefs.edit { putInt(Settings.KEY_TIME_SIG_POS, position) }
                }
                restartIfPlaying()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.buttonStartStop.setOnClickListener {
            if (isPlaying) stopMetronome() else startMetronome()
        }
    }

    private fun setBpm(value: Int) {
        val clamped = value.coerceIn(30, 240)
        if (clamped == bpm) return
        bpm = clamped
        prefs.edit { putInt(Settings.KEY_BPM, bpm) }
        restartIfPlaying()
    }

    // 재생 중에 템포·음표·박자표가 바뀌면 기준 시각을 다시 잡는다. 안 그러면 바뀐 간격이 다음 틱부터 어긋난다.
    private fun restartIfPlaying() {
        if (isPlaying) {
            handler.removeCallbacks(tickRunnable)
            startUptime = SystemClock.uptimeMillis()
            tickIndex = 0
            currentBeat = 0
            handler.post(tickRunnable)
        }
    }

    private fun startMetronome() {
        isPlaying = true
        currentBeat = 0
        tickIndex = 0
        startUptime = SystemClock.uptimeMillis()
        binding.buttonStartStop.text = getString(R.string.stop)
        handler.post(tickRunnable)
    }

    fun stopMetronome() {
        isPlaying = false
        _binding?.buttonStartStop?.text = getString(R.string.start)
        handler.removeCallbacks(tickRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMetronome()
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener)
        soundPool.release()
        _binding = null
    }
}
