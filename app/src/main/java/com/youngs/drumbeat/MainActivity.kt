package com.youngs.drumbeat

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.youngs.drumbeat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var intervalSeconds: Int = 3
    private var isRunning = false // 실행 중인지 상태 체크용

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewRemainingTime.text = "남은 시간: -"
        binding.progressBarTimer.progress = 100
        setListener()
        stopTimer()
    }

    private fun setListener() {
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

            startTimer()

            Toast.makeText(this, "$intervalSeconds 초마다 숫자와 이미지가 갱신됩니다.", Toast.LENGTH_SHORT).show()
        }

        binding.buttonStop.setOnClickListener {
            if (!isRunning) {
                Toast.makeText(this, "동작 중이 아닙니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            stopTimer()
            Toast.makeText(this, "동작이 중지되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer() {
        // 첫 갱신 즉시 실행
        setViewsVisible(true)
        setRandomNumbersAndImages()
        updateRemainingTimeText(intervalSeconds)
        updateProgressBar(intervalSeconds, intervalSeconds)

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((intervalSeconds * 1000).toLong(), 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSec = ((millisUntilFinished + 999) / 1000).toInt()  // 올림 처리
                updateRemainingTimeText(remainingSec)
                updateProgressBar(remainingSec, intervalSeconds)
            }

            override fun onFinish() {
                updateRemainingTimeText(0)
                updateProgressBar(0, intervalSeconds)
                if (isRunning) {
                    setRandomNumbersAndImages()
                    startTimer()
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

    private fun setViewsVisible(visible: Boolean) {
        val visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE

        val numberTextViews = listOf(binding.number1, binding.number2, binding.number3, binding.number4)
        val imageViews = listOf(binding.image1, binding.image2, binding.image3, binding.image4)

        numberTextViews.forEach { it.visibility = visibility }
        imageViews.forEach { it.visibility = visibility }
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
