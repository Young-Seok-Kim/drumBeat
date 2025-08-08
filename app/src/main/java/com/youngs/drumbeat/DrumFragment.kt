package com.youngs.drumbeat

import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.youngs.drumbeat.databinding.FragmentDrumBinding

class DrumFragment : Fragment() {

    private var _binding: FragmentDrumBinding? = null
    private val binding get() = _binding!!

    private var intervalSeconds: Int = 3
    private var isRunning = false
    private var countDownTimer: CountDownTimer? = null
    private var remainingSeconds: Int = 0
    private var numbers = listOf(1, 1, 1, 1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDrumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 복원 처리
        if (savedInstanceState != null) {
            intervalSeconds = savedInstanceState.getInt("intervalSeconds", 3)
            isRunning = savedInstanceState.getBoolean("isRunning", false)
            remainingSeconds = savedInstanceState.getInt("remainingSeconds", intervalSeconds)
            numbers = savedInstanceState.getIntegerArrayList("numbers")?.toList() ?: List(4) { 1 }
        } else {
            remainingSeconds = intervalSeconds
            numbers = List(4) { (1..16).random() }
        }

        setScaleTypeByOrientation()
        setListeners()

        if (isRunning) {
            setRandomNumbersAndImages(numbers)
            startTimer(remainingSeconds)
        } else {
            stopTimer()
            setRandomNumbersAndImages(numbers)
        }
    }

    private fun setScaleTypeByOrientation() {
        val scaleType = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ImageView.ScaleType.FIT_XY
        } else {
            ImageView.ScaleType.FIT_CENTER
        }

        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)
        for (imageView in imageViews) {
            imageView.scaleType = scaleType
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setScaleTypeByOrientation()
    }

    private fun setListeners() {
        binding.buttonStartStop.setOnClickListener {
            if (isRunning) {
                stopTimer()
                Toast.makeText(requireContext(), "동작이 중지되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val timeText = binding.editTextTime.text.toString()
                val seconds = timeText.toIntOrNull()
                if (seconds == null || seconds !in 1..10) {
                    Toast.makeText(requireContext(), "시간을 1초에서 10초 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                intervalSeconds = seconds
                remainingSeconds = seconds
                isRunning = true
                numbers = List(4) { (1..16).random() }
                setRandomNumbersAndImages(numbers)
                startTimer(remainingSeconds)
                Toast.makeText(requireContext(), "$intervalSeconds 초마다 숫자와 이미지가 갱신됩니다.", Toast.LENGTH_SHORT).show()
            }
            updateButtonText()
        }
    }

    private fun updateButtonText() {
        binding.buttonStartStop.text = if (isRunning) "종료" else "시작"
    }

    private fun startTimer(startSeconds: Int = intervalSeconds) {
        setViewsVisible(true)
        setRandomNumbersAndImages(numbers)
        updateRemainingTimeText(startSeconds)
        updateProgressBar(startSeconds, intervalSeconds)
        updateButtonText()

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((startSeconds * 1000).toLong(), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                remainingSeconds = ((millisUntilFinished + 999) / 1000).toInt()
                updateRemainingTimeText(remainingSeconds)
                updateProgressBar(remainingSeconds, intervalSeconds)
            }

            override fun onFinish() {
                updateRemainingTimeText(0)
                updateProgressBar(0, intervalSeconds)
                if (isRunning) {
                    numbers = List(4) { (1..16).random() }
                    setRandomNumbersAndImages(numbers)
                    remainingSeconds = intervalSeconds
                    startTimer(remainingSeconds)
                }
            }
        }.start()
    }

    private fun stopTimer() {
        isRunning = false
        countDownTimer?.cancel()
        binding.textViewRemainingTime.text = "중지됨"
        binding.progressBarTimer.progress = 0
        setViewsVisible(false)
        updateButtonText()
    }

    private fun updateRemainingTimeText(remainingSec: Int) {
        binding.textViewRemainingTime.text = "남은 시간: $remainingSec 초"
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
            val num = nums[i]
            val imageResId = resources.getIdentifier("beat$num", "drawable", requireContext().packageName)
            if (imageResId != 0) {
                imageViews[i].setImageResource(imageResId)
            } else {
                imageViews[i].setImageDrawable(null)
            }
        }
    }

    private fun setViewsVisible(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)
        imageViews.forEach { it.visibility = visibility }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("intervalSeconds", intervalSeconds)
        outState.putBoolean("isRunning", isRunning)
        outState.putInt("remainingSeconds", remainingSeconds)
        outState.putIntegerArrayList("numbers", ArrayList(numbers))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
