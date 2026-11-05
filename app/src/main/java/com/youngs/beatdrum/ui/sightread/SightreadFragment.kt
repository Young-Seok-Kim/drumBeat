package com.youngs.beatdrum.ui.sightread

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.youngs.beatdrum.R
import com.youngs.beatdrum.data.Settings
import com.youngs.beatdrum.databinding.FragmentDrumBinding

class SightreadFragment : Fragment() {
    // 초견 메뉴

    private var _binding: FragmentDrumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SightreadViewModel by viewModels()

    // 현재 선택된 박자 (기본값 4/4)
    private var selectedBeats = 4

    private val imageViews get() = listOf(binding.image1, binding.image2, binding.image3, binding.image4)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDrumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScaleTypeByOrientation()
        setListener()
        setObserve()
        restoreSettings()
    }

    // 매니페스트가 orientation 을 직접 처리해 회전해도 프래그먼트가 다시 만들어지지 않는다.
    // 그래서 여기서 다시 맞춰 줘야 가로에서 음표가 세로 비율로 남지 않는다.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (_binding != null) setScaleTypeByOrientation()
    }

    private fun restoreSettings() {
        val prefs = Settings.prefs(requireContext())
        binding.spinnerTimeSignature.setSelection(
            prefs.getInt(Settings.KEY_TIME_SIG_POS, Settings.DEFAULT_TIME_SIG_POS)
        )
        if (binding.editTextTime.text.isNullOrEmpty()) {
            binding.editTextTime.setText(
                prefs.getInt(Settings.KEY_SIGHTREAD_SECONDS, Settings.DEFAULT_SIGHTREAD_SECONDS).toString()
            )
        }
    }

    private fun setListener() {
        // 시작/종료 버튼
        binding.buttonStartStop.setOnClickListener {
            if (viewModel.isRunning.value == true) {
                viewModel.stopDrum()
                Toast.makeText(requireContext(), R.string.sightread_stopped, Toast.LENGTH_SHORT).show()
            } else {
                val seconds = binding.editTextTime.text.toString().toIntOrNull()
                if (seconds == null || seconds !in 1..10) {
                    Toast.makeText(requireContext(), R.string.sightread_time_range, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                Settings.prefs(requireContext()).edit { putInt(Settings.KEY_SIGHTREAD_SECONDS, seconds) }
                viewModel.setInterval(seconds)
                viewModel.startDrum()
                Toast.makeText(requireContext(), getString(R.string.sightread_started, seconds), Toast.LENGTH_SHORT).show()
            }
        }

        // 악보고정 체크박스
        binding.checkBoxFixScore.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFixScore(isChecked)
        }

        // 박자표. 메트로놈과 같은 설정 키를 써서 양쪽이 같이 바뀐다.
        binding.spinnerTimeSignature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBeats = Settings.beatsPerMeasure(position)
                val prefs = Settings.prefs(requireContext())
                if (prefs.getInt(Settings.KEY_TIME_SIG_POS, Settings.DEFAULT_TIME_SIG_POS) != position) {
                    prefs.edit { putInt(Settings.KEY_TIME_SIG_POS, position) }
                }
                updateImageVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setObserve() {
        viewModel.isRunning.observe(viewLifecycleOwner) { running ->
            updateButtonText(running)
            setViewsVisible(running)
        }

        viewModel.remainingSeconds.observe(viewLifecycleOwner) { remaining ->
            binding.textViewRemainingTime.text =
                if (remaining > 0) getString(R.string.sightread_remaining, remaining)
                else getString(R.string.sightread_stopped)
            updateProgressBar(remaining, viewModel.intervalSeconds.value ?: 3)
        }

        viewModel.numbers.observe(viewLifecycleOwner) { nums ->
            setRandomNumbersAndImages(nums)
        }

        viewModel.intervalSeconds.observe(viewLifecycleOwner) {
            if (viewModel.remainingSeconds.value == null || viewModel.remainingSeconds.value == 0) {
                binding.textViewRemainingTime.text = getString(R.string.sightread_remaining_none)
                binding.progressBarTimer.progress = 0
            }
        }
    }

    private fun updateButtonText(running: Boolean) {
        binding.buttonStartStop.text = if (running) getString(R.string.stop) else getString(R.string.start)
    }

    private fun setViewsVisible(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        imageViews.forEach { it.visibility = visibility }
        updateImageVisibility()
    }

    // 박자 수에 따라 이미지 표시 개수 조절
    private fun updateImageVisibility() {
        imageViews.forEachIndexed { index, imageView ->
            imageView.visibility = if (index < selectedBeats) View.VISIBLE else View.GONE
        }
    }

    private fun updateProgressBar(remainingSec: Int, totalSec: Int) {
        val progressPercent = if (totalSec > 0) {
            (remainingSec.toFloat() / totalSec.toFloat() * 100).toInt()
        } else 0
        binding.progressBarTimer.progress = progressPercent
    }

    private fun setRandomNumbersAndImages(nums: List<Int>) {
        for (i in nums.indices) {
            if (i >= selectedBeats) break // 선택된 박자 수까지만 표시
            val resId = BEAT_DRAWABLES.getOrNull(nums[i] - 1)
            if (resId != null) imageViews[i].setImageResource(resId)
            else imageViews[i].setImageDrawable(null)
        }
    }

    private fun setScaleTypeByOrientation() {
        val scaleType = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ImageView.ScaleType.FIT_XY else ImageView.ScaleType.FIT_CENTER
        imageViews.forEach { it.scaleType = scaleType }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // getIdentifier 로 매번 이름을 찾는 대신 id 를 직접 둔다. 빠르고 R8 난독화에도 안전하다.
        private val BEAT_DRAWABLES = intArrayOf(
            R.drawable.sightread_beat1, R.drawable.sightread_beat2, R.drawable.sightread_beat3, R.drawable.sightread_beat4,
            R.drawable.sightread_beat5, R.drawable.sightread_beat6, R.drawable.sightread_beat7, R.drawable.sightread_beat8,
            R.drawable.sightread_beat9, R.drawable.sightread_beat10, R.drawable.sightread_beat11, R.drawable.sightread_beat12,
            R.drawable.sightread_beat13, R.drawable.sightread_beat14, R.drawable.sightread_beat15, R.drawable.sightread_beat16,
        )
    }
}
