package com.youngs.beatdrum.ui.drum

import DrumViewModel
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.youngs.beatdrum.R
import com.youngs.beatdrum.databinding.FragmentDrumBinding

class DrumFragment : Fragment() {

    private var _binding: FragmentDrumBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DrumViewModel by viewModels()

    // 현재 선택된 박자 (기본값 4/4)
    private var selectedBeats = 4

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDrumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setScaleTypeByOrientation()
        setListener()
        setObserve()
    }

    private fun setListener() {
        // 시작/종료 버튼
        binding.buttonStartStop.setOnClickListener {
            if (viewModel.isRunning.value == true) {
                viewModel.stopDrum()
                Toast.makeText(requireContext(), "동작이 중지되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val seconds = binding.editTextTime.text.toString().toIntOrNull()
                if (seconds == null || seconds !in 1..10) {
                    Toast.makeText(requireContext(), "시간을 1초에서 10초 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.setInterval(seconds)
                viewModel.startDrum()
                Toast.makeText(requireContext(), "$seconds 초마다 숫자와 이미지가 갱신됩니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 악보고정 체크박스
        binding.checkBoxFixScore.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setFixScore(isChecked)
        }

        // 스피너 선택 리스너
        binding.spinnerTimeSignature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedBeats = when (position) {
                    0 -> 2 // 2/4
                    1 -> 3 // 3/4
                    else -> 4 // 4/4
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
            binding.textViewRemainingTime.text = if (remaining > 0) "남은 시간: $remaining 초" else "중지됨"
            updateProgressBar(remaining, viewModel.intervalSeconds.value ?: 3)
        }

        viewModel.numbers.observe(viewLifecycleOwner) { nums ->
            setRandomNumbersAndImages(nums)
        }

        viewModel.intervalSeconds.observe(viewLifecycleOwner) {
            if (viewModel.remainingSeconds.value == null || viewModel.remainingSeconds.value == 0) {
                binding.textViewRemainingTime.text = "남은 시간: -"
                binding.progressBarTimer.progress = 0
            }
        }
    }

    private fun updateButtonText(running: Boolean) {
        binding.buttonStartStop.text = if (running) getString(R.string.stop) else getString(R.string.start)
    }

    private fun setViewsVisible(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        listOf(binding.image1, binding.image2, binding.image3, binding.image4).forEach {
            it.visibility = visibility
        }
        updateImageVisibility()
    }

    // 박자 수에 따라 이미지 표시 개수 조절
    private fun updateImageVisibility() {
        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)
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
        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)
        for (i in nums.indices) {
            if (i >= selectedBeats) break // 선택된 박자 수까지만 표시
            val num = nums[i]
            val imageResId = resources.getIdentifier("beat$num", "drawable", requireContext().packageName)
            if (imageResId != 0) imageViews[i].setImageResource(imageResId)
            else imageViews[i].setImageDrawable(null)
        }
    }

    private fun setScaleTypeByOrientation() {
        val scaleType = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            ImageView.ScaleType.FIT_XY else ImageView.ScaleType.FIT_CENTER

        listOf(binding.image1, binding.image2, binding.image3, binding.image4).forEach {
            it.scaleType = scaleType
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
