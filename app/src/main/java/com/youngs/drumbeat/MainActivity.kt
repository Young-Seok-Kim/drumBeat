package com.youngs.drumbeat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.youngs.drumbeat.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val handler = Handler(Looper.getMainLooper())
    private var intervalSeconds: Int = 3
    private var remainingTimeSeconds: Int = 0
    private var isRunning = false // 실행 중인지 상태 체크용

    private val updateRunnable = object : Runnable {
        override fun run() {
            setRandomNumbersAndImages()
            remainingTimeSeconds = intervalSeconds
            updateRemainingTimeText()
            updateProgressBar()

            handler.postDelayed(countdownRunnable, 1000L)
            handler.postDelayed(this, intervalSeconds * 1000L)
        }
    }

    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (remainingTimeSeconds > 0 && isRunning) {
                remainingTimeSeconds--
                updateRemainingTimeText()
                updateProgressBar()
                handler.postDelayed(this, 1000L)
            } else if(remainingTimeSeconds <= 0) {
                binding.textViewRemainingTime.text = "남은 시간: 0초"
                binding.progressBarTimer?.progress = 0
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewRemainingTime.text = "남은 시간: -"
        binding.progressBarTimer?.progress = 100

        // 시작 버튼 클릭
        binding.buttonStart.setOnClickListener {
            val timeText = binding.editTextTime.text.toString()
            val seconds = timeText.toIntOrNull()

            if (seconds == null || seconds !in 1..10) {
                Toast.makeText(this, "시간을 1초에서 10초 사이로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRunning) {
                Toast.makeText(this, "이미 실행 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            intervalSeconds = seconds
            isRunning = true

            setRandomNumbersAndImages()

            remainingTimeSeconds = intervalSeconds
            updateRemainingTimeText()
            updateProgressBar()

            handler.postDelayed(countdownRunnable, 1000L)
            handler.postDelayed(updateRunnable, intervalSeconds * 1000L)

            Toast.makeText(this, "$intervalSeconds 초마다 숫자와 이미지가 갱신됩니다.", Toast.LENGTH_SHORT).show()
        }

        // 중지 버튼 클릭
        binding.buttonStop.setOnClickListener {
            if (!isRunning) {
                Toast.makeText(this, "동작 중이 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            isRunning = false
            handler.removeCallbacks(updateRunnable)
            handler.removeCallbacks(countdownRunnable)
            binding.textViewRemainingTime.text = "중지됨"
            binding.progressBarTimer?.progress = 0
            Toast.makeText(this, "동작이 중지되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateRemainingTimeText() {
        binding.textViewRemainingTime.text = "남은 시간: $remainingTimeSeconds 초"
    }

    private fun updateProgressBar() {
        binding.progressBarTimer?.let { progressBar ->
            val progressPercent = (remainingTimeSeconds.toFloat() / intervalSeconds.toFloat()) * 100
            progressBar.progress = progressPercent.toInt()
        }
    }

    private fun setRandomNumbersAndImages() {
        val numbers = List(4) { (1..16).random() }  // 1~16 랜덤

        val numberTextViews = listOf(binding.number1, binding.number2, binding.number3, binding.number4)
        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)

        for (i in numbers.indices) {
            val num = numbers[i]
            numberTextViews[i].text = num.toString()

            val imageResId = resources.getIdentifier("beat$num", "drawable", packageName)
            if (imageResId != 0) {
                imageViews[i].setImageResource(imageResId)
            } else {
                imageViews[i].setImageDrawable(null)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        handler.removeCallbacks(countdownRunnable)
    }
}